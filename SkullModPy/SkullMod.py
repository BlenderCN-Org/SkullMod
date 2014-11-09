import sys
import argparse
import traceback

import colorama
from colorama import Fore, Back
from SkullModPy import app_info
from SkullModPy.files import *

if __name__ == "__main__":
    colorama.init()

    print(Fore.RED + " ██ █ █ █  █ █  █  █   █  ██  ██ ")
    print("█   ██  █  █ █  █  ██ ██ █  █ █ █")
    print(" █  █   █  █ █  █  █ █ █ █  █ █ █")
    print("  █ ██  █  █ █  █  █   █ █  █ █ █")
    print("██  █ █  ██  ██ ██ █   █  ██  ██ " + Fore.RESET + Back.RESET)
    print("Version: " + app_info.APPLICATION_VERSION + " " + app_info.APPLICATION_DATE)
    print("Made by 0xFAIL\n")

    parser = argparse.ArgumentParser(description="Modding tool for the game SkullGirls", prog="SkullModPy")

    parser.add_argument('-do', choices=('unpack', 'pack'), help="Default: unpack", default='unpack', required=False)
    parser.add_argument('-gfs', action='store_true', help=".gfs file", required=False)
    parser.add_argument('-gfs_pack_align', action='store_true', default=False, help="GFS Alignment", required=False)
    parser.add_argument('-lvl', action='store_true', help="Export/import level")
    parser.add_argument('-spr', action='store_true', help="Export/import sprite")
    parser.add_argument('-files', nargs='+', metavar="f", help="Files or directories to work on", required=True)

    """
    # Check for availability of NVidia Texture Tools
    # Copy the content of the 'bin' directory of NVTT into a directory called NVTT in the .exe or .py directory
    script_path = os.path.dirname(os.path.realpath(sys.argv[0]))
    nvtt_path = os.path.join(script_path, 'NVTT')
    if not (os.path.exists(nvtt_path) and os.path.isdir(nvtt_path)):
        print('\nError: NVidia Texture Tools are missing (no NVTT directory)')
        sys.exit(1)
    """

    # Don't print an error message if there are no arguments, display help instead
    if len(sys.argv) == 1:
        parser.print_help()
        os.system("pause")  # Windows only
        sys.exit(0)

    args = vars(parser.parse_args())

    if args['lvl'] + args['spr'] + args['gfs'] != 1:  # Check if only one mode was selected
        print("\nError: Select only one filetype to proces (lvl or spr or gfs")
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
    # Simple command line error handling: Display exception and wait for any keypress
    # Using Windows only pause
    try:
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
                        print(Fore.RED + 'USE THE -gfs_pack_align OPTION')
                        print('OR USE "GFS pack aligned.bat" FOR THIS FILE' + Fore.RESET + '\n')
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
                    print('spr unpack')
                    print('Not implemented yet')
                else:
                    print('spr pack')
                    print('Not implemented yet')
    except Exception as err:  # Exception class used on purpose
        print("An error occured")
        exc_info = sys.exc_info()
        traceback.print_exception(*exc_info)
        del exc_info
        # traceback.print_tb(err.__traceback__)