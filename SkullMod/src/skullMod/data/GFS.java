package skullMod.data;

import java.nio.ByteBuffer;

/**
 *
 */
public class GFS {
    public static final String MAGIC_STRING = "Reverge Package File";
    public static final String KNOWN_VERSION_NUMBER = "1.1";

    private static int offset = 0;

    //Assumes correct file is choosen, currently doesn't check for errors
    public static BinaryFile[] splitGFS(byte[] data){
        offset = 0; //Files aren't bigger than a few hundred megabytes

        int dataOffset = readInt(data);
        System.out.println("Offset to data portion of GFS: " + dataOffset);

        if(readPascalString(data).equals(MAGIC_STRING)){  //Read magic string
            System.out.println("Found Magic string");
        }else{
            throw new IllegalArgumentException("Magic string not found");
        }

        if(readPascalString(data).equals(KNOWN_VERSION_NUMBER)){
            System.out.println("Found fitting version number");
        }else{
            throw new IllegalArgumentException("Version number is unknown");
        }

        long nOfFiles = readLong(data);
        System.out.println("Number of files: " + nOfFiles);

        //Length of one entry:

        //Pascal String
        //Length of file (long)
        //Unknown (4 bytes), always 1?
        for(int i = 0;i < nOfFiles;i++){
            System.out.println("Filename:" + readPascalString(data));
            System.out.println("Length: " + readLong(data));
            System.out.println("Unknown: " + readInt(data));
        }



        return null;
    }
    private static byte[] byteSubArray(byte[] array, int length){
        byte[] result = new byte[length];
        System.arraycopy(array,offset,result,0,length);
        addToOffset(length);
        return result;
    }
    private static String byteArray2String(byte[] data){
        StringBuilder sb = new StringBuilder(data.length);
        for (int i = 0; i < data.length; ++ i) {
            if (data[i] < 0) throw new IllegalArgumentException();
            sb.append((char) data[i]);
        }
        return sb.toString();
    }
    private static int readInt(byte[] array){
        return ByteBuffer.wrap(byteSubArray(array, 4)).getInt();
    }
    private static long readLong(byte[] array){
        return ByteBuffer.wrap(byteSubArray(array,8)).getLong();
    }

    private static String readPascalString(byte[] data){
        int length = (int) readLong(data); //I really hope the skullgirls people use no string longer than an int can hold
        return byteArray2String(byteSubArray(data,length));
    }

    //Skip is called when an unknown part of data is skipped
    private static void skip(int i){ addToOffset(i);}
    //Padding is called when a known padding location is skipped TODO check if all elements are 0x00?
    private static void padding(int i){ skip(i);}
    //addToOffset is called when an internal method reads
    private static void addToOffset(int i){ offset += i; }
}
