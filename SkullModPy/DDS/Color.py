import ctypes

class Pixel:
    def __init__(self, red=0, green=0, blue=0, alpha=255, has_alpha=True):
        if ~has_alpha and alpha != 255:
            raise ValueError("Image has no alpha, no other alpha value than 255 allowed")
        self.r = red
        self.g = green
        self.b = blue
        self.a = alpha
        self.has_alpha = has_alpha

        self.iteration_index = -1

    def __iter__(self):
        return self

    def __next__(self):
        self.iteration_index += 1
        if self.iteration_index == 0:
            return self.r
        elif self.iteration_index == 1:
            return self.g
        elif self.iteration_index == 2:
            return self.b
        elif self.iteration_index == 3 and self.has_alpha:
            return self.a
        else:
            self.iteration_index = -1
            raise StopIteration

    def __eq__(self, other):
        if self.has_alpha == other.has_alpha and self.r == other.r and self.g == other.g and self.b == other.b and self.a == other.a:
            return True
        else:
            return False

    def get_rgba8(self):
        return bytearray([self.r, self.g, self.b, self.a])

    def get_abgr8(self):
        return bytearray([self.a, self.b, self.g, self.r])

    def get_rgb565(self):
        if self.r > 31 or self.g > 63 or self.b > 31:
            raise ValueError('Color contains out of range value for red and/or blue')
        # Build the value as an int (simpler than shifting around each bytes values)
        result = (self.r << 11) + (self.g << 5) + self.b
        return bytearray(result.to_bytes(2, 'big'))

