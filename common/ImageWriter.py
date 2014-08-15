import zlib
import struct


# MIT License http://code.activestate.com/recipes/577443-write-a-png-image-in-native-python/
# TODO should work with combined rgba data as well (1 int containing rgba instead of 4)
def write_png(buf, width, height):
    """

    :param buf: Buffer with RGBA bytes
    :param width: Imagewidth
    :param height: Imageheight
    :return: Finished png file
    """
    width_byte_4 = width * 4
    raw_data = b"".join(b'\x00' + buf[span:span + width_byte_4] for span in range((height - 1) * width * 4, -1, - width_byte_4))

    # Write png chunk
    def png_pack(png_tag, data):
        chunk_head = png_tag + data
        return struct.pack("!I", len(data)) + chunk_head + struct.pack("!I", 0xFFFFFFFF & zlib.crc32(chunk_head))

    return b"".join([
        b'\x89PNG\r\n\x1a\n',
        png_pack(b'IHDR', struct.pack("!2I5B", width, height, 8, 6, 0, 0, 0)),
        png_pack(b'IDAT', zlib.compress(raw_data, 9)),
        png_pack(b'IEND', b'')])