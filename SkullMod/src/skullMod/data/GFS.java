package skullMod.data;

import java.io.File;
import java.io.IOException;

/**
 * GFS file format
 */
public class GFS {
    public static final String MAGIC_STRING = "Reverge Package File";
    public static final String KNOWN_VERSION_NUMBER = "1.1";

    //Assumes correct file is choosen, currently doesn't check for errors thouroughly
    public static InternalFileReference[] getReferencesGFS(DataStreamIn data) throws IOException {

        int dataOffset = data.s.readInt();
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

        int nOfFiles = (int) data.s.readLong(); //Archive won't have that many files
        System.out.println("Number of files: " + nOfFiles);

        //Length of one entry:
        //Pascal String
        //Length of file (long)
        //Unknown (4 bytes), always 1?

        //Running offset
        int fileOffset = dataOffset;
        InternalFileReference[] result = new InternalFileReference[nOfFiles];

        for(int i = 0;i < nOfFiles;i++){
            File file = new File(readPascalString(data));
            int fileLength = (int) data.s.readLong();
            data.s.readInt(); //Unknown, always 1
            //Create new InternalFileReference
            result[i] = new InternalFileReference(file.getParent(),file.getName(),fileLength,fileOffset,null); //Last param doesn't matter in this case
            fileOffset += fileLength; //The new fileOffset after the current file
        }

        if(fileOffset != data.fileLength){
            throw new IllegalArgumentException("The accumulated file length does not match with the real file length");
        }else{
            System.out.println("Calculated file length and actual file length match");
        }

        Statistics.getMemoryUsage();
        return result;
    }
    private static String byteArray2String(byte[] data){
        StringBuilder sb = new StringBuilder(data.length);
        for (byte character : data) {
            if (character < 0) throw new IllegalArgumentException();
            sb.append((char) character);
        }
        return sb.toString();
    }

    private static String readPascalString(DataStreamIn data) throws IOException{
        int length = (int) data.s.readLong();
        return byteArray2String(readBytes(data,length));
    }

    private static byte[] readBytes(DataStreamIn data, int nOfBytes) throws IOException{
        byte[] result = new byte[nOfBytes];
        for(int i = 0;i < nOfBytes;i++){
            result[i] = data.s.readByte();
        }
        return result;
    }
}
