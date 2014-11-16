import os
import pathlib
import struct
import zlib
import math

from SkullModPy.writer import collada_export

from SkullModPy.common.CommonConstants import BIG_ENDIAN, LITTLE_ENDIAN
from SkullModPy.common.Reader import Reader
from SkullModPy.common import SimpleParse


# DOCUMENTATION FOR DDS: http://msdn.microsoft.com/en-us/library/windows/desktop/bb943991%28v=vs.85%29.aspxF
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


class GFSReader(Reader):
    FILE_IDENTIFIER = "Reverge Package File"
    FILE_EXTENSION = "gfs"
    FILE_VERSION = "1.1"

    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def get_metadata(self):
        """
        Read GFS file
        :raise ValueError: File integrity compromised
        """
        # Basic metadata
        data_offset = self.read_int()
        if data_offset < 48:  # Header must be at this long to be valid
            raise ValueError("Given file header is too short")
        file_identifier_length = self.read_int(8)
        # The file identifier is checked manually (instead of using read_pascal_string()) to be extra careful
        if file_identifier_length != len(self.FILE_IDENTIFIER):
            raise ValueError("Given file is not a GFS file (Identifier length error)")
        file_identifier = str(self.file.read(len(self.FILE_IDENTIFIER)), 'ascii')
        if file_identifier != GFSReader.FILE_IDENTIFIER:
            raise ValueError("Given file is not a GFS file (Identifier string error)")
        file_version = self.read_pascal_string()
        if not file_version == GFSReader.FILE_VERSION:
            raise ValueError("Given file has the wrong version")
        n_of_files = self.read_int(8)

        # Get file name and path
        file_name = os.path.splitext(self.file_path)[0]
        file_directory = os.path.dirname(self.file_path)
        # Make an output path
        new_dir_path = os.path.join(file_directory, file_name)

        # Process
        running_offset = data_offset
        references = []
        for _ in range(n_of_files):
            reference_path = self.read_pascal_string()
            reference_length = self.read_int(8)
            reference_alignment = self.read_int()
            # The alignment is already included
            running_offset += (reference_alignment - (running_offset % reference_alignment)) % reference_alignment

            references.append([running_offset, reference_length, reference_path])

            running_offset += reference_length
        return {'path': new_dir_path, 'metadata': references}

    def read_pascal_string(self):
        """
        Read long+ASCII String from internal file
        :return: String
        """
        return self.read_string(self.read_int(8))


