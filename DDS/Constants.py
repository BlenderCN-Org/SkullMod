# DOCUMENTATION FOR DDS: http://msdn.microsoft.com/en-us/library/windows/desktop/bb943991%28v=vs.85%29.aspx

# DDS FOURCC
DDS_MAGIC = bytes('DDS ', 'ascii')
# dwSize
DDS_HEADER_SIZE = 124
# dwFlags (Surface description flags)
# TODO make it constants (lazy, lazy)
DDS_CAPS_FLAG = 2 ** 0          # Bit 1
DDS_HEIGHT_FLAG = 2 ** 1        # Bit 2
DDS_WIDTH_FLAG = 2 ** 2         # Bit 3
DDS_PITCH_FLAG = 2 ** 3         # Bit 4
DDS_PIXELFORMAT_FLAG = 2 ** 12  # Bit 13
DDS_MIPMAPCOUNT_FLAG = 2 ** 17  # Bit 18
DDS_LINEARSIZE_FLAG = 2 ** 19   # Bit 20
DDS_DEPTH_FLAG = 2 ** 23        # Bit 24

# dwReserved (Reserved fields, additional data)
# If 9th int is this string the file was compressed with the Nvidia Texture Tools (NVTT)
NVTT = bytes('NVTT', 'ascii')

# dwFlags (Pixel format flags)
DDSF_ALPHAPIXELS = 2 ** 0    # Bit 1
DDSF_ALPHA = 2 ** 1          # Bit 2
DDSF_FOURCC = 2 ** 2         # Bit 3
DDSF_RGB = 2 ** 6            # Bit 7
DDSF_YUV = 2 ** 9            # Bit 10
DDSF_LUMINANCE = 2 ** 17     # Bit 18

# dwCaps flags
DDSCAPS_COMPLEX = 8
DDSCAPS_MIPMAP = 0x400000
DDSCAPS_TEXTURE = 0x1000

# dwCaps2 flags
DDSCAPS2_CUBEMAP = 0x200
DDSCAPS2_CUBEMAP_POSITIVEX = 0x400
DDSCAPS2_CUBEMAP_NEGATIVEX = 0x800
DDSCAPS2_CUBEMAP_POSITIVEY = 0x1000
DDSCAPS2_CUBEMAP_NEGATIVEY = 0x2000
DDSCAPS2_CUBEMAP_POSITIVEZ = 0x4000
DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x8000
DDSCAPS2_VOLUME = 0x200000