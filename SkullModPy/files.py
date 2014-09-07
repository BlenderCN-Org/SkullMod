import os
import pathlib
import struct

from SkullModPy.writer import collada_export

from SkullModPy.common.CommonConstants import BIG_ENDIAN
from SkullModPy.common.Reader import Reader
from SkullModPy.common import SimpleParse


class GFS(Reader):
    FILE_IDENTIFIER = "Reverge Package File"
    FILE_EXTENSION = "gfs"
    FILE_VERSION = "1.1"

    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def get_metadata(self):
        """
        Read GFS file
        :raise ValueError: File integrity compromised
        """
        # Basic metadata
        data_offset = self.read_int()
        if data_offset < 48:  # Header must be at this long to be valid
            raise ValueError("Given file header is too short")
        file_identifier_length = self.read_int(8)
        # The file identifier is checked manually (instead of using read_pascal_string()) to be extra careful
        if file_identifier_length != len(self.FILE_IDENTIFIER):
            raise ValueError("Given file is not a GFS file (Identifier length error)")
        file_identifier = str(self.file.read(len(self.FILE_IDENTIFIER)), 'ascii')
        print(file_identifier)
        print(GFS.FILE_IDENTIFIER)
        if file_identifier != GFS.FILE_IDENTIFIER:
            raise ValueError("Given file is not a GFS file (Identifier string error)")
        file_version = self.read_pascal_string()
        if not file_version == GFS.FILE_VERSION:
            raise ValueError("Given file has the wrong version")
        n_of_files = self.read_int(8)

        # Get file name and path
        file_name = os.path.splitext(self.file_path)[0]
        file_directory = os.path.dirname(self.file_path)
        # Make an output path
        new_dir_path = os.path.join(file_directory, file_name)

        # Process
        running_offset = data_offset
        references = []
        for _ in range(n_of_files):
            reference_path = self.read_pascal_string()
            reference_length = self.read_int(8)
            reference_alignment = self.read_int()
            # The alignment is already included
            running_offset += (reference_alignment - (running_offset % reference_alignment)) % reference_alignment

            references.append([running_offset, reference_length, reference_path])

            running_offset += reference_length
        return {'path': new_dir_path, 'metadata': references}

    def read_pascal_string(self):
        """
        Read long+ASCII String from internal file
        :return: String
        """
        return self.read_string(self.read_int(8))


class LVL():
    """ Load an entire level and convert it to its objects """
    def __init__(self, file_path):
        self.file_path = os.path.abspath(file_path)

        if not pathlib.Path(os.path.join(os.path.dirname(self.file_path), 'background.sgi.msb')).exists():
            raise FileNotFoundError("Missing background.sgi.msb in same folder")

        with open(file_path, "r", 1, 'ascii') as f:
            self.content = f.readlines()

        # Note for Pointlight: Last two params are "Radius in pixels(at default screen res of 1280x720)" and nevercull
        #                     4 pointlights are used for effects
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

            #obj_writer(obj_file_path, vertex_list, current_sgm['index_buffer'])
        collada_path = os.path.join(os.path.abspath(os.path.dirname(file_path)), 'collada', element['shape_name'] + '.dae')
        collada_export(os.path.join("D:/","random","test.dae"), os.path.join("D:/","randomStart","textures"), "some_level", sgm_data, sgi_data)


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


class SPR(Reader):
    FILE_EXTENSION = "spr.msb"
    FILE_VERSION = "2.0"
    DATA_FORMAT_STRING = "unigned char tile_x, tile_y, tile_u, tile_v;"

    def __init__(self, file_path):
        super().__init__(open(file_path, "rb"), os.path.getsize(file_path), BIG_ENDIAN)
        self.file_path = os.path.abspath(file_path)

    def read_spr(self):
        if self.read_pascal_string() != SGI.FILE_VERSION:
            raise ValueError("Invalid version")

    def read_pascal_string(self):
            """
            Read long+ASCII String from internal file
            :return: String
            """
            return self.read_string(self.read_int(8))
"""
        public SPR_File(DataInputStream dis) throws IOException{
        fileFormatRevision = Utility.readLongPascalString(dis);

        if(!fileFormatRevision.equals(knownFileFormatRevision)){
            throw new IllegalArgumentException("File format revision does not match, stopped reading");
        }

        sceneName = Utility.readLongPascalString(dis);
        unknown1 = dis.readInt();

        dataFormatString = Utility.readLongPascalString(dis);

        if(!dataFormatString.equals(defaultDataFormatString)){
            throw new IllegalArgumentException("Data format string is not valid");
        }

        //Unsigned
        bytesPerEntry = dis.readLong();
        nOfEntries = dis.readLong();
        nOfFrames = dis.readLong();
        nOfAnimations = dis.readLong();
        blockWidth = dis.readLong();
        blockHeight = dis.readLong();

        //Init arrays for the following reads
        entries = new SPR_Entry[(int)nOfEntries];
        frames = new SPR_Frame[(int)nOfFrames];
        animations = new SPR_Animation[(int) nOfAnimations];

        for(int i = 0;i < nOfEntries;i++){
            entries[i] = new SPR_Entry(dis);
        }
        for(int i = 0;i < nOfFrames;i++){
            frames[i] = new SPR_Frame(dis, i);
        }
        for(int i = 0;i < nOfAnimations;i++){
            animations[i] = new SPR_Animation(dis);
        }
    }
    """