class GFSWriter:
    """
    The writer is lazy:
    A file can have all its entries aligned (4096) or none (1)
    The file format would allow for .gfs files with aligned and unaligned
    entries, but this isn't used in the game
    """

    def __init__(self, dir_path, is_aligned):
        self.dir_path = os.path.abspath(dir_path)
        self.is_aligned = is_aligned

    def get_metadata(self):
        """
        Overwrites existing files
        """
        # Check if all prerequisits are met
        if not os.path.exists(self.dir_path) or not os.path.isdir(self.dir_path):
            raise NotADirectoryError("Doesn't exist or not a directory")
        # Generate file list for the directory
        file_list = []
        for root, subdirs, files in os.walk(self.dir_path):
            # Get basepath length explicitly
            base_path_length = len(self.dir_path) + 1  # +1 because of the path delimiter
            # Go through all files in this directory
            # Save their relative positions and size
            for file in files:
                if root == self.dir_path:
                    file_list.append(file)
                    file_list.append(os.path.getsize(os.path.join(root, file)))
                else:
                    # Add to file list and replace all backwards slashes with forward slashes
                    file_list.append((root[base_path_length:len(root)] + '/' + file).replace('\\', '/'))
                    file_list.append(os.path.getsize(os.path.join(root, file)))
        return file_list

    def write_content(self, metadata):
        if os.path.isdir(self.dir_path + '.gfs'):
            raise FileExistsError('There is a directory with the same name as a .gfs file')
        if os.path.exists(self.dir_path + '.gfs'):
            print(os.path.basename(self.dir_path + '.gfs') + " will be overwritten")
        # Calculate number of files
        n_of_files = len(metadata) // 2
        # Save alignment
        alignment = 4096 if self.is_aligned else 1
        # Calculate the offset for the data portion (independent of the alignment)
        header_length = 51  # Base size (contains offset/file string/version/nOfFiles)
        for i in range(0, n_of_files):
            header_length += 8 + len(metadata[i * 2]) + 8 + 4  # long strLength+fileName+long fileSize+uint alignment
        # Calculate each position for the files (requires alignment)
        file_offsets = []
        running_offset = header_length
        for i in range(0, n_of_files):
            if alignment != 1:
                running_offset += alignment - (running_offset % alignment)
            file_offsets.append(running_offset)
            running_offset += metadata[i * 2 + 1]
        # Write header
        with open(self.dir_path + '.gfs', 'wb') as f:
            # Q ... uint64     L ... uint32
            f.write(struct.pack(BIG_ENDIAN + 'L', header_length))
            GFSWriter.write_pascal_string(f, 'Reverge Package File')
            GFSWriter.write_pascal_string(f, '1.1')
            f.write(struct.pack(BIG_ENDIAN + 'Q', n_of_files))
            for i in range(0, n_of_files):
                GFSWriter.write_pascal_string(f, metadata[i * 2])
                f.write(struct.pack(BIG_ENDIAN + 'Q', metadata[i * 2 + 1]))
                f.write(struct.pack(BIG_ENDIAN + 'L', alignment))
            if f.tell() % alignment != 0:  # Only align if alignment is needed
                f.write(b'\x00' * (alignment - (f.tell() % alignment)))  # Align header if needed
            for i in range(0, n_of_files):
                # Open file, read chunks, write chunks into this file
                with open(os.path.join(self.dir_path, metadata[i * 2].replace("/", "\\")), 'rb') as data_file:
                    bytes_read = data_file.read(4096)
                    while bytes_read:
                        f.write(bytes_read)
                        bytes_read = data_file.read(4096)
                if f.tell() % alignment != 0:  # Only align if alignment is needed
                    f.write(b'\x00' * (alignment - (f.tell() % alignment)))  # Write alignment

    @staticmethod
    def write_pascal_string(f, string):
        ascii_string = string.encode('ascii')
        f.write(struct.pack(BIG_ENDIAN + 'Q', len(ascii_string)))
        f.write(ascii_string)


class LVL():
    """ Load an entire level and convert it to its objects """

    def __init__(self, file_path):
        self.file_path = os.path.abspath(file_path)

        if not pathlib.Path(os.path.join(os.path.dirname(self.file_path), 'background.sgi.msb')).exists():
            raise FileNotFoundError("Missing background.sgi.msb in same folder")

        with open(file_path, "r", 1, 'ascii') as f:
            self.content = f.readlines()

        # Note for Pointlight: Last two params are "Radius in pixels(at default screen res of 1280x720)" and nevercull
        # 4 point lights are used for effects
        # Default values: (thanks MikeZ)
        # stageSizeDefaultX = 3750
        # stageSizeDefaultY = 2000
        # defaultShadowDistance = -400 # negative is down (below the chars), positive is up (on floor behind them)
        # Guessed default values:
        # z near and far: 3,20000
        parser_instructions = [['StageSize:', 'ii'],
                               ['BottomClearance:', 'i'],
                               ['Start1:', 'i'],
                               ['Start2:', 'i'],
                               ['ShadowDir:', 'c'],  # deprecated, only U and D are allowed characters
                               ['ShadowDist:', 'i'],  # Use this instead (to convert: Default is -400)
                               ['Light:', 'siiifffis'],  # String is 'Pt',  rgbxyz...  , 8 allowed (use max 4)
                               ['Light:', 'siiifffi'],  # Pointlight without nevercull
                               ['Light:', 'siiifff'],  # String is 'Dir', rgbxyz,  4 allowed (use max 2)
                               ['Light:', 'siii'],  # String is 'Amb', rgb,     1 allowed
                               ['CAMERA', 'iii'],  # fov, znear zfar
                               ['CAMERA', 'i'],  # fov
                               ['3D', 'fii'],  # tile_rate, tilt_height1, tilt_height2
                               ['2D', 's'],  # Contains the path to the texture for the 2D level
                               ['Music_Intro', 's'],
                               ['Music_Loop', 's'],
                               ['Music_InterruptIntro', 'i'],  # If >0 loop starts even if intro hasn't finished
                               ['Music_Outro', 's'],
                               ['Replace', 'sssss'],
                               ['ForceReplace', 'i'],
                               ['ReplaceNumIfChar', 'si'],
                               ['Replace', 'ss']]  # This one is for ReplaceNumIfChar
        lvl_metadata = SimpleParse.parse(self.content, parser_instructions)
        sgi = SGI(os.path.join(os.path.dirname(self.file_path), 'background.sgi.msb'))
        sgi_data = sgi.get_metadata()

        sgm_data = []  # List of models
        sga_data = {}  # Dictionary of animations (key is animation name, global)

        for element in sgi_data:
            sgm = SGM(os.path.join(os.path.abspath(os.path.dirname(file_path)), element['shape_name'] + '.sgm.msb'))
            current_sgm = sgm.get_data()

            sgm_data.append(current_sgm)

            obj_file_path = os.path.join(os.path.abspath(os.path.dirname(file_path)), 'obj',
                                         element['shape_name'] + '.obj')
            vertex_list = []
            for vertex in current_sgm['vertices']:
                x = struct.unpack('>f', vertex[0:4])[0]
                y = struct.unpack('>f', vertex[4:8])[0]
                z = struct.unpack('>f', vertex[8:12])[0]
                vertex_list.append(['{:6g}'.format(x), '{:6g}'.format(y), '{:6g}'.format(z)])

                # obj_writer(obj_file_path, vertex_list, current_sgm['index_buffer'])
        collada_path = os.path.join(os.path.abspath(os.path.dirname(file_path)), 'collada',
                                    element['shape_name'] + '.dae')
        collada_export(os.path.join("D:/", "random", "test.dae"), os.path.join("D:/", "randomStart", "textures"),
                       "some_level", sgm_data, sgi_data)


