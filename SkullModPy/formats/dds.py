import os
from SkullModPy.common.CommonConstants import LITTLE_ENDIAN
from SkullModPy.common.Reader import Reader
from SkullModPy.common.helper import *  # includes struct and math
from SkullModPy.formats.png import PNGWriter


class DDSReader(Reader):
    """
    Source:
    http://www.matejtomcik.com/Public/KnowHow/DXTDecompression/
    http://msdn.microsoft.com/en-us/library/windows/desktop/bb943991%28v=vs.85%29.aspx
    """

    # DDS FOURCC
    DDS_MAGIC = bytes('DDS ', 'ascii')
    # dwSize
    DDS_HEADER_SIZE = 124
    # dwFlags (Surface description flags)
    # TODO make it constants (lazy, lazy)
    DDS_CAPS_FLAG = 2 ** 0  # Bit 1
    DDS_HEIGHT_FLAG = 2 ** 1  # Bit 2
    DDS_WIDTH_FLAG = 2 ** 2  # Bit 3
    DDS_PITCH_FLAG = 2 ** 3  # Bit 4
    DDS_PIXELFORMAT_FLAG = 2 ** 12  # Bit 13
    DDS_MIPMAPCOUNT_FLAG = 2 ** 17  # Bit 18
    DDS_LINEARSIZE_FLAG = 2 ** 19  # Bit 20
    DDS_DEPTH_FLAG = 2 ** 23  # Bit 24

    # dwReserved (Reserved fields, additional data)
    # If 9th int is this string the file was compressed with the Nvidia Texture Tools (NVTT)
    NVTT = bytes('NVTT', 'ascii')

    # dwFlags (Pixel format flags)
    DDSF_ALPHAPIXELS = 2 ** 0  # Bit 1
    DDSF_ALPHA = 2 ** 1  # Bit 2
    DDSF_FOURCC = 2 ** 2  # Bit 3
    DDSF_RGB = 2 ** 6  # Bit 7
    DDSF_YUV = 2 ** 9  # Bit 10
    DDSF_LUMINANCE = 2 ** 17  # Bit 18

    # dwCaps flags
    DDSCAPS_COMPLEX = 8
    DDSCAPS_MIPMAP = 0x400000
    DDSCAPS_TEXTURE = 0x1000

    # dwCaps2 flags
    DDSCAPS2_CUBEMAP = 0x200
    DDSCAPS2_CUBEMAP_POSITIVEX = 0x400
    DDSCAPS2_CUBEMAP_NEGATIVEX = 0x800
    DDSCAPS2_CUBEMAP_POSITIVEY = 0x1000
    DDSCAPS2_CUBEMAP_NEGATIVEY = 0x2000
    DDSCAPS2_CUBEMAP_POSITIVEZ = 0x4000
    DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x8000
    DDSCAPS2_VOLUME = 0x200000

    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), LITTLE_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def check_destination(self):
        png_path = os.path.splitext(self.file_path)[0] + '.png'
        if os.path.exists(png_path) and os.path.isfile(png_path):
            print("Found a file at given path, will be overwritten")
        if os.path.exists(png_path) and not os.path.isfile(png_path):
            raise FileExistsError("Can not create dds file, there is a folder in the way with the same name")

    def get_png_data(self):
        # FOURCC check
        if self.file.read(4) != DDSReader.DDS_MAGIC:
            raise ValueError("Not a valid DDS file")

        # === HEADER ===
        if self.read_int() != 124:
            raise ValueError("Not a valid DDS header")

        dds_flags = self.read_int()

        dds_has_caps = True if dds_flags & DDSReader.DDS_CAPS_FLAG else False
        dds_has_height = True if dds_flags & DDSReader.DDS_HEIGHT_FLAG else False
        dds_has_width = True if dds_flags & DDSReader.DDS_WIDTH_FLAG else False
        dds_has_pitch = True if dds_flags & DDSReader.DDS_PITCH_FLAG else False
        dds_has_pixelformat = True if dds_flags & DDSReader.DDS_PIXELFORMAT_FLAG else False
        dds_has_mipmapcount = True if dds_flags & DDSReader.DDS_MIPMAPCOUNT_FLAG else False
        dds_has_depth = True if dds_flags & DDSReader.DDS_DEPTH_FLAG else False

        if dds_flags & ~(DDSReader.DDS_CAPS_FLAG | DDSReader.DDS_HEIGHT_FLAG | DDSReader.DDS_WIDTH_FLAG |
                             DDSReader.DDS_PITCH_FLAG | DDSReader.DDS_PIXELFORMAT_FLAG |
                             DDSReader.DDS_MIPMAPCOUNT_FLAG | DDSReader.DDS_DEPTH_FLAG) != 0:
            print("Info: An unknown bit is set in dds_flags, this is common")

        # TODO check all flags for validity of dds_flags (and other combinations as well) and the vars below as well

        dds_height = self.read_int()
        dds_width = self.read_int()
        dds_pitch_or_linear_size = self.read_int()
        dds_depth = self.read_int()
        dds_mipmapcount = self.read_int()

        # 11 * 4 bytes of reserved fields
        dds_reserved1 = self.file.read(11 * 4)
        if dds_reserved1[36:40] == DDSReader.NVTT:
            # See src/nvimage/DirectDrawSurface.cpp of NVTT for details
            print("Info: DDS was made with Nvidia texture tools version " + str(dds_reserved1[42]) + "." +
                  str(dds_reserved1[41]) + "." + str(dds_reserved1[40]))

        # DDS Pixelformat
        if self.read_int() != 32:
            raise ValueError("DDS Pixelformat has an invalid length")
        pixelformat_flags = self.read_int()

        ddsf_has_alphapixels = True if pixelformat_flags & DDSReader.DDSF_ALPHAPIXELS else False
        ddsf_has_alpha = True if pixelformat_flags & DDSReader.DDSF_ALPHA else False
        ddsf_has_fourcc = True if pixelformat_flags & DDSReader.DDSF_FOURCC else False
        ddsf_has_rgb = True if pixelformat_flags & DDSReader.DDSF_RGB else False
        ddsf_has_yuv = True if pixelformat_flags & DDSReader.DDSF_YUV else False
        ddsf_has_luminance = True if pixelformat_flags & DDSReader.DDSF_LUMINANCE else False

        # Valid when dds_has_fourcc = True
        dds_fourcc = str(self.file.read(4), encoding='ascii')

        if dds_fourcc == 'DX10':
            print("Warning: Detected Direct X 10 dds, parsing will stop after normal header")

        ddsf_bitcount = self.read_int()
        ddsf_r_bitmask = self.read_int()
        ddsf_g_bitmask = self.read_int()
        ddsf_b_bitmask = self.read_int()
        ddsf_a_bitmask = self.read_int()

        if dds_fourcc == 'DX10':
            raise ValueError("Direct X 10 headers are not supported yet")

        # DDS Header again
        dds_caps1 = self.read_int()
        dds_caps2 = self.read_int()
        dds_caps3 = self.read_int()
        dds_caps4 = self.read_int()
        dds_reserved2 = self.read_int()

        # Start reading
        # Image height has to be a multiple of 4 for DXT1/3/5, ignored for anything else
        image_height = dds_height if dds_height % 4 == 0 else dds_height + 4 - (dds_height % 4)
        image_width = dds_width if dds_width % 4 == 0 else dds_width + 4 - (dds_width % 4)

        y_blocks = image_height // 4
        x_blocks = image_width // 4

        if dds_fourcc == 'DXT1' or dds_fourcc == 'DXT3' or dds_fourcc == 'DXT5':
            # Array of pixeldata, packed abgr8 is used because no conversion is needed for png
            image_data = [[0] * image_width for _ in range(image_height)]
        else:
            # Assuming uncompressed argb data
            image_data = [[0] * dds_width for _ in range(dds_height)]

        if dds_fourcc == 'DXT5':
            for block in range(x_blocks * y_blocks):  # For each block
                a = [0 for _ in range(8)]
                c = [0 for _ in range(4)]

                a[0] = self.read_int(1)  # Alpha 0, 1 Byte
                a[1] = self.read_int(1)  # Alpha 1, 1 Byte

                # alpha indices (6 Bytes(16*3 bit)), swap each 3 bytes
                alpha_indices_raw = self.file.read(6)
                alpha_indices_raw = bytes([alpha_indices_raw[2], alpha_indices_raw[1], alpha_indices_raw[0],
                                           alpha_indices_raw[5], alpha_indices_raw[4], alpha_indices_raw[3]])
                # Get alpha indices
                alpha_indices = get_bits_array(alpha_indices_raw, 3)
                # Order is: hgfedcba ponmlkji, reorder to abcdefgh ijklmnop
                alpha_indices = [alpha_indices[((i // 8) * 8) + (7 - (i % 8))] for i in range(len(alpha_indices))]

                # c0 2 Bytes
                c[0] = struct.unpack('<H', self.file.read(2))[0]
                # c1 2 Bytes
                c[1] = struct.unpack('<H', self.file.read(2))[0]
                # Get and split color indices (16*2 Bit)
                color_indices = get_bits_array(self.file.read(4), 2)
                # Order: dcba hgfe lkji ponm reorder: to a-p
                color_indices = [color_indices[((i // 4) * 4) + (3 - (i % 4))] for i in range(len(color_indices))]

                # Calculate: Color 2 and 3
                c0_split = rgb565_split(c[0])
                c1_split = rgb565_split(c[1])

                c[2] = rgb565_to_abgr8(
                    rgb565(int(math.floor(((2.0 * c0_split['r'] + c1_split['r']) / 3.0) + 0.5)),
                           int(math.floor(((2.0 * c0_split['g'] + c1_split['g']) / 3.0) + 0.5)),
                           int(math.floor(((2.0 * c0_split['b'] + c1_split['b']) / 3.0) + 0.5)))
                )
                c[3] = rgb565_to_abgr8(
                    rgb565(int(math.floor(((2.0 * c1_split['r'] + c0_split['r']) / 3.0) + 0.5)),
                           int(math.floor(((2.0 * c1_split['g'] + c0_split['g']) / 3.0) + 0.5)),
                           int(math.floor(((2.0 * c1_split['b'] + c0_split['b']) / 3.0) + 0.5)))
                )

                c[0] = rgb565_to_abgr8(c[0])
                c[1] = rgb565_to_abgr8(c[1])

                # Calculate: a2-a7
                if a[0] > a[1]:
                    a[2] = (6 * a[0] + 1 * a[1]) // 7
                    a[3] = (5 * a[0] + 2 * a[1]) // 7
                    a[4] = (4 * a[0] + 3 * a[1]) // 7
                    a[5] = (3 * a[0] + 4 * a[1]) // 7
                    a[6] = (2 * a[0] + 5 * a[1]) // 7
                    a[7] = (1 * a[0] + 6 * a[1]) // 7
                else:
                    a[2] = (4 * a[0] + 1 * a[1]) // 5
                    a[3] = (3 * a[0] + 2 * a[1]) // 5
                    a[4] = (2 * a[0] + 3 * a[1]) // 5
                    a[5] = (1 * a[0] + 4 * a[1]) // 5
                    a[6] = 0
                    a[7] = 255

                for y_block_pos in range(4):
                    for x_block_pos in range(4):
                        x_pos = (block // (image_width // 4) * 4) + y_block_pos
                        y_pos = ((block * 4) % image_width) + x_block_pos
                        color_index = color_indices[y_block_pos * 4 + x_block_pos]
                        alpha_index = alpha_indices[y_block_pos * 4 + x_block_pos]
                        # Beware: x and y are flipped, image_data[y][x] is written
                        image_data[x_pos][y_pos] = merge_abgr8(a[alpha_index], c[color_index])
        elif dds_fourcc == 'DXT3':
            for block in range(x_blocks * y_blocks):  # For each block
                # Get alpha values reorder to little endian
                alpha_raw = self.file.read(8)
                alpha_raw = bytes([alpha_raw[1], alpha_raw[0],
                                   alpha_raw[3], alpha_raw[2],
                                   alpha_raw[5], alpha_raw[4],
                                   alpha_raw[7], alpha_raw[6]])
                # Get alpha bytes
                a = get_bits_array(alpha_raw, 4)
                # Order is: hgfedcba ponmlkji, reorder to abcdefgh ijklmnop
                a = [a[((i // 8) * 8) + (7 - (i % 8))] for i in range(len(a))]
                # Expand alpha to 8 bit
                a = [int(math.floor(a[i] * 255.0 / 15.0 + 0.5)) for i in range(len(a))]
                # Get color values
                c = [0 for _ in range(4)]
                # c0 2 Bytes
                c[0] = rgb565_to_abgr8(struct.unpack('<H', self.file.read(2))[0])
                # c1 2 Bytes
                c[1] = rgb565_to_abgr8(struct.unpack('<H', self.file.read(2))[0])
                # Get and split color indices (16*2 Bit)
                color_indices = get_bits_array(self.file.read(4), 2)
                # Order: dcba hgfe lkji ponm reorder: to a-p
                color_indices = [color_indices[((i // 4) * 4) + (3 - (i % 4))] for i in range(len(color_indices))]
                # Calculate: Color 2 and 3
                c[2] = abgr8(
                    int(math.floor(((2.0 * get_channel_abgr8(c[0], 'r') + get_channel_abgr8(c[1], 'r')) / 3.0) + 0.5)),
                    int(math.floor(((2.0 * get_channel_abgr8(c[0], 'g') + get_channel_abgr8(c[1], 'g')) / 3.0) + 0.5)),
                    int(math.floor(((2.0 * get_channel_abgr8(c[0], 'b') + get_channel_abgr8(c[1], 'b')) / 3.0) + 0.5)),
                    255)
                c[3] = abgr8(
                    int(math.floor(((2.0 * get_channel_abgr8(c[1], 'r') + get_channel_abgr8(c[0], 'r')) / 3.0) + 0.5)),
                    int(math.floor(((2.0 * get_channel_abgr8(c[1], 'g') + get_channel_abgr8(c[0], 'g')) / 3.0) + 0.5)),
                    int(math.floor(((2.0 * get_channel_abgr8(c[1], 'b') + get_channel_abgr8(c[0], 'b')) / 3.0) + 0.5)),
                    255)
                # Write
                for y_block_pos in range(4):
                    for x_block_pos in range(4):
                        x_pos = (block // (image_width // 4) * 4) + y_block_pos
                        y_pos = ((block * 4) % image_width) + x_block_pos
                        color_index = color_indices[y_block_pos * 4 + x_block_pos]
                        alpha_value = a[y_block_pos * 4 + x_block_pos]

                        # Beware: x and y are flipped, image_data[y][x] is written
                        image_data[x_pos][y_pos] = merge_abgr8(alpha_value, c[color_index])
        elif dds_fourcc == 'DXT1':
            for block in range(x_blocks * y_blocks):  # For each block
                c = [0 for _ in range(4)]
                # c0 2 Bytes
                c0_raw = struct.unpack('<H', self.file.read(2))[0]
                c[0] = rgb565_to_abgr8(c0_raw)
                # c1 2 Bytes
                c1_raw = struct.unpack('<H', self.file.read(2))[0]
                c[1] = rgb565_to_abgr8(c1_raw)
                # Get and split color indices (16*2 Bit)
                color_indices = get_bits_array(self.file.read(4), 2)
                # Order: dcba hgfe lkji ponm reorder: to a-p
                color_indices = [color_indices[((i // 4) * 4) + (3 - (i % 4))] for i in range(len(color_indices))]

                # Calculate: Color 2 and 3
                if c0_raw > c1_raw:
                    c[2] = abgr8((2.0 * get_channel_abgr8(c[0], 'r') + get_channel_abgr8(c[1], 'r')) / 3.0,
                                 (2.0 * get_channel_abgr8(c[0], 'g') + get_channel_abgr8(c[1], 'g')) / 3.0,
                                 (2.0 * get_channel_abgr8(c[0], 'b') + get_channel_abgr8(c[1], 'b')) / 3.0,
                                 255)
                    c[3] = abgr8((2.0 * get_channel_abgr8(c[1], 'r') + get_channel_abgr8(c[0], 'r')) / 3.0,
                                 (2.0 * get_channel_abgr8(c[1], 'g') + get_channel_abgr8(c[0], 'g')) / 3.0,
                                 (2.0 * get_channel_abgr8(c[1], 'b') + get_channel_abgr8(c[0], 'b')) / 3.0,
                                 255)
                else:
                    c[2] = abgr8(
                        (get_channel_abgr8(c[0], 'r') + get_channel_abgr8(c[1], 'r')) / 2.0,
                        (get_channel_abgr8(c[0], 'g') + get_channel_abgr8(c[1], 'g')) / 2.0,
                        (get_channel_abgr8(c[0], 'b') + get_channel_abgr8(c[1], 'b')) / 2.0,
                        255)
                    if ddsf_has_alphapixels:
                        c[3] = 0
                    else:
                        c[3] = abgr8(0, 0, 0, 255)
                for y_block_pos in range(4):
                    for x_block_pos in range(4):
                        x_pos = (block // (image_width // 4) * 4) + y_block_pos
                        y_pos = ((block * 4) % image_width) + x_block_pos
                        color_index = color_indices[y_block_pos * 4 + x_block_pos]
                        # Beware: x and y are flipped
                        image_data[x_pos][y_pos] = c[color_index]
        elif ddsf_has_rgb and ddsf_bitcount == 32 and ddsf_r_bitmask == 0xFF0000 and ddsf_g_bitmask == 0xFF00 and ddsf_b_bitmask == 0xFF and ddsf_a_bitmask == 0xFF000000:
            # Uncompressed argb8
            for y in range(dds_height):
                for x in range(dds_width):
                    color = self.read_int()
                    # argb to abgr
                    image_data[y][x] = abgr8((color & ddsf_r_bitmask) >> 16, (color & ddsf_g_bitmask) >> 8,
                                             color & ddsf_b_bitmask, (color & ddsf_a_bitmask) >> 24)
        elif ddsf_has_rgb and ddsf_bitcount == 16 and ddsf_r_bitmask == 63488 and ddsf_g_bitmask == 2016 and ddsf_b_bitmask == 31 and ddsf_a_bitmask == 0:
            # Uncompressed rgb565
            for y in range(dds_height):
                for x in range(dds_width):
                    color = self.read_int(2)
                    image_data[y][x] = rgb565_to_abgr8(rgb565((color & ddsf_r_bitmask) >> 11,
                                                              (color & ddsf_g_bitmask) >> 5,
                                                              (color & ddsf_b_bitmask)))
        else:
            raise ValueError("Unknown image compression used")

        self.file.close()  # Close dds file
        return [image_data, dds_width, dds_height, dds_fourcc]

    def write_png(self, data):
        # Write png
        png = PNGWriter(os.path.splitext(self.file_path)[0] + '.png')
        png.set_data_argb8_array(data[0], data[1], data[2])  # Truncate pixels that are not required
        png.write()
