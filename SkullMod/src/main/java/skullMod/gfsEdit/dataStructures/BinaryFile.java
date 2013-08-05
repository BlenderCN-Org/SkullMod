package skullMod.gfsEdit.dataStructures;

import java.io.Serializable;

/**
 * A single file held in memory, used for exporting
 * Use only for files lower than 50 MB
 */
public class BinaryFile implements Serializable {
    public final String filename;
    public final byte[] data;

    public BinaryFile(String filename, byte[] data){
        if(filename == null || filename.equals("")){ throw new IllegalArgumentException("Given filename is null or empty"); }
        if(data == null){ throw new IllegalArgumentException("Given dataStructures is null"); }
        this.filename = filename;
        this.data = data;
    }
}
