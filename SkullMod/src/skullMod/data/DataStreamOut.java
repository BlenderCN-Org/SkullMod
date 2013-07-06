package skullMod.data;

import java.io.*;

/**
 * Wraps streams for easier usage, length of stream has to be known
 */
public class DataStreamOut {
    private final FileOutputStream fis;
    private final BufferedOutputStream bis;
    public final DataOutputStream s;
    public final long fileLength;
    public final String fileName;
    public DataStreamOut(String path) throws FileNotFoundException {
        File file = new File(path);
        if(file.isDirectory()){ throw new IllegalArgumentException("Given filename points to a directory!"); }
        fileLength = file.length();
        fileName = file.getName();
        fis = new FileOutputStream(file);
        bis = new BufferedOutputStream(fis);
        s = new java.io.DataOutputStream(bis);
    }

    public void close(){
        try {
            s.close();
            bis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Uknown error while closing streams");
        }
    }
    //I don't get why there is no straightforward way to write byte arrays without offset, length and the other stuff
    public void writeBytes(byte[] data) throws IOException{
        for(int i = 0;i < data.length;i++){
            s.writeByte(data[i]);
        }
    }

}
