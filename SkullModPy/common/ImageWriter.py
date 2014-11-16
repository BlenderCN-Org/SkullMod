import zlib
import struct
from SkullModPy.DDS.Color import Pixel


# MIT License http://code.activestate.com/recipes/577443-write-a-png-image-in-native-python/
def write_png(pixel_data: list, width: int, height: int):
    """
    :param pixel_data: An array of Pixel objects
    :param width: image width
    :param height: image height
    :return: Finished png file as bytes
    """
    raw_data = b''
    for y in range(0, height):
        raw_data += b'\x00'
        for x in range(0, width):
            raw_data += pixel_data[y*width + x].get_rgba8()

    # Write png chunk
    def png_pack(png_tag, data):
        chunk_head = png_tag + data
        return struct.pack("!I", len(data)) + chunk_head + struct.pack("!I", 0xFFFFFFFF & zlib.crc32(chunk_head))

    return b"".join([
        b'\x89PNG\r\n\x1a\n',
        png_pack(b'IHDR', struct.pack("!2I5B", width, height, 8, 6, 0, 0, 0)),
        png_pack(b'IDAT', zlib.compress(raw_data, 9)),
        png_pack(b'IEND', b'')])