package skullMod.lvlEdit.gui.menuListeners;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ExtensionFileFilter extends FileFilter {
    public final String extension;
    public ExtensionFileFilter(String extension){
        if(extension == null){ throw new IllegalArgumentException("Given extension is null"); }
        this.extension = extension;
    }
    public boolean accept(File f) {
        if(f.isDirectory() || (f.getName().endsWith(this.extension) && f.isFile() && f.length() < 1024*512)){ //If a lvl file is bigger than 512kb something is very wrong
            return true;
        }else{
            return false;
        }
    }
    public String getDescription() {
        return "*" + this.extension;
    }
}