class SGM(Reader):
    FILE_EXTENSION = "sgm.msb"
    FILE_VERSION = "2.0"

    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def get_data(self):
        sgm_data = {}
        if self.read_pascal_string() != SGM.FILE_VERSION:
            raise ValueError("Invalid version")
        sgm_data['texture_name'] = self.read_pascal_string()
        self.skip_bytes(52)  # TODO Unknown stuff
        sgm_data['data_format'] = self.read_pascal_string()
        sgm_data['attribute_length_per_vertex'] = self.read_int(8)
        number_of_vertices = self.read_int(8)
        number_of_triangles = self.read_int(8)
        number_of_joints = self.read_int(8)

        # VERTICES
        vertices = []
        for _ in range(0, number_of_vertices):
            vertices.append(self.file.read(sgm_data['attribute_length_per_vertex']))
        sgm_data['vertices'] = vertices
        # TRIANGLE DEFINTION for an index buffer
        triangles = []
        for _ in range(0, number_of_triangles):
            triangles.append([self.read_int(2), self.read_int(2), self.read_int(2)])
        sgm_data['index_buffer'] = triangles

        # Object pos/rot
        # TODO make a for out of it
        sgm_data['pos_xyz'] = [self.read_float() for _ in range(0, 3)]
        sgm_data['rot_xyz'] = [self.read_float() for _ in range(0, 3)]
        # JOINTS
        joints = []
        for _ in range(0, number_of_joints):
            joints.append([self.read_pascal_string()])
        for i in range(0, number_of_joints):
            joints[i].append(self.read_mat4())
        sgm_data['joints'] = joints
        return sgm_data

    def read_pascal_string(self):
        """
        Read long+ASCII String from internal file
        :return: String
        """
        return self.read_string(self.read_int(8))

    def read_mat4(self):
        return [self.read_float() for _ in range(0, 16)]


class SGI(Reader):
    FILE_EXTENSION = "sgi.msb"
    FILE_VERSION = "2.0"

    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def get_metadata(self):
        """
        Read SGI file
        :raise ValueError: File integrity compromised
        """
        sgi_data = []

        if self.read_pascal_string() != SGI.FILE_VERSION:
            raise ValueError("Invalid version")
        number_of_elements = self.read_int(8)

        for _ in range(0, number_of_elements):
            element = {'element_name': self.read_pascal_string(),
                       'shape_name': self.read_pascal_string(),
                       'mat4': self.read_mat4()}
            self.skip_bytes(2)  # TODO unknown

            number_of_animations = self.read_int(8)
            animations = []
            for _ in range(0, number_of_animations):
                animations.append({'animation_name': self.read_pascal_string(),
                                   'animation_file_name': self.read_pascal_string()})
            element['animations'] = animations
            sgi_data.append(element)
        return sgi_data

    def read_pascal_string(self):
        """
        Read long+ASCII String from internal file
        :return: String
        """
        return self.read_string(self.read_int(8))

    def read_mat4(self):
        return [self.read_float() for _ in range(0, 16)]


