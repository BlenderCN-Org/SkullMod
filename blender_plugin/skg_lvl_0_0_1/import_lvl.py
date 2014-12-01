import pathlib

from .Reader import *  # struct + os
from .SimpleParse import *


# NOTE: BLENDER IS USING A RIGHT HAND COORDINATE SYSTEM
#       DIRECTX IS USING A LEFT HAND COORDINATE SYSTEM BY DEFAULT

import bpy
import mathutils

def load_lvl(file_path):
    scene = bpy.context.scene
    print("Trying to load " + str(file_path))

    file_path = os.path.abspath(file_path)

    if not pathlib.Path(os.path.join(os.path.dirname(file_path), 'background.sgi.msb')).exists():
        raise FileNotFoundError("Missing background.sgi.msb in same folder")

    with open(file_path, "r", 1, 'ascii') as f:
        content = f.readlines()

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
    lvl_metadata = parse(content, parser_instructions)
    sgi = SGI(os.path.join(os.path.dirname(file_path), 'background.sgi.msb'))
    sgi_data = sgi.get_metadata()

    sgm_data = []  # List of models

    # Materials


    # SGM
    n_of_vertices = 0
    for element in sgi_data:
        print("Current sgm file: " + os.path.join(os.path.abspath(os.path.dirname(file_path)), element['shape_name'] + '.sgm.msb'))
        sgm = SGM(os.path.join(os.path.abspath(os.path.dirname(file_path)), element['shape_name'] + '.sgm.msb'))
        current_sgm = sgm.get_data()
        sgm_data.append(current_sgm)

        vertex_list = []
        for vertex in current_sgm['vertices']:
            x = struct.unpack('>f', vertex[0:4])[0]
            y = struct.unpack('>f', vertex[4:8])[0]
            z = struct.unpack('>f', vertex[8:12])[0]
            vertex_list.append(mathutils.Vector((x, y, z)))
        n_of_vertices += len(vertex_list)
        print("Writing new object")
        mesh = bpy.data.meshes.new(element['shape_name'])
        mesh.from_pydata(vertex_list, [], current_sgm['index_buffer'])
        for o in scene.objects:
            o.select = False

        mesh.update()
        mesh.validate()

        new_object = bpy.data.objects.new(element['shape_name'], mesh)
        new_object.location = current_sgm['pos_xyz']

        scene.objects.link(new_object)
        new_object.select = True

        if scene.objects.active is None or scene.objects.active.mode == 'OBJECT':
            scene.objects.active = new_object


    print("Stage has " + str(len(sgi_data)) + " objects")
    print("Stage has " + str(n_of_vertices) + " vertices")

def load_materials():
    """Load DDS files and make materials"""
    pass

def load_lights():
    """Make lights, data is in lvl"""
    pass

def load_animations():
    """SGA"""
    pass


def load_bones():
    """SGS"""
    pass


def load(operator, context, filepath=""):
    load_lvl(filepath)
    # TODO error handling
    return {'FINISHED'}



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
