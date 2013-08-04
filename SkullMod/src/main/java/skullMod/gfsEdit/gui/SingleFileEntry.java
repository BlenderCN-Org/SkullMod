package skullMod.gfsEdit.gui;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Check for single file and save path and file name
 */
public class SingleFileEntry {
    public final String absolutePath;
    public final String fileName;
    //Default constructor, creates an empty copy
    public SingleFileEntry(){
        absolutePath = null;
        fileName = "          ";
    }
    public SingleFileEntry(String path) throws FileNotFoundException{
        if(path == null){ throw new IllegalArgumentException("No path given"); }
        File temp = new File(path);
        if(!temp.exists()){ throw new FileNotFoundException(); }
        if(!temp.isFile()){ throw new IllegalArgumentException("Given string \"" + path + "\" does not point to a file"); }
        absolutePath = temp.getAbsolutePath();
        fileName = temp.getName();
    }
    public String toString(){ return fileName; }
}
