package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.data.GFS;
import skullMod.gfsEdit.data.GFSInternalFileReference;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;

public class FileItemListener implements ItemListener{
    private JList<GFSInternalFileReference> fileList;
    public FileItemListener(JList<GFSInternalFileReference> fileList){
        if(fileList == null){ throw new IllegalArgumentException(); }
        this.fileList = fileList;
    }
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED){
            System.out.println("SELECTED");
            File item = (File) e.getItem();

            GFSInternalFileReference[] references = null;
            try {
                references = GFS.getInternalFileReferences(item);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            System.out.println(references.length);

            fileList.setListData(references);
        }
    }
}
