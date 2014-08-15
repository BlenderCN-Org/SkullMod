package skullMod.gfsEdit.gui.listeners;

import skullMod.gfsEdit.gui.ModalProgressBarDialog;
import skullMod.gfsEdit.processing.UnpackWorker;

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

        UnpackWorker pw = new UnpackWorker(model, dropTargetCheckboxCreateDirectoryWithFilename.isSelected(), dropTargetCheckboxOverwriteFiles.isSelected(), parent);

        new ModalProgressBarDialog(parent, "Unpack .gfs files", pw);
    }
}