class SPR(Reader):
    FILE_EXTENSION = "spr.msb"
    FILE_VERSION = "2.0"
    DATA_FORMAT_STRING = "unigned char tile_x, tile_y, tile_u, tile_v;"

    class SPREntry:
        def __init__(self, spr):
            self.tile_x = spr.read_int(1)
            self.tile_y = spr.read_int(1)
            self.tile_u = spr.read_int(1)
            self.tile_v = spr.read_int(1)

    class SPRFrame:
        def __init__(self, spr):
            self.block_offset = spr.read_int(4)
            self.n_of_blocks = spr.read_int(4)
            self.unkwn1 = spr.read_int(4)
            self.center_x = struct.unpack('>f', spr.file.read(4))[0]
            self.center_y = struct.unpack('>f', spr.file.read(4))[0]


    class SPRAnimations:
        def __init__(self, spr):
            self.animation_name = spr.read_pascal_string()
            self.frame_offset = spr.read_int(4)
            self.n_of_frames = spr.read_int(4)
            self.unknw1 = spr.read_int(4)
            self.unknw2 = spr.read_int(4)

    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def read_spr(self):
        if self.read_pascal_string() != SPR.FILE_VERSION:
            raise ValueError("Invalid version")
        sprite_name = self.read_pascal_string()
        self.skip_bytes(4)  # TODO make it known
        if self.read_pascal_string() != SPR.DATA_FORMAT_STRING:
            raise ValueError("Not a valid sprite")
        bytes_per_entry = self.read_int(8)
        if bytes_per_entry != 4:
            raise ValueError("Unknown number of bytes per entry for sprite")
        n_of_entries = self.read_int(8)
        n_of_frames = self.read_int(8)
        n_of_animations = self.read_int(8)
        block_width = self.read_int(8)
        block_height = self.read_int(8)

        # Read data from file
        entries = []
        frames = []
        animations = []
        for _ in range(n_of_entries):
            entries.append(self.SPREntry(self))
        for _ in range(n_of_frames):
            frames.append(self.SPRFrame(self))
        for _ in range(n_of_animations):
            animations.append(self.SPRAnimations(self))

        # Check if requirements are met to write data out
        # Requirements are: No files that are named like the folders we want to write
        # Everything that can be overwritten will be overwritten
        base_dir = os.path.splitext(os.path.splitext(self.file_path)[0])[0]   # TODO split away both extensions, better way?
        if os.path.exists(base_dir) and not os.path.isdir(base_dir):
            raise FileExistsError("There is a file with the same name as the directory that should be created")
        if os.path.exists(os.path.join(base_dir, sprite_name)) and not os.path.isdir(os.path.join(base_dir, sprite_name)):
            raise FileExistsError("There is a file with the same name as the directory that should be created")
        for i in range(n_of_animations):
            target_dir = os.path.join(base_dir, sprite_name, animations[i].animation_name)
            if os.path.exists(target_dir) and os.path.isfile(target_dir):
                raise FileExistsError("There is a file with the same name as the directory that should be created")
        # Get image data
        dds_path = base_dir + '.dds'
        if not os.path.exists(dds_path) or not os.path.isfile(dds_path):
            raise ValueError("dds file is missing or directory where dds file should be")
        dds = DDSReader(base_dir + '.dds')
        png_data = dds.get_png_data()[0]
        # Create directories
        if not os.path.exists(base_dir):
            os.makedirs(base_dir)  # Base directory
        for i in range(n_of_animations):  # A directory for each animation
            if not os.path.exists(os.path.join(base_dir, sprite_name,animations[i].animation_name)):
                os.makedirs(os.path.join(base_dir, sprite_name, animations[i].animation_name))
        # Create png files
        for animation in animations:
            print('Extracting animation: ' + animation.animation_name)
            for framenumber in range(animation.frame_offset, animation.frame_offset + animation.n_of_frames, 1):
                frame = frames[framenumber]
                bounds = self.max_bounds(entries, frame.block_offset, frame.n_of_blocks, block_width, block_height)
                frame_width = bounds[0]
                frame_height = bounds[1]
                # Make image data
                frame_image_data = [[0] * frame_width for i in range(frame_height)]

                for entry in range(frame.block_offset, frame.block_offset + frame.n_of_blocks, 1):
                    self.move_rect(frame_image_data, png_data, entries[entry].tile_u, entries[entry].tile_v,
                                   entries[entry].tile_x, entries[entry].tile_y, block_width, block_height)
                # Write image
                png = PNGWriter(os.path.join(base_dir, sprite_name, animation.animation_name,
                                             str(framenumber - animation.frame_offset) + '.png'))
                png.set_data_argb8_array(frame_image_data)
                png.write()
                # Create meta files
                # TODO do this, needed for packing it again
            # Create html files
            with open(os.path.join(base_dir, sprite_name, animation.animation_name + '.html'), 'w') as html:
                html.writelines(["<!DOCTYPE html>\n",
                                "<html>\n",
                                "<head>\n",
                                "<title>" + animation.animation_name + "</title>\n",
                                "<meta charset=\"UTF-8\">\n",
                                "<style>\n",
                                "#animation img { display: none; }\n",
                                "#animation img:first.child { display: block; }\n",
                                "</style>\n",
                                "<script>\n",
                                "loading_finished = function startAnimation(){\n"
                                "  var frames = document.getElementById(\"animation\").children;\n",
                                "  var frameCount = frames.length;\n",
                                "  var i = 0;\n",
                                "  setInterval(function(){\n",
                                "    frames[i % frameCount].style.display = \"none\";\n",
                                "    frames[++i % frameCount].style.display = \"block\";\n",
                                "  },100);\n",
                                "}\n",
                                "window.onload=loading_finished;\n",
                                "</script>\n",
                                "</head>\n",
                                "<body>\n",
                                "<div id=\"animation\">\n"])
                for i in range(animation.frame_offset, animation.frame_offset + animation.n_of_frames, 1):
                    html.write("  <img src=\"" + animation.animation_name + "/" + str(i - animation.frame_offset) + ".png\"/>\n")
                html.writelines(["</div>\n",
                                "</body>\n",
                                "</html>"])



    @staticmethod
    def move_rect(frame_image_data, png_data, tile_u, tile_v, tile_x, tile_y, block_width, block_height):
        for y in range(block_height):
            for x in range(block_width):
                frame_image_data[tile_y * block_height + y][tile_x * block_width + x] = \
                    png_data[tile_v * block_height + y][tile_u * block_width + x]


    @staticmethod
    def max_bounds(entries, block_offset, n_of_blocks, block_width, block_height):
        x_max = 0
        y_max = 0
        for i in range(block_offset, block_offset + n_of_blocks, 1):
            x_max = max(x_max, (entries[i].tile_x + 1) * block_width)
            y_max = max(y_max, (entries[i].tile_y + 1) * block_height)
        return [x_max, y_max]

    def read_pascal_string(self):
        """
            Read long+ASCII String from internal file
            :return: String
            """
        return self.read_string(self.read_int(8))


