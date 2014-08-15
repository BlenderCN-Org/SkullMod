package skullMod.gfsEdit.gui.listeners;

import skullMod.gfsEdit.gui.ModalProgressBarDialog;
import skullMod.gfsEdit.processing.GFS;
import skullMod.gfsEdit.processing.PackWorker;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class PackActionListener implements ActionListener {
    private JLabel pathLabel;
    private JTextField outputFilennameTextField;
    private JCheckBox includeDirectoryNameCheckbox;
    private JRadioButton alignment4kRadioButton;

    private JFrame parent;

    public PackActionListener(JFrame parent, JLabel pathLabel, JTextField outputFilennameTextField, JCheckBox includeDirectoryNameCheckbox,JRadioButton alignment4kRadioButton){
        if(parent == null || pathLabel == null || outputFilennameTextField == null || includeDirectoryNameCheckbox == null || alignment4kRadioButton == null){
            throw new IllegalArgumentException("An UI element is null");
        }
        this.pathLabel = pathLabel;
        this.outputFilennameTextField = outputFilennameTextField;
        this.includeDirectoryNameCheckbox = includeDirectoryNameCheckbox;
        this.alignment4kRadioButton = alignment4kRadioButton;
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        new ModalProgressBarDialog(parent, "Pack .gfs",new PackWorker(parent, pathLabel.getToolTipText(), outputFilennameTextField.getText(), alignment4kRadioButton.isSelected(), includeDirectoryNameCheckbox.isSelected()));
    }
}
