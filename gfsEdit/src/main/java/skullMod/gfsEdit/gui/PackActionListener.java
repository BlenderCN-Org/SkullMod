package skullMod.gfsEdit.gui;

import skullMod.gfsEdit.processing.GFS;

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
        //TODO null check
        this.pathLabel = pathLabel;
        this.outputFilennameTextField = outputFilennameTextField;
        this.includeDirectoryNameCheckbox = includeDirectoryNameCheckbox;
        this.alignment4kRadioButton = alignment4kRadioButton;
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        //TODO can the jtextfield return null?

        String directoryPath = pathLabel.getToolTipText();
        if(directoryPath == null){
            JOptionPane.showMessageDialog(parent,"No directory selected","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        File directory = new File(pathLabel.getToolTipText());

        if(!directory.exists()){
            JOptionPane.showMessageDialog(parent,"Directory doesn't exist","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!directory.isDirectory()){
            JOptionPane.showMessageDialog(parent,"Path leads to a file instead of a directory","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        String outputName = outputFilennameTextField.getText();

        if(outputName != null && !outputName.equals("") && !(outputName.matches("[\\w\\-\\.]+"))){
            if(outputName.startsWith(".") || outputName.endsWith(".")){
                JOptionPane.showMessageDialog(parent,"Leading or tailing dots are not allowed in the output name","Error",JOptionPane.ERROR_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(parent,"Given output filename is not valid","Error",JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        //TODO check if there is a file in place of the file to create, ask user to overwrite, extra checkbox?

        if(outputName == null && !directory.getName().matches("[\\w\\-\\.]+")){
            JOptionPane.showMessageDialog(parent,"Input folder name is not valid, rules for it are below \"Output filename\"","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean includeDirectoryName = includeDirectoryNameCheckbox.isSelected();
        boolean align4k = alignment4kRadioButton.isSelected();

        //outputName may be null
        GFS.pack(directory,outputName,includeDirectoryName,align4k);

        JOptionPane.showMessageDialog(parent,"File was written in the parent directory of the given directory","Success",JOptionPane.INFORMATION_MESSAGE);
    }
}
