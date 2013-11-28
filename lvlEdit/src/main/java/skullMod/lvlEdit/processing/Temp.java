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
    public static final String SGI_EXTENSION = ".sgi" + BYTE_ORDER_EXTENSION; //Skull Girls (Index or Info)
    public static final String SGM_EXTENSION = ".sgm" + BYTE_ORDER_EXTENSION; //Skull Girls Model
    public static final String SGA_EXTENSION = ".sga" + BYTE_ORDER_EXTENSION; //Skull Girls Animation
    public static final String SGS_EXTENSION = ".sgs" + BYTE_ORDER_EXTENSION; //Skull Girls (Sprite?)

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


    //Let's go with "constant stuff I don't have to worry about", probably not position or rotation
    //Also java bytes are USELESS because they are signed, (byte) (startByte & 0xFF) is used to upcast the int bits to a byte
}