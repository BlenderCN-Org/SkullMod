package skullMod;

import skullMod.data.*;

import java.io.*;

/**
 * SkullMod, mod tools for Skullgirls, Steam version
 *
 * Extracts all files in .gfs file
 * Hi there. Have fun.
 */
public class Application {
    public static void main(String[] args){
        try {
            DataStreamIn data = new DataStreamIn("D:\\characters-art.gfs");
            String base = "D:\\";
            InternalFileReference[] files = GFS.getReferencesGFS(data);
            data.close();
            data = new DataStreamIn("D:\\characters-art.gfs");
            data.s.skipBytes(files[0].offset);

            for(int i = 0;i < files.length;i++){
                String basePath = base + "\\";
                File tempFile = new File(basePath + files[i].path);
                tempFile.mkdirs();

                System.out.println(basePath);
                System.out.println(files[i].path);
                System.out.println(files[i].name);
                System.out.println(basePath + files[i].path + files[i].name);
                DataStreamOut dataOut = new DataStreamOut(basePath + files[i].path + files[i].name);
                for(int j = 0;j < files[i].length;j++){
                    dataOut.s.writeByte(data.s.readByte()); //Oh java why
                }
                dataOut.s.flush();
                dataOut.close();
            }
        } catch (IOException e) {
        }
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
}