class PNGWriter:
    """ Very simple PNG writer """

    def __init__(self, path):
        self.file_path = path
        self.data = None
        self.width = None
        self.height = None

    def set_data_argb8(self, data, width, height):
        """
        Prepare rgba8 data for writing
        :param data: 1D array of abgr8 int
        :param width: Width of the image
        :param height: Height of the image
        """
        self.width = width
        self.height = height
        self.data = bytearray()
        for y in range(0, self.height):
            self.data += b'\x00'  # Line start
            for x in range(self.width):
                self.data += struct.pack('<L', data[y * width + x])

    def set_data_argb8_array(self, data, width=None, height=None):
        """
        Prepare abgr8 data for writing
        :param data: 2D array (first dimension ... y, second ... x) with abgr8 int
        """
        self.width = len(data[0]) if width is None else width
        self.height = len(data) if height is None else height

        self.data = bytearray()
        for y in range(0, self.height):
            self.data += b'\x00'  # Line start
            for x in range(0, self.width):
                self.data += struct.pack('<L', data[y][x])

    def write(self):
        """
        Write the PNG file
        Do NOT use this reference after calling write
        All data is removed after writing
        """
        if os.path.isdir(self.file_path):
            raise IsADirectoryError("The given path is a directory")
        if os.path.isfile(self.file_path):
            print("Found a file at given path, will be overwritten")
        with open(self.file_path, 'wb') as f:
            f.write(b"".join([
                b'\x89PNG\r\n\x1a\n',
                PNGWriter.png_pack(b'IHDR', struct.pack("!2I5B", self.width, self.height, 8, 6, 0, 0, 0)),
                PNGWriter.png_pack(b'IDAT', zlib.compress(self.data, 9)),
                PNGWriter.png_pack(b'IEND', b'')]))
        del self.data  # Remove last reference and explicitly tell python that this isn't desired anymore
        # TODO is this correct?

    @staticmethod
    def png_pack(png_tag, data):
        """
        Generate PNG chunk
        :param png_tag: Chunk identifier (4 byte ascii string)
        :param data: Complete chunk
        :return:
        """
        chunk_head = png_tag + data
        return struct.pack("!I", len(data)) + chunk_head + struct.pack("!I", 0xFFFFFFFF & zlib.crc32(chunk_head))

    @staticmethod
    def get_abgr8_int(r, g, b, a):
        """
        :param r: Red color, must be between 0-255
        :param g: Green color, must be between 0-255
        :param b: Blue color, must be between 0-255
        :param a: Alpha (transparency), must be between 0-255
        :return: abgr8 int
        """
        return a << 24 | b << 16 | g << 8 | r


