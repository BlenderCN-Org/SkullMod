package skullMod.sprConv.dataStructures.SPR;

import java.io.*;

/**
 * Wraps streams for easier usage, length of stream has to be known
 * TODO might not play nice with symlinks?, does it close them correctly?
 *
 */
public class DataStreamIn {
    private final FileInputStream fis;
    private final BufferedInputStream bis;
    public final DataInputStream s;
    public final long fileLength;
    public final String fileName;
    public DataStreamIn(String path) throws FileNotFoundException {
        File file = new File(path);
        if(file.isDirectory()){ throw new IllegalArgumentException("Given filename points to a directory!"); }
        fileLength = file.length();
        fileName = file.getName();
        fis = new FileInputStream(file);
        bis = new BufferedInputStream(fis);
        s = new DataInputStream(bis);
    }

    //TODO check if this is necessary
    public void close(){
        try {
            s.close();
            bis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unknown error while closing streams");
        }
    }

}
