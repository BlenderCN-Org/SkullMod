package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.processing.GFS;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class UnpackActionListener implements ActionListener{
    private JComboBox<File> files;
    private JCheckBox dropTargetCheckboxCreateDirectoryWithFilename,dropTargetCheckboxOverwriteFiles;
    private JFrame parent;

    public UnpackActionListener(JComboBox<File> files,JCheckBox dropTargetCheckboxCreateDirectoryWithFilename,JCheckBox dropTargetCheckboxOverwriteFiles,JFrame parent){
        if(files == null || dropTargetCheckboxCreateDirectoryWithFilename == null || dropTargetCheckboxOverwriteFiles == null){ throw new IllegalArgumentException("Missing UI element"); }

        this.files = files;
        this.dropTargetCheckboxCreateDirectoryWithFilename = dropTargetCheckboxCreateDirectoryWithFilename;
        this.dropTargetCheckboxOverwriteFiles = dropTargetCheckboxOverwriteFiles;
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        ComboBoxModel<File> model = files.getModel();
        int size = model.getSize();

        for(int i=0;i < size;i++){
            try {
                GFS.unpack(model.getElementAt(i), dropTargetCheckboxCreateDirectoryWithFilename.isSelected(), dropTargetCheckboxOverwriteFiles.isSelected());
            } catch (FileNotFoundException fnfe ) {
                JOptionPane.showMessageDialog(parent,fnfe.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioe){
                JOptionPane.showMessageDialog(parent,ioe.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException iae){
                JOptionPane.showMessageDialog(parent,iae.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        if(size>0){
            JOptionPane.showMessageDialog(parent,"Extraction finished\nFiles are in the same directory as the .gfs files","Info",JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