class DDSReader(Reader):
    """
    Source:
    http://www.matejtomcik.com/Public/KnowHow/DXTDecompression/
    http://msdn.microsoft.com/en-us/library/windows/desktop/bb943991%28v=vs.85%29.aspx
    """

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
        if self.file.read(4) != DDS_MAGIC:
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

        if dds_fourcc != 'DXT5' and dds_fourcc != 'DXT3' and dds_fourcc != 'DXT1':
            raise ValueError("dxt1/3/5 implemented")
        # Start reading
        # Image height has to be a multiple of 4

        image_height = dds_height if dds_height % 4 == 0 else dds_height + 4 - (dds_height % 4)
        image_width = dds_width if dds_width % 4 == 0 else dds_width + 4 - (dds_width % 4)

        y_blocks = image_height // 4
        x_blocks = image_width // 4

        # Array of pixeldata, packed abgr8 is used because no conversion is needed for png
        image_data = [[0] * image_width for i in range(image_height)]

        if dds_fourcc == 'DXT5':
            for block in range(x_blocks * y_blocks):  # For each block
                a = [0 for i in range(8)]
                c = [0 for i in range(4)]

                a[0] = self.read_int(1)  # Alpha 0, 1 Byte
                a[1] = self.read_int(1)  # Alpha 1, 1 Byte

                # alpha indices (6 Bytes(16*3 bit)), swap each 3 bytes
                alpha_indices_raw = self.file.read(6)
                alpha_indices_raw = bytes([alpha_indices_raw[2], alpha_indices_raw[1], alpha_indices_raw[0],
                                           alpha_indices_raw[5], alpha_indices_raw[4], alpha_indices_raw[3]])
                # Get alpha indices
                alpha_indices = self.get_bits_array(alpha_indices_raw, 3)
                # Order is: hgfedcba ponmlkji, reorder to abcdefgh ijklmnop
                alpha_indices = [alpha_indices[((i // 8) * 8) + (7 - (i % 8))] for i in range(len(alpha_indices))]

                # c0 2 Bytes
                c[0] = struct.unpack('<H', self.file.read(2))[0]
                # c1 2 Bytes
                c[1] = struct.unpack('<H', self.file.read(2))[0]
                # Get and split color indices (16*2 Bit)
                color_indices = self.get_bits_array(self.file.read(4), 2)
                # Order: dcba hgfe lkji ponm reorder: to a-p
                color_indices = [color_indices[((i // 4) * 4) + (3 - (i % 4))] for i in range(len(color_indices))]

                # Calculate: Color 2 and 3
                c0_split = self.rgb565_split(c[0])
                c1_split = self.rgb565_split(c[1])

                c[2] = self.rgb565_to_abgr8(
                    self.rgb565(int(math.floor(((2.0 * c0_split['r'] + c1_split['r']) / 3.0) + 0.5)),
                                int(math.floor(((2.0 * c0_split['g'] + c1_split['g']) / 3.0) + 0.5)),
                                int(math.floor(((2.0 * c0_split['b'] + c1_split['b']) / 3.0) + 0.5)))
                )
                c[3] = self.rgb565_to_abgr8(
                    self.rgb565(int(math.floor(((2.0 * c1_split['r'] + c0_split['r']) / 3.0) + 0.5)),
                                int(math.floor(((2.0 * c1_split['g'] + c0_split['g']) / 3.0) + 0.5)),
                                int(math.floor(((2.0 * c1_split['b'] + c0_split['b']) / 3.0) + 0.5)))
                )
                # Lower precision?
                # c[2] = self.rgb565_to_abgr8(
                # self.rgb565((2 * c0_split['r'] + c1_split['r']) // 3,
                #                (2 * c0_split['g'] + c1_split['g']) // 3,
                #                (2 * c0_split['b'] + c1_split['b']) // 3)
                #)
                #c[3] = self.rgb565_to_abgr8(
                #    self.rgb565((2 * c1_split['r'] + c0_split['r']) // 3,
                #                (2 * c1_split['g'] + c0_split['g']) // 3,
                #                (2 * c1_split['b'] + c0_split['b']) // 3)
                #)

                c[0] = self.rgb565_to_abgr8(c[0])
                c[1] = self.rgb565_to_abgr8(c[1])

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
                        image_data[x_pos][y_pos] = self.merge_abgr8(a[alpha_index], c[color_index])
        elif dds_fourcc == 'DXT3':
            for block in range(x_blocks * y_blocks):  # For each block
                raise NotImplementedError('Not implemented')
        elif dds_fourcc == 'DXT1':
            for block in range(x_blocks * y_blocks):  # For each block
                c = [0 for i in range(4)]
                # c0 2 Bytes
                c0_raw = struct.unpack('<H', self.file.read(2))[0]
                c[0] = self.rgb565_to_abgr8(c0_raw)
                # c1 2 Bytes
                c1_raw = struct.unpack('<H', self.file.read(2))[0]
                c[1] = self.rgb565_to_abgr8(c1_raw)
                # Get and split color indices (16*2 Bit)
                color_indices = self.get_bits_array(self.file.read(4), 2)
                # Order: dcba hgfe lkji ponm reorder: to a-p
                color_indices = [color_indices[((i // 4) * 4) + (3 - (i % 4))] for i in range(len(color_indices))]

                # Calculate: Color 2 and 3
                if c0_raw > c1_raw:
                    c[2] = self.abgr8((2.0 * self.get_channel_abgr8(c[0], 'r') + self.get_channel_abgr8(c[1], 'r')) / 3.0,
                                      (2.0 * self.get_channel_abgr8(c[0], 'g') + self.get_channel_abgr8(c[1], 'g')) / 3.0,
                                      (2.0 * self.get_channel_abgr8(c[0], 'b') + self.get_channel_abgr8(c[1], 'b')) / 3.0,
                                      255
                    )
                    c[3] = self.abgr8((2.0 * self.get_channel_abgr8(c[1], 'r') + self.get_channel_abgr8(c[0], 'r')) / 3.0,
                                      (2.0 * self.get_channel_abgr8(c[1], 'g') + self.get_channel_abgr8(c[0], 'g')) / 3.0,
                                      (2.0 * self.get_channel_abgr8(c[1], 'b') + self.get_channel_abgr8(c[0], 'b')) / 3.0,
                                      255
                    )
                else:
                    c[2] = self.abgr8(
                        (self.get_channel_abgr8(c[0], 'r') + self.get_channel_abgr8(c[1], 'r')) / 2.0,
                        (self.get_channel_abgr8(c[0], 'g') + self.get_channel_abgr8(c[1], 'g')) / 2.0,
                        (self.get_channel_abgr8(c[0], 'b') + self.get_channel_abgr8(c[1], 'b')) / 2.0,
                        255)
                    if ddsf_has_alphapixels:
                        c[3] = 0
                    else:
                        c[3] = self.abgr8(0, 0, 0, 255)
                for y_block_pos in range(4):
                    for x_block_pos in range(4):
                        x_pos = (block // (image_width // 4) * 4) + y_block_pos
                        y_pos = ((block * 4) % image_width) + x_block_pos
                        color_index = color_indices[y_block_pos * 4 + x_block_pos]
                        # Beware: x and y are flipped
                        image_data[x_pos][y_pos] = c[color_index]
        else:
            raise ValueError('Unknown dds format: ' + dds_fourcc)

        self.file.close()  # Close dds file
        return [image_data, dds_width, dds_height]

    def write_png(self, data):
        # Write png
        png = PNGWriter(os.path.splitext(self.file_path)[0] + '.png')
        png.set_data_argb8_array(data[0], data[1], data[2])  # Truncates bytes that are not required
        png.write()

    @staticmethod
    def get_bits(bytes_in, bit_start, bits):
        """
        Get bits, only upto 8 bit, ugly
        :param bytes_in: bytes array
        :param bit_start: start bit (from the left), starts with 1, inclusive
        :param bits: How many bits?
        :return: Remaining value
        """
        bit_start -= 1  # Convenience, starts with 1

        if bits > 8:
            raise ValueError('More than 8 bit requested')
        if bits < 1:
            raise ValueError('At least one bit has to be requested')
        # Starting bit out of range
        if bit_start < 1 | bit_start > len(bytes_in) * 8:
            raise ValueError('Start bit is out of bounds')

        # Check if one or two bytes are affected and cut them
        bytes_affected = 2 if (bit_start % 8) + bits > 8 else 1
        result = bytes_in[bit_start // 8:((bit_start + bits - 1) // 8) + 1]  # TODO explanation
        result = struct.unpack(">B", result)[0] if bytes_affected == 1 else struct.unpack(">H", result)[0]

        # Get required bytes
        result >>= (8 - ((bit_start + bits) % 8)) % 8
        result &= (2 ** bits) - 1  # Remove all bits left from the desired bits
        return result

    @staticmethod
    def get_bits_array(bytes_in, bits):
        """
        Get all bits from bytes
        :param bytes_in:
        :param bits:
        :return:
        """
        if (len(bytes_in) * 8) % bits != 0:
            raise ValueError('Given divisor (bits) does not divide the bits evenly')
        result = [0] * (len(bytes_in) * 8 // bits)
        for i in range((len(bytes_in) * 8) // bits):
            result[i] = DDSReader.get_bits(bytes_in, (i * bits) + 1, bits)
        return result

    @staticmethod
    def rgb565_to_abgr8(rgb565):
        # Get channel value
        r = (rgb565 >> 11) & ((2 ** 5) - 1)  # Get red channel value
        g = (rgb565 >> 5) & ((2 ** 6) - 1)  # Get green channel value
        b = rgb565 & ((2 ** 5) - 1)  # Get blue channel value
        # Expand channel
        # Lowest precision
        # r = (r << 3)
        # g = (g << 2)
        #b = (b << 3)
        # Lower precision
        #r = (r << 3) | (r >> 2)
        #g = (g << 2) | (g >> 4)
        #b = (b << 3) | (b >> 2)
        # As precise as possible, this is still not exactly like when loaded with Photoshop
        # But the difference is less never more than 3
        r = int(math.floor(r * 255.0 / 31.0 + 0.5))
        g = int(math.floor(g * 255.0 / 63.0 + 0.5))
        b = int(math.floor(b * 255.0 / 31.0 + 0.5))
        return PNGWriter.get_abgr8_int(r, g, b, 255)

    @staticmethod
    def split_abgr8(abgr8: int):
        return {'a': (abgr8 >> 24) & 0xFF, 'b': (abgr8 >> 16) & 0xFF, 'g': (abgr8 >> 8) & 0xFF, 'r': abgr8 & 0xFF}

    @staticmethod
    def rgb565_split(rgb565: int):
        r = (rgb565 >> 11) & ((2 ** 5) - 1)  # Get red channel value
        g = (rgb565 >> 5) & ((2 ** 6) - 1)  # Get green channel value
        b = rgb565 & ((2 ** 5) - 1)  # Get blue channel value
        return {'r': r, 'g': g, 'b': b}

    @staticmethod
    def rgb565(r: int, g: int, b: int):
        return (r << 11) | (g << 5) | b

    @staticmethod
    def merge_abgr8(a, bgr):
        return (a << 24) | (bgr & ((2 ** 24) - 1))

    @staticmethod
    def abgr8(r, g, b, a):
        r = int(r)
        g = int(g)
        b = int(b)
        if r > 255:
            r = 255
        if g > 255:
            g = 255
        if b > 255:
            b = 255

        return a << 24 | b << 16 | g << 8 | r

    @staticmethod
    def get_channel_abgr8(abgr8: int, channel: str):
        if channel == 'r':
            return abgr8 & 0xFF
        elif channel == 'g':
            return (abgr8 >> 8) & 0xFF
        elif channel == 'b':
            return (abgr8 >> 16) & 0xFF
        elif channel == 'a':
            return (abgr8 >> 24) & 0xFF
        else:
            raise ValueError("Invalid channel")