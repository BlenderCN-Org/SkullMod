import os
from SkullModPy.common.CommonConstants import BIG_ENDIAN
from SkullModPy.common.Reader import Reader

FILE_EXTENSION = "sgm.msb"
FILE_VERSION = "2.0"


class SGM(Reader):
    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def get_data(self):
        sgm_data = {}
        if self.read_pascal_string() != FILE_VERSION:
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