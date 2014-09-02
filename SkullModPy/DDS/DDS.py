import os
from SkullModPy.common import ImageWriter

from SkullModPy.common.CommonConstants import *
from SkullModPy.common.Reader import Reader
from SkullModPy.DDS.Constants import *

from multiprocessing import Pool

FILE_IDENTIFIER = b'DDS '
FILE_EXTENSION = "dds"


def dxt5_block(data_in):
    def parse_rgb565(packed):
        r = (packed >> 11) & 0x1F
        g = (packed >> 5) & 0x3F
        b = packed & 0x1F
        return [(r << 3) | (r >> 2), (g << 2) | (g >> 4), (b << 3) | (b >> 2)]

    import struct

    alpha = [0]*8
    color = [[0, 0, 0]]*4
    alpha_indices = [0]*16
    color_indices = [0]*16
    # Read alpha values and indices
    alpha[0] = data_in[0]
    alpha[1] = data_in[1]
    # See http://msdn.microsoft.com/en-us/library/windows/desktop/bb694531%28v=vs.85%29.aspx#BC3 for ordering
    # Orders from a to p
    alpha_indices_raw1 = struct.unpack('<I', b'\00' + data_in[2:5])[0]
    alpha_indices_raw2 = struct.unpack('<I', b'\00' + data_in[5:8])[0]
    for i in range(0, 8):
        mask = 2 ** (i*3) + 2 ** (i*3+1) + 2**(i*3+2)
        alpha_indices[i] = (alpha_indices_raw1 & mask) >> i*3
        alpha_indices[i+8] = (alpha_indices_raw2 & mask) >> i*3
    # Calculate the missing alpha values
    if alpha[0] > alpha[1]:
        alpha[2] = (6*alpha[0] + 1*alpha[1]) // 7
        alpha[3] = (5*alpha[0] + 2*alpha[1]) // 7
        alpha[4] = (4*alpha[0] + 3*alpha[1]) // 7
        alpha[5] = (3*alpha[0] + 4*alpha[1]) // 7
        alpha[6] = (2*alpha[0] + 5*alpha[1]) // 7
        alpha[7] = (1*alpha[0] + 6*alpha[1]) // 7
    else:
        alpha[2] = (4*alpha[0] + 1*alpha[1]) // 5
        alpha[3] = (3*alpha[0] + 2*alpha[1]) // 5
        alpha[4] = (2*alpha[0] + 3*alpha[1]) // 5
        alpha[5] = (1*alpha[0] + 4*alpha[1]) // 5
        alpha[6] = 0
        alpha[7] = 255
    # Read colors and color indices
    color[0] = parse_rgb565(struct.unpack('<H', data_in[8:10])[0])
    color[1] = parse_rgb565(struct.unpack('<H', data_in[10:12])[0])
    # See http://msdn.microsoft.com/en-us/library/windows/desktop/bb694531%28v=vs.85%29.aspx#BC3 for ordering
    # Orders from a to p
    color_indices_raw = struct.unpack('bbbb', data_in[12:16])
    for i in range(0, 4):
        mask = 2 ** (i*2) + 2 ** (i*2+1)
        color_indices[i] = (color_indices_raw[0] & mask) >> i*2
        color_indices[i + 4] = (color_indices_raw[1] & mask) >> i*2
        color_indices[i + 8] = (color_indices_raw[2] & mask) >> i*2
        color_indices[i + 12] = (color_indices_raw[3] & mask) >> i*2

    # Calculate the missing color values
    color[2] = [int((2*color[0][i] + color[1][i])/3) for i in range(0, 3)]
    color[3] = [int((2*color[1][i] + color[0][i])/3) for i in range(0, 3)]

    result = [[0, 0, 0, 0]]*16
    for i in range(0, 16):
        result[i][0] = color[color_indices[i]][0]
        result[i][1] = color[color_indices[i]][1]
        result[i][2] = color[color_indices[i]][2]
        result[i][3] = alpha[alpha_indices[i]]

    return result

