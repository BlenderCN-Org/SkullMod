import argparse

import colorama
from colorama import Fore, Back
import sys
import time
from DDS.DDS import DDS

from files import *

import app_info



if __name__ == "__main__":
    colorama.init()

    print(Fore.RED + Back.WHITE + " ██ █ █ █  █ █  █  █   █  ██  ██ ")
    print("█   ██  █  █ █  █  ██ ██ █  █ █ █")
    print(" █  █   █  █ █  █  █ █ █ █  █ █ █")
    print("  █ ██  █  █ █  █  █   █ █  █ █ █")
    print("██  █ █  ██  ██ ██ █   █  ██  ██ " + Fore.RESET + Back.RESET)

    #text_reader = GFS("D:\SteamLibrary\SteamApps\common\Skullgirls Beta\data01\\ved.gfs")
    #text_reader.export_files(text_reader.get_metadata())
    # try:

    # except FileNotFoundError:
    #    print("File not found")
    #except ValueError:
    #    print("Not a valid file")

    #RGBA
    #with open("C:/test.png", 'wb') as output_file:
    #    output_file.write(write_png(bytearray([255,0,0,255]*100),10,10))
    #sample_reader = Reader(open("D:/pyDevelop/SkullModPy/test.txt",'rb'), os.path.getsize("D:/pyDevelop/SkullModPy/test.txt"))
    #print(sample_reader.get_bit_array())

    print("Version: " + app_info.APPLICATION_VERSION + " " + app_info.APPLICATION_DATE)
    print("Made by 0xFAIL\n")

    parser = argparse.ArgumentParser(
        description="Modding tool for the game Skullgirls")

    parser.add_argument('-do', nargs=1, choices=('unpack', 'pack'), help="Default: unpack",
                        required=True)
    parser.add_argument('-gfs', nargs=1, help=".gfs file", metavar='file.gfs', required=False)
    parser.add_argument('-gfs_align', nargs=1, type=int, choices=(1, 4096), default=1, help="Alignment, Default: 1",
                        required=False)
    parser.add_argument('-lvl', nargs=1, metavar='file.lvl', help="Export/import level")
    parser.add_argument('-spr', nargs=1, metavar='file.spr', help="Export/import sprite")

    #if len(sys.argv) == 1:
    #    parser.print_help()
    #    sys.exit(0)

    #args = vars(parser.parse_args())


    #TEST DDS reader
    dds_file = DDS('../dxt5.dds')
    #print(dds_file.bits_to_int(b'\x01\xFF', 7, 8))

    startTime = time.time()

    #dds_file.export_png()
    with open("C:/test.png", 'wb') as output_file:
        output_file.write(bytearray(dds_file.export_png()))

    endTime = time.time()
    print("TIME:", int((endTime-startTime) * 1000))













    #lvl = LVL("D:/SteamLibrary/SteamApps/common/Skullgirls Beta/data01/levels/temp/levels/atrium/background.lvl")
    #sgi = SGI("D:/SteamLibrary/SteamApps/common/Skullgirls Beta/data01/levels/temp/levels/atrium/background.sgi.msb")
    #print(sgi.get_metadata())

