import os
import sys
import argparse

from SkullModPy import app_info
from SkullModPy.formats.dds import DDSReader
from SkullModPy.formats.gfs import GFSReader, GFSWriter
from SkullModPy.formats.pcx import PCXReader
from SkullModPy.formats.spr import SPR

if __name__ == "__main__":
    try:
        print(" ██ █ █ █  █ █  █  █   █  ██  ██ ")
        print("█   ██  █  █ █  █  ██ ██ █  █ █ █")
        print(" █  █   █  █ █  █  █ █ █ █  █ █ █")
        print("  █ ██  █  █ █  █  █   █ █  █ █ █")
        print("██  █ █  ██  ██ ██ █   █  ██  ██ ")
    except UnicodeEncodeError:
        print("Maybe you have a special local set (like japanese)")
        print("Switching to safe output (this is not an error)")
        print(" ## # # #  # #  #  #   #  ##  ## ")
        print("#   ##  #  # #  #  ## ## #  # # #")
        print(" #  #   #  # #  #  # # # #  # # #")
        print("  # ##  #  # #  #  #   # #  # # #")
        print("##  # #  ##  ## ## #   #  ##  ## ")
    print("Version: " + app_info.APPLICATION_VERSION + " " + app_info.APPLICATION_DATE)
    print("Made by 0xFAIL\n")

    parser = argparse.ArgumentParser(description="Modding tool for SkullGirls", prog="SkullMod")

    parser.add_argument('-do', choices=('unpack', 'pack'), help="Default: unpack", default='unpack', required=False)
    parser.add_argument('-gfs', action='store_true', help="Pack/Unpack .gfs", required=False)
    parser.add_argument('-gfs_pack_align', action='store_true', default=False, help="GFS 4k alignment flag", required=False)
    parser.add_argument('-lvl', action='store_true', help="Export/Import level")
    parser.add_argument('-spr', action='store_true', help="Export/Import sprite")
    #parser.add_argument('-spr_charselect', action='store_true', help="Export charselect with palette")
    #parser.add_argument('-spr_charselect_p', action='store', help="Palettenumber for charselect")
    parser.add_argument('-dds', action='store_true', help="Export dds to png (no import)")
    parser.add_argument('-pcx', action='store_true', help="Export pcx to png (no import)")
    parser.add_argument('-files', nargs='+', metavar="f", help="Files or directories to work with", required=True)

    # Don't print an error message if there are no arguments, display help instead
    if len(sys.argv) == 1:
        parser.print_help()
        os.system("pause")  # Windows only
        sys.exit(0)

    args = vars(parser.parse_args())

    if args['lvl'] + args['spr'] + args['gfs'] + args['dds'] + args['pcx'] != 1:  # Check if only one mode was selected
        print("\nError: Select only one filetype to process (lvl/spr/gfs/dds")
        parser.print_help()
        sys.exit(1)
    if args['gfs'] and args['do'] == 'unpack' and args['gfs_pack_align']:
        parser.print_help()
        print("\nError: gfs_pack_align is not required for unpacking, use it for packing only")
        sys.exit(1)

    if args['gfs'] is False and args['gfs_pack_align'] is True:  # Check dependency of gfs_pack_align
        parser.print_help()
        print("\nError: gfs_pack_align without gfs and pack")
        sys.exit(1)

    # Iterate through files
    for file in args['files']:
        print("Processing: " + os.path.basename(file))
        if args['gfs']:
            if args['do'] == 'unpack':
                try:
                    gfs_file = GFSReader(file)
                    gfs_file.export_files(gfs_file.get_metadata())
                    print('Done')
                except Exception as e:
                    print("Please report this error: " + str(e))
                    sys.exit(1)
            elif args['do'] == 'pack':

                if os.path.basename(file) == 'characters-art-pt' and not args['gfs_pack_align']:
                    print('Skipping file: ' + os.path.basename(file))
                    print('USE THE -gfs_pack_align OPTION')
                    print('OR USE "GFS pack aligned.bat" FOR THIS FILE' + '\n')
                    continue
                gfs_file = GFSWriter(file, args['gfs_pack_align'])
                gfs_file.write_content(gfs_file.get_metadata())
                print("Done")
        if args['lvl']:
            if args['do'] == 'unpack':
                print('lvl unpack')
                print('Not implemented yet')
            else:
                print('lvl pack')
                print('Not implemented yet')
        if args['spr']:
            if args['do'] == 'unpack':
                spr = SPR(file)
                spr.read_spr()
                print("Done")
            else:
                print('spr pack')
                print('Not implemented yet')
        if args['dds']:
            if args['do'] == 'unpack':
                print("Unpacking DDS is slow, may take a while")
                dds = DDSReader(file)
                dds.check_destination()
                dds.write_png(dds.get_png_data())
                print("Done")
            else:
                print("Packing DDS is not implemented, use the NVidia Texture Tools")
        if args['pcx']:
            if args['do'] == 'unpack':
                    pcx = PCXReader(file)
                    pcx.check_destination()
                    pcx.write_png(pcx.read_data(pcx.read_metadata())[0])
                    print("Done")
            else:
                print('pcx pack')
                print("Not implemented use GIMP, Photoshop or other tools that can export pcx images")