class DDS(Reader):
    """
    Source:
    http://www.matejtomcik.com/Public/KnowHow/DXTDecompression/
    http://msdn.microsoft.com/en-us/library/windows/desktop/bb943991%28v=vs.85%29.aspx
    """
    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), LITTLE_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def export_png(self):
        # FOURCC check
        if self.file.read(4) != FILE_IDENTIFIER:
            raise ValueError("Not a valid DDS file")

        # === HEADER ===
        if self.read_int() != 124:
            raise ValueError("Not a valid DDS header")

        dds_flags = self.read_int()

        dds_has_caps = True if dds_flags & DDS_CAPS_FLAG else False
        dds_has_height = True if dds_flags & DDS_HEIGHT_FLAG else False
        dds_has_width = True if dds_flags & DDS_WIDTH_FLAG else False
        dds_has_pitch = True if dds_flags & DDS_PITCH_FLAG else False
        dds_has_pixelformat = True if dds_flags & DDS_PIXELFORMAT_FLAG else False
        dds_has_mipmapcount = True if dds_flags & DDS_MIPMAPCOUNT_FLAG else False
        dds_has_depth = True if dds_flags & DDS_DEPTH_FLAG else False

        if dds_flags & ~(DDS_CAPS_FLAG | DDS_HEIGHT_FLAG | DDS_WIDTH_FLAG | DDS_PITCH_FLAG |
                         DDS_PIXELFORMAT_FLAG | DDS_MIPMAPCOUNT_FLAG | DDS_DEPTH_FLAG) != 0:
            print("Info: An unknown bit is set in dds_flags, this is common")

        # TODO check all flags for validity of dds_flags (and other combinations as well) and the vars below as well

        dds_height = self.read_int()
        dds_width = self.read_int()
        dds_pitch_or_linear_size = self.read_int()
        dds_depth = self.read_int()
        dds_mipmapcount = self.read_int()

        # 11 * 4 bytes of reserved fields
        dds_reserved1 = self.file.read(11 * 4)
        if dds_reserved1[36:40] == NVTT:
            # See src/nvimage/DirectDrawSurface.cpp of NVTT for details
            print("Info: DDS was made with Nvidia texture tools version " + str(dds_reserved1[42]) + "." +
                  str(dds_reserved1[41]) + "." + str(dds_reserved1[40]))

        # DDS Pixelformat
        if self.read_int() != 32:
            raise ValueError("DDS Pixelformat has an invalid length")
        pixelformat_flags = self.read_int()

        ddsf_has_alphapixels = True if pixelformat_flags & DDSF_ALPHAPIXELS else False
        ddsf_has_alpha = True if pixelformat_flags & DDSF_ALPHA else False
        ddsf_has_fourcc = True if pixelformat_flags & DDSF_FOURCC else False
        ddsf_has_rgb = True if pixelformat_flags & DDSF_RGB else False
        ddsf_has_yuv = True if pixelformat_flags & DDSF_YUV else False
        ddsf_has_luminance = True if pixelformat_flags & DDSF_LUMINANCE else False

        # Valid when dds_has_fourcc = True
        dds_fourcc = str(self.file.read(4), encoding='ascii')

        if dds_fourcc == 'DX10':
            print("Warning: Detected Direct X 10 dds, parsing will stop after normal header")

        ddsf_bitcount = self.read_int()
        ddsf_r_bitmask = self.read_int()
        ddsf_g_bitmask = self.read_int()
        ddsf_b_bitmask = self.read_int()

        if dds_fourcc == 'DX10':
            raise ValueError("Direct X 10 headers are not supported yet")

        # DDS Header again
        dds_caps1 = self.read_int()
        dds_caps2 = self.read_int()
        dds_caps3 = self.read_int()
        dds_caps4 = self.read_int()
        dds_reserved2 = self.read_int()

        relative_pointer = self.read_int()  # Relative pointer to imagedata? TODO check

        if dds_fourcc != 'DXT5':
            raise ValueError("Anything other than dxt5 not implemented yet")
        # Start reading
        # Image height has to be a multiple of 4
        image_height = dds_height + dds_height % 4
        y_blocks = image_height // 4
        image_width = dds_width + dds_width % 4
        x_blocks = image_width // 4

        # Filled with rgba data
        output = bytearray(4*image_height*image_width)


        ######################################

        data_in = [[self.file.read(16)] for _ in range(0, x_blocks*y_blocks)]


        with Pool() as test_pool:
            result = test_pool.starmap_async(dxt5_block, data_in)
            res = result.get()

        import itertools
        merged = list(itertools.chain(*res))
        merged2 = list(itertools.chain(*merged))
        print(merged2)
        return ImageWriter.write_png(bytearray(merged2), image_width, image_height)

    """
        alpha = [0]*8
        color = [[0, 0, 0]]*4
        for y in range(0, y_blocks):
            for x in range(0, x_blocks):
                alpha[0] = self.read_int(1)
                alpha[1] = self.read_int(1)
                alpha_indices = self.file.read(6)
                color[0] = self.read_rgb565()
                color[1] = self.read_rgb565()
                color_indices = self.file.read(4)
                # Calculate the missing color values
                color[2] = [int((2*color[0][i] + color[1][i])/3) for i in range(0, 3)]
                color[3] = [int((2*color[1][i] + color[0][i])/3) for i in range(0, 3)]
                # Calculate the missing alpha values
                if alpha[0] > alpha[1]:
                    alpha[2] = (6 * alpha[0] + 1 * alpha[1]) // 7
                    alpha[3] = (5 * alpha[0] + 2 * alpha[1]) // 7
                    alpha[4] = (4 * alpha[0] + 3 * alpha[1]) // 7
                    alpha[5] = (3 * alpha[0] + 4 * alpha[1]) // 7
                    alpha[6] = (2 * alpha[0] + 5 * alpha[1]) // 7
                    alpha[7] = (1 * alpha[0] + 6 * alpha[1]) // 7
                else:
                    alpha[2] = (4 * alpha[0] + 1 * alpha[1]) // 5
                    alpha[3] = (3 * alpha[0] + 2 * alpha[1]) // 5
                    alpha[4] = (2 * alpha[0] + 3 * alpha[1]) // 5
                    alpha[5] = (1 * alpha[0] + 4 * alpha[1]) // 5
                    alpha[6] = 0
                    alpha[7] = 255

                # Now that both tables are complete write the data in an array to pass to the png function
                # Don't forget that for now the actual size (dds_ width and height)is ignored and the next 4 pixel boundary is used
                # TODO change that

                for i in range(0, 16):


                    pixel_alpha = alpha[self.bits_to_int(alpha_indices, 45-i*3, 3)]
                    #pixel_color = color[self.bits_to_int(color_indices, 30-i*2, 2)]
                    pixel_y_pos = y*4 + 3-(i // 4)
                    pixel_x_pos = x*4 + (i % 4)  # Horizontal flip

                    # DEV: If no flipping of the file is needed use:
                    # pos_in_output = pixel_y_pos*4*image_width + pixel_x_pos*4 # No flip
                    # pos_in_output = (image_width*image_height*4)-(pixel_y_pos*4*image_width + pixel_x_pos*4)-4 # Y Flip
                    # pos_in_output = pixel_y_pos*4*image_width + (image_width - pixel_x_pos)*4 # X Flip
                    pos_in_output = (image_width*image_height*4)-(pixel_y_pos*4*image_width + (image_width-pixel_x_pos)*4)-4

                    #output[pos_in_output] = pixel_color[0]
                    #output[pos_in_output+1] = pixel_color[1]
                    #output[pos_in_output+2] = pixel_color[2]
                    #output[pos_in_output+3] = pixel_alpha

                    alpha_index = 9999
                    if i<4:
                        alpha_index = self.bits_to_int(alpha_indices, 33-(i % 4)*3, 3)
                    elif i>=4 and i<8:
                        alpha_index = self.bits_to_int(alpha_indices, 45-(i % 4)*3, 3)
                    elif i>=8 and i<12:
                        alpha_index = self.bits_to_int(alpha_indices, 9-(i % 4)*3, 3)
                    elif i>=12 and i<16:
                        alpha_index = self.bits_to_int(alpha_indices, 21-(i % 4)*3, 3)



                    new_alpha_value = alpha[alpha_index]
                    output[pos_in_output] = new_alpha_value
                    output[pos_in_output+1] = new_alpha_value
                    output[pos_in_output+2] = new_alpha_value
                    output[pos_in_output+3] = 255



    #    return ImageWriter.write_png(bytearray(output), image_width, image_height)

    def read_rgb565(self):
        packed = self.read_int(2)
        r = (packed >> 11) & 0x1F
        g = (packed >> 5) & 0x3F
        b = packed & 0x1F

        r = (r << 3) | (r >> 2)
        g = (g << 2) | (g >> 4)
        b = (b << 3) | (b >> 2)

        return [r, g, b]
          """
#def decode_block(data_in, in_offset, data_out, out_offset, y_flip=True):

"""
Plan:

Make output bytearray
make pool

Open input file (read metadata)
for each block
    read block
    new process with read data
wait for last thread

"""