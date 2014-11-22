# TODO extra method to reorder channels afterward
# TODO dds reading: read data blocks without conversion
# TODO dds read metadata as an extra method

# Staging area for readers/writers

import os
import pathlib
import struct

from SkullModPy.writer import collada_export

from SkullModPy.common.CommonConstants import BIG_ENDIAN
from SkullModPy.common.Reader import Reader
from SkullModPy.common import SimpleParse


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
