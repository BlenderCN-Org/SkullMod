import os
import struct
from SkullModPy.common.CommonConstants import LITTLE_ENDIAN
from SkullModPy.common.Reader import Reader
from SkullModPy.common.helper import abgr8
from SkullModPy.formats.png import PNGWriter

MANUFACTURER = b'\x0A'
VERSION = b'\x05'  # The only version we will support
ENCODING = b'\x01'  # Encoding used in the game files (RLE)
BITS_PER_CHANNEL = b'\x08'  # Channel depth used in the game files (8 bits per channel)


class PCXReader(Reader):
    """ Info: http://www.shikadi.net/moddingwiki/PCX_Format + Wikipedia """

    def __init__(self, file_path: str):
        super().__init__(open(file_path, 'rb'), os.path.getsize(file_path), endianness=LITTLE_ENDIAN)
        self.file_path = file_path

    def check_destination(self):
        png_path = os.path.splitext(self.file_path)[0] + '.png'
        if os.path.exists(png_path):
            print("File already exists, will be overwritten")
        if os.path.exists(png_path) and not os.path.isfile(png_path):
            raise ValueError("Can not write png file, there is a directory in the way")

    def read_metadata(self):
        if self.file.read(1) != MANUFACTURER:
            raise ValueError("Manufacturer does not match")
        if self.file.read(1) != VERSION:
            raise ValueError("Wrong version")
        if self.file.read(1) != ENCODING:
            raise ValueError("Wrong encoding")
        if self.file.read(1) != BITS_PER_CHANNEL:
            raise ValueError("Wrong number of bits per channel")
        # According to spec
        x_min = self.read_int(2)
        y_min = self.read_int(2)
        x_max = self.read_int(2)
        y_max = self.read_int(2)
        if x_min != 0 or y_min != 0:
            raise ValueError("x_min and/or y_min are not 0")
        self.skip_bytes(2+2+48+1) # Skip VertDPI, HorzDPI, palette and reserved
        color_planes = self.read_int(1)
        if color_planes != 1:
            raise ValueError("Only one color plane allowed")
        bytes_per_plane_line = self.read_int(2)
        palette_type = self.read_int(2)
        if palette_type != 1:
            raise ValueError("Unknown palette type")

        # The rest of the header contains no relevant data
        # The actual palette is after the image data
        # Skipping to end of header (which has a fixed length of 128 bytes)
        self.file.seek(128)
        # Why +? ==> Image starts at 0 and not at 1 (see x_min and y_min)
        # Basically ?_min and ?_max are array boundaries
        return [x_max+1, y_max+1, bytes_per_plane_line]

    def read_data(self, metadata):
        width = metadata[0]
        height = metadata[1]
        bytes_per_plane_line = metadata[2]

        # Decompress the image

        image_data = [[0] * width for _ in range(height)]
        decompressed_indices_buffer = bytearray()
        while len(decompressed_indices_buffer) < (bytes_per_plane_line*height):
            # Read 'instruction' byte which contains how often the following byte has to be repeated
            # Or is already a data byte, depending if the byte value is < 192 (the two highest bits are set)
            first_byte = self.read_int(1)

            if first_byte >= 192:
                repetitions = first_byte - 192
                out_byte = self.read_int(1)
                for _ in range(repetitions):
                    decompressed_indices_buffer.append(out_byte)
            else:
                decompressed_indices_buffer.append(first_byte)

        # Image data is over, the palette should start with 0x0C
        if self.file.read(1) != b'\x0C':
            raise ValueError("Missing palette or wrong offset after reading image data")

        # Read the palette (RGB8)
        palette = [0] * 256
        for palette_entry in range(256):
            color = struct.unpack("3B", self.file.read(3))
            palette[palette_entry] = abgr8(color[0], color[1], color[2], 255)

        # Create image data
        for y in range(height):
            for x in range(width):
                color_index = decompressed_indices_buffer[y*bytes_per_plane_line + x]
                image_data[y][x] = color_index
        # Change indices to actual color values
        for y in range(height):
            for x in range(width):
                image_data[y][x] = palette[image_data[y][x]]  # Replace palette index with color

        # Finished with this file, close it
        self.file.close()
        return [image_data, width, height]

    def write_png(self, data):
        png = PNGWriter(os.path.splitext(self.file_path)[0] + '.png')
        png.set_data_argb8_array(data)
        png.write()
