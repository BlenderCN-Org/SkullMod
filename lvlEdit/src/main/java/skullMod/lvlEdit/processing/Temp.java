package skullMod.lvlEdit.processing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

public class Temp{
    public static final String LVL_EXTENSION = ".lvl";
    //Is not read or written, is the same as the main .lvl file
    public static final String BACKGROUND_FILENAME = "background" + LVL_EXTENSION;
    public static final String BYTE_ORDER_EXTENSION = ".msb";
    public static final String SGI_EXTENSION = ".sgi" + BYTE_ORDER_EXTENSION; //Skull Girls Index or Info
    public static final String SGM_EXTENSION = ".sgm" + BYTE_ORDER_EXTENSION; //Skull Girls Model
    public static final String SGA_EXTENSION = ".sga" + BYTE_ORDER_EXTENSION; //Skull Girls Animation


    public static void parseLVL(String pathToFile){
        if(pathToFile == null){ throw new NullPointerException("Given pathToFile is null"); }

        //TODO Normalize with commons
        File file = new File(pathToFile);

        if(!(file.exists() && file.isFile())){ throw new IllegalArgumentException(""); }

        //TODO validate entire structure before continuing


        List lines = null;
        try {
             lines = FileUtils.readLines(file);

        } catch (FileNotFoundException fnfe) {
            throw new IllegalArgumentException("Given file " + pathToFile + " does not exist");
        } catch (IOException ioe){
            //TODO what to do with "useless" IOExceptions? Cancel or throw another exception?
        }
    }

    public static void readSGI(String pathToFile){
        if(pathToFile == null){ throw new IllegalArgumentException("Given pathToFile is null"); }

        //TODO normalize with commons
        File file = new File(pathToFile);

        if(!(file.exists() && file.isFile())){ throw new IllegalArgumentException("Not a valid file"); }


    }

    /**
     * Big endian
     *
     * @param dis
     * @return
     * @throws IOException
     */
    public static String readLongPascalString(DataInputStream dis) throws IOException{
        if(dis == null){ throw new IllegalArgumentException("Given stream is null"); }
        long stringLength = dis.readLong();
        if(stringLength < 1 || stringLength > Integer.MAX_VALUE){ throw new IllegalArgumentException("Given stream position results in invalid data: " + stringLength); }
        byte[] data = new byte[(int) stringLength]; //No string is longer than Integer.MAX_VALUE
        dis.read(data);

        return IOUtils.toString(data,"ASCII");
    }


    //Let's go with "constant stuff I don't have to worry about", probably not position or rotation
    //Also java bytes are USELESS because they are signed, (byte) (startByte & 0xFF) is used to upcast the int bits to a byte
    //They are written correctly regardless
    //BTW 0x3F80 is 1.0 as a 32 bit IEEE 754 float

    //12 0x00 bytes
    //6 0x3F800000
    //12 0x00 bytes
    //1 0x42B40000    Unicode Han Character 'food made of rice-flour' (U+42B4) or whatever

    //SGM nOf = triangles, each has a 16 bit index, so nOf * (2*3) follow the data
    //Alwaysnull field is not always null, maybe determins type, geoShape has a different value there (10)
    //Last 24 bytes are random stuff
}