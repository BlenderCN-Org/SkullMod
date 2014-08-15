import os
from SkullModPy.common.CommonConstants import BIG_ENDIAN
from SkullModPy.common.Reader import Reader

FILE_EXTENSION = "sgi.msb"
FILE_VERSION = "2.0"


class SGI(Reader):
    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def get_metadata(self):
        """
        Read SGI file
        :raise ValueError: File integrity compromised
        """
        sgi_data = []

        if self.read_pascal_string() != FILE_VERSION:
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