import os  # File size

from SkullModPy.common.CommonConstants import *
from SkullModPy.common.Reader import Reader


FILE_IDENTIFIER = "Reverge Package File"
FILE_EXTENSION = "gfs"
FILE_VERSION = "1.1"


class GFS(Reader):
    """ Reader for GFS """

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
        file_identifier = self.read_pascal_string()
        if not file_identifier == FILE_IDENTIFIER:
            raise ValueError("Given file is not a GFS file")
        file_version = self.read_pascal_string()
        if not file_version == FILE_VERSION:
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
