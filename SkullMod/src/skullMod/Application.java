package skullMod;

import skullMod.data.GFS;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * SkullMod, mod tools for Skullgirls, Steam version
 *
 * Hi there. Have fun.
 *
 * Currently limted functionality
 * Reads metadata of a gfs file, only tested with very small files
 */
public class Application {
    public static void main(String[] args){
        try {
            GFS.splitGFS(readFile("D:\\ui.gfs"));
        } catch (IOException e) {
        }
    }

    public static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
