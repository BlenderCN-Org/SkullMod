import struct
import math


def get_abgr8_int(r, g, b, a):
    """
    :param r: Red color, must be between 0-255
    :param g: Green color, must be between 0-255
    :param b: Blue color, must be between 0-255
    :param a: Alpha (transparency), must be between 0-255
    :return: abgr8 int
    """
    return a << 24 | b << 16 | g << 8 | r


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
        result[i] = get_bits(bytes_in, (i * bits) + 1, bits)
    return result


def rgb565_to_abgr8(rgb565_color):
    # Get channel value
    r = (rgb565_color >> 11) & ((2 ** 5) - 1)  # Get red channel value
    g = (rgb565_color >> 5) & ((2 ** 6) - 1)  # Get green channel value
    b = rgb565_color & ((2 ** 5) - 1)  # Get blue channel value
    # Expand channel
    # As precise as possible, this is still not exactly like when loaded with Photoshop
    # But the difference is less never more than 3
    r = int(math.floor(r * 255.0 / 31.0 + 0.5))
    g = int(math.floor(g * 255.0 / 63.0 + 0.5))
    b = int(math.floor(b * 255.0 / 31.0 + 0.5))
    return get_abgr8_int(r, g, b, 255)


def split_abgr8(abgr8_color: int):
    return {'a': (abgr8_color >> 24) & 0xFF, 'b': (abgr8_color >> 16) & 0xFF,
            'g': (abgr8_color >> 8) & 0xFF, 'r': abgr8_color & 0xFF}


def rgb565_split(rgb565_color: int):
    r = (rgb565_color >> 11) & ((2 ** 5) - 1)  # Get red channel value
    g = (rgb565_color >> 5) & ((2 ** 6) - 1)  # Get green channel value
    b = rgb565_color & ((2 ** 5) - 1)  # Get blue channel value
    return {'r': r, 'g': g, 'b': b}


def rgb565(r: int, g: int, b: int):
    return (r << 11) | (g << 5) | b


def merge_abgr8(a, bgr):
    return (a << 24) | (bgr & ((2 ** 24) - 1))


def abgr8(r, g, b, a):
    # Runden
    r = int(r)
    g = int(g)
    b = int(b)
    a = int(a)
    #if r > 255 or g > 255 or b > 255:
    #    raise ValueError("Color out of range")
    if r > 255:
        r = 255
    if g > 255:
        g = 255
    if b > 255:
        b = 255
    return a << 24 | b << 16 | g << 8 | r


def get_channel_abgr8(abgr8_color: int, channel: str):
    if channel == 'r':
        return abgr8_color & 0xFF
    elif channel == 'g':
        return (abgr8_color >> 8) & 0xFF
    elif channel == 'b':
        return (abgr8_color >> 16) & 0xFF
    elif channel == 'a':
        return (abgr8_color >> 24) & 0xFF
    else:
        raise ValueError("Invalid channel")