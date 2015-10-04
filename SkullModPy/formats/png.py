import os
import struct
import zlib


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
