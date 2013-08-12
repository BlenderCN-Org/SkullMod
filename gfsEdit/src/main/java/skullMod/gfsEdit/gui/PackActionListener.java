package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.processing.GFS;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PackActionListener implements ActionListener {
    private JLabel pathLabel;
    private JTextField outputFilennameTextField;
    private JCheckBox includeDirectoryNameCheckbox;
    private JRadioButton alignment4kRadioButton;

    public PackActionListener(JLabel pathLabel, JTextField outputFilennameTextField, JCheckBox includeDirectoryNameCheckbox,JRadioButton alignment4kRadioButton){
        //TODO null check
        this.pathLabel = pathLabel;
        this.outputFilennameTextField = outputFilennameTextField;
        this.includeDirectoryNameCheckbox = includeDirectoryNameCheckbox;
        this.alignment4kRadioButton = alignment4kRadioButton;
    }

    public void actionPerformed(ActionEvent e) {
        //TODO what does an unset tooltip return?
        String directoryPath = pathLabel.getToolTipText();
        String outputName = outputFilennameTextField.getText();

        if(outputName.equals("")){ outputName = null; }

        boolean includeDirectoryName = includeDirectoryNameCheckbox.isSelected();
        boolean align4k = alignment4kRadioButton.isSelected();

        GFS.pack(directoryPath,outputName,includeDirectoryName,align4k);
    }
}
