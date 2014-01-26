package skullMod.gfsEdit.gui.listeners;

import skullMod.gfsEdit.processing.GFS;
import skullMod.gfsEdit.dataStructures.GFSInternalFileReference;

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
        fileList.setListData(new GFSInternalFileReference[0]);
        if(e.getStateChange() == ItemEvent.SELECTED){
            System.out.println("SELECTED");
            File item = (File) e.getItem();

            GFSInternalFileReference[] references = null;
            try {
                references = GFS.getInternalFileReferences(item);
                if(references == null){
                    System.out.println("null was returned for item: " + item.getAbsoluteFile());
                }else{
                    System.out.println("Number of internal file references: " + references.length);
                    fileList.setListData(references); //TODO Should this be set if references is null?
                }

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.out.println("Some file not found, more investigation");
            }



        }
    }
}
