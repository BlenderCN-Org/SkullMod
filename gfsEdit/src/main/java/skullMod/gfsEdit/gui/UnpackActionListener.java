package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.processing.GFS;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

public class UnpackActionListener implements ActionListener{
    private JComboBox<File> files;

    //TODO Add other gui elements (checkboxes)

    public UnpackActionListener(JComboBox<File> files){
        if(files == null){ throw new IllegalArgumentException("Missing UI element"); }
        this.files = files;
    }

    public void actionPerformed(ActionEvent e) {
        ComboBoxModel<File> model = files.getModel();
        int size = model.getSize();

        for(int i=0;i < size;i++){
            //TODO handle exception
            try {
                GFS.unpack(model.getElementAt(i), true, true);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
