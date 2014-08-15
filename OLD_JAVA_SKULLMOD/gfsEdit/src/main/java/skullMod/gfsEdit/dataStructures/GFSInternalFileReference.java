package skullMod.gfsEdit.dataStructures;

import skullMod.gfsEdit.gui.MainWindow;

import javax.swing.*;
import java.io.File;

/** A reference of a file inside another file */
public class GFSInternalFileReference {
    public final String path;
    public final String name;
    public final int length;
    public final int offset;
    public final String originalFileName;
    public final int alignment;

    public GFSInternalFileReference(int length, int offset, int alignment){ this(null,null,length,offset,null,alignment); }
    public GFSInternalFileReference(String name, int length, int offset, int alignment){ this(null,name,length,offset,null,alignment); }
    public GFSInternalFileReference(String name, int length, int offset, String originalFileName, int alignment){ this(null,name,length,offset,originalFileName,alignment); }

    public GFSInternalFileReference(String path, String name, int length, int offset, String originalFileName,int alignment){
        if(length < 0){ throw new IllegalArgumentException("length is smaller than 0!"); }
        if(offset < 0){ throw new IllegalArgumentException("offset is smaller than 0!"); }
        if(path.contains(":") || path.contains("..") || path.startsWith("/")){ //Filter out absolute paths and people trying to put things were they don't belong
            JOptionPane.showMessageDialog(MainWindow.window, "Security exception, this file is malicious:\n" + path + File.separator + name);
            throw new IllegalArgumentException("Security exception, this file is malicious");
        }
        //Regex doesn't like \\ for a \ character, you have to escape twice, once for java and one for regex so \\\\ is correct *sigh*
        this.path = path.replaceAll("/","\\\\")+ "\\";
        this.name = name;
        this.length = length;
        this.offset = offset;
        this.originalFileName = originalFileName;
        this.alignment = alignment;
    }
    public String toString(){
        return path + name;
    }
}
