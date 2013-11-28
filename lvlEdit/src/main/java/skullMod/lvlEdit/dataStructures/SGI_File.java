package skullMod.lvlEdit.dataStructures;

import java.io.Serializable;

public class SGI_File implements Serializable{
    public static final String KNOWN_FILE_FORMAT_REVISION = "2.0";
    public String fileFormatRevision;
    public SGI_Element[] elements;

    //TODO do for all classes?
    public static SGI_Element readSGI(DataStreamIn sgi_stream){
        return null;
    }
}
