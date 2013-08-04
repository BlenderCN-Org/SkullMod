package skullMod.gfsEdit.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SelectDirectoryListener implements ActionListener{
    private JFileChooser directoryChooser;
    private JFrame parent;
    private JLabel selectDirectoryLabel;

    public SelectDirectoryListener(JFrame parent, JLabel selectDirectoryLabel){
        if(parent == null){ throw new IllegalArgumentException("No parent provided"); }
        if(selectDirectoryLabel == null){ throw new IllegalArgumentException("No label provided"); }

        this.parent = parent;
        this.selectDirectoryLabel = selectDirectoryLabel;

        directoryChooser = new JFileChooser(new File("."));
        directoryChooser.setDialogTitle("Select directory");
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setAcceptAllFileFilterUsed(false);
    }
    public void actionPerformed(ActionEvent e) {
        if(directoryChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION){
            String directoryName = directoryChooser.getSelectedFile().getName();
            String absPath = directoryChooser.getSelectedFile().getAbsolutePath();
            selectDirectoryLabel.setText(directoryName);
            selectDirectoryLabel.setToolTipText(absPath);
        }
    }
    public File getSelectedFile(){
        return directoryChooser.getSelectedFile();
    }
}
