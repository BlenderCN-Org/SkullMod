import os
import struct
import math
from SkullModPy.common.CommonConstants import BIG_ENDIAN
from SkullModPy.common.Reader import Reader
from SkullModPy.formats.dds import DDSReader
from SkullModPy.formats.png import PNGWriter
from SkullModPy.common.helper import rgb565_split, abgr8, split_abgr8

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
            self.unknw = spr.read_int(4)
            self.last_frame = spr.read_int(4)

    def __init__(self, file_path, charselect=False, charselect_palette=None):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)
        self.charselect = charselect
        self.charselect_palette = charselect_palette

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
        base_dir = os.path.splitext(os.path.splitext(self.file_path)[0])[0]  # TODO split away both extensions, better?
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
            raise ValueError("dds file is missing or a directory where dds file should be")
        dds = DDSReader(base_dir + '.dds', self.charselect)
        png_data = dds.get_png_data()[0]

        # Apply palette
        if self.charselect:
            for y in range(len(png_data)):
                for x in range(len(png_data[0])):
                    colors = rgb565_split(png_data[y][x])
                    r = colors['r']
                    g = colors['g']
                    b = colors['b']
                    color = self.charselect_palette[g][int(b/2)] # TODO round?
                    png_data[y][x] = color
                    # Apply outline
                    # r ... outline blending intensity (blend not if 255, blend completly if 0)
                    # b/2? ... x-coordinate in the palette
                    # g ... y-coordinate in the palette
                    if r != 31:
                        split_color = split_abgr8(color)

                        new_color = abgr8(int(split_color['r'] * (r/31.0)), # TODO round?
                                          int(split_color['g'] * (r/31.0)),
                                          int(split_color['b'] * (r/31.0)),
                                          255)
                        png_data[y][x] = new_color

        # Create directories
        if not os.path.exists(base_dir):
            os.makedirs(base_dir)  # Base directory
        for i in range(n_of_animations):  # A directory for each animation
            if not os.path.exists(os.path.join(base_dir, sprite_name, animations[i].animation_name)):
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
                frame_image_data = [[0] * frame_width for _ in range(frame_height)]

                for entry in range(frame.block_offset, frame.block_offset + frame.n_of_blocks, 1):
                    self.move_rect(frame_image_data, png_data, entries[entry].tile_u, entries[entry].tile_v,
                                   entries[entry].tile_x, entries[entry].tile_y, block_width, block_height)
                # Write image
                png = PNGWriter(os.path.join(base_dir, sprite_name, animation.animation_name,
                                             str(framenumber - animation.frame_offset) + '.png'))
                png.set_data_argb8_array(frame_image_data)
                png.write()
                # Create meta files
                with open(os.path.join(base_dir, sprite_name, animation.animation_name,
                                       str(framenumber - animation.frame_offset) + '.meta.txt'), 'w') as meta_file:
                    meta_file.write('x_center ' + str(int(frame.center_x)) + '\n')
                    meta_file.write('y_center ' + str(int(frame.center_y)) + '\n')
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
                    html.write("  <img src=\"" + animation.animation_name + "/" +
                               str(i - animation.frame_offset) + ".png\"/>\n")
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

class SPRWriter:
    def __init__(self, directory_path):
        self.directory_path = directory_path

    def check_files(self):
        print("checking")