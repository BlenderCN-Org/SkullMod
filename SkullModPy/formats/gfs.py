import os
import struct
from SkullModPy.common.CommonConstants import BIG_ENDIAN
from SkullModPy.common.Reader import Reader


class GFSReader(Reader):
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
        if file_identifier != GFSReader.FILE_IDENTIFIER:
            raise ValueError("Given file is not a GFS file (Identifier string error)")
        file_version = self.read_pascal_string()
        if not file_version == GFSReader.FILE_VERSION:
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


class GFSWriter:
    """
    The writer is lazy:
    A file can have all its entries aligned (4096) or none (1)
    The file format would allow for .gfs files with aligned and unaligned
    entries, but this isn't used in the game
    """

    def __init__(self, dir_path, is_aligned):
        self.dir_path = os.path.abspath(dir_path)
        self.is_aligned = is_aligned

    def get_metadata(self):
        """
        Overwrites existing files
        """
        # Check if all prerequisits are met
        if not os.path.exists(self.dir_path) or not os.path.isdir(self.dir_path):
            raise NotADirectoryError("Doesn't exist or not a directory")
        # Generate file list for the directory
        file_list = []
        for root, subdirs, files in os.walk(self.dir_path):
            # Get basepath length explicitly
            base_path_length = len(self.dir_path) + 1  # +1 because of the path delimiter
            # Go through all files in this directory
            # Save their relative positions and size
            for file in files:
                if root == self.dir_path:
                    file_list.append(file)
                    file_list.append(os.path.getsize(os.path.join(root, file)))
                else:
                    # Add to file list and replace all backwards slashes with forward slashes
                    file_list.append((root[base_path_length:len(root)] + '/' + file).replace('\\', '/'))
                    file_list.append(os.path.getsize(os.path.join(root, file)))
        return file_list

    def write_content(self, metadata):
        if os.path.isdir(self.dir_path + '.gfs'):
            raise FileExistsError('There is a directory with the same name as a .gfs file')
        if os.path.exists(self.dir_path + '.gfs'):
            print(os.path.basename(self.dir_path + '.gfs') + " will be overwritten")
        # Calculate number of files
        n_of_files = len(metadata) // 2
        # Save alignment
        alignment = 4096 if self.is_aligned else 1
        # Calculate the offset for the data portion (independent of the alignment)
        header_length = 51  # Base size (contains offset/file string/version/nOfFiles)
        for i in range(0, n_of_files):
            header_length += 8 + len(metadata[i * 2]) + 8 + 4  # long strLength+fileName+long fileSize+uint alignment
        # Calculate each position for the files (requires alignment)
        file_offsets = []
        running_offset = header_length
        for i in range(0, n_of_files):
            if alignment != 1:
                running_offset += alignment - (running_offset % alignment)
            file_offsets.append(running_offset)
            running_offset += metadata[i * 2 + 1]
        # Write header
        with open(self.dir_path + '.gfs', 'wb') as f:
            # Q ... uint64     L ... uint32
            f.write(struct.pack(BIG_ENDIAN + 'L', header_length))
            GFSWriter.write_pascal_string(f, 'Reverge Package File')
            GFSWriter.write_pascal_string(f, '1.1')
            f.write(struct.pack(BIG_ENDIAN + 'Q', n_of_files))
            for i in range(0, n_of_files):
                GFSWriter.write_pascal_string(f, metadata[i * 2])
                f.write(struct.pack(BIG_ENDIAN + 'Q', metadata[i * 2 + 1]))
                f.write(struct.pack(BIG_ENDIAN + 'L', alignment))
            if f.tell() % alignment != 0:  # Only align if alignment is needed
                f.write(b'\x00' * (alignment - (f.tell() % alignment)))  # Align header if needed
            for i in range(0, n_of_files):
                # Open file, read chunks, write chunks into this file
                with open(os.path.join(self.dir_path, metadata[i * 2].replace("/", "\\")), 'rb') as data_file:
                    bytes_read = data_file.read(4096)
                    while bytes_read:
                        f.write(bytes_read)
                        bytes_read = data_file.read(4096)
                if f.tell() % alignment != 0:  # Only align if alignment is needed
                    f.write(b'\x00' * (alignment - (f.tell() % alignment)))  # Write alignment

    @staticmethod
    def write_pascal_string(f, string):
        ascii_string = string.encode('ascii')
        f.write(struct.pack(BIG_ENDIAN + 'Q', len(ascii_string)))
        f.write(ascii_string)
