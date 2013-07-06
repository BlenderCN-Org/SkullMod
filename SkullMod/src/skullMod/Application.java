package skullMod;

import skullMod.data.*;

import java.io.*;
import java.util.LinkedList;

/**
 * SkullMod, mod tools for Skullgirls, Steam version
 *
 * Extracts all files in .gfs file
 * Can pack a directory (with all its subdirectorys) back into a .gfs file
 *      May not be sorted the original way, will do that later, maybe.
 * Hi there. Have fun.
 *
 * Now you can change the music in the game to your liking, just be aware, first the intro file playes and then the loop file keeps looping
 */
public class Application {
    public static void main(String[] args){

        LinkedList<ExternalFileReference> referencesList = new LinkedList<>();
        walk("D:\\temp\\","music-win.gfs",referencesList);
        ExternalFileReference[] references = referencesList.toArray(new ExternalFileReference[0]);

        byte[] header = { 0,0,0,0, 0,0,0,0x14, 0x52,0x65,0x76,0x65,0x72,0x67,0x65,0x20,0x50,0x61,0x63,0x6b,0x61,0x67,0x65,0x20,0x46,0x69,0x6c,0x65,
                          0,0,0,0, 0,0,0,3,0x31,0x2e,0x31}; //Hardcoded header, if you're lazy and you know it clap your hands

        int offset = 4+header.length+8; //Offset for data, 51

        for(int i = 0;i < references.length;i++){
            offset += 8; //The string size long
            offset += references[i].internalPath.length();
            offset += 8; //The file size long
            offset += 4; //The unknown, always 1 integer
        }
        System.out.println(offset);
        try {
            DataStreamOut output = new DataStreamOut("D:\\test.gfs");
            output.s.writeInt(offset);
            output.writeBytes(header);
            output.s.writeLong(references.length);
            for(int i = 0;i < references.length;i++){
                output.s.writeLong(references[i].internalPath.length());
                output.s.writeBytes(references[i].internalPath);
                output.s.writeLong(references[i].length);
                output.s.writeInt(1);
            }
            for(int i = 0;i < references.length;i++){
                DataStreamIn input = new DataStreamIn(references[i].absolutePath);
                for(int j = 0;j < references[i].length;j++){
                    output.s.writeByte(input.s.readByte());
                }
            }
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        try {
            DataStreamIn data = new DataStreamIn("D:\\music-win.gfs");
            String base = "D:\\";
            InternalFileReference[] files = GFS.getReferencesGFS(data);
            data.close();
            data = new DataStreamIn("D:\\music-win.gfs");
            data.s.skipBytes(files[0].offset);

            for(int i = 0;i < files.length;i++){
                String basePath = base;
                File tempFile = new File(basePath + files[i].path);
                tempFile.mkdirs();

                System.out.println(basePath);
                System.out.println(files[i].path);
                System.out.println(files[i].name);
                System.out.println(basePath + files[i].path + files[i].name);
                DataStreamOut dataOut = new DataStreamOut(basePath + files[i].path + files[i].name);
                for(int j = 0;j < files[i].length;j++){
                    dataOut.s.writeByte(data.s.readByte());
                }
                dataOut.s.flush();
                dataOut.close();
            }
        } catch (IOException e) {
        }
        */

    }

    public static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        try (RandomAccessFile f = new RandomAccessFile(file, "r")) {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        }
    }
    //http://stackoverflow.com/questions/2056221/recursively-list-files-in-java
    public static void walk( String basePath,String outputFileName,LinkedList<ExternalFileReference> references) {
        File root = new File( basePath );

        String fileBasePath = root.getParentFile().getName();

        File[] list = root.listFiles();

        if (list == null){
            return;
        }

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath(),outputFileName,references);
                //System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
                //System.out.println( "File:" + f.getAbsoluteFile() );

                String absPath = f.getAbsolutePath();
                references.add(new ExternalFileReference(absPath,absPath.substring(fileBasePath.length()-1,absPath.length()).replaceAll("\\\\","/"),f.getName(),f.length()));
            }
        }
    }
}
