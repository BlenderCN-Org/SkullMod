package skullMod.sprConv.gui;

import skullMod.sprConv.dataStructures.SPR.ProcessSPR;
import skullMod.sprConv.dataStructures.SPR.SPR_File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;


public class LoadFileListener implements ActionListener {
    private String lastPath = ".";

    private final Component parent;
    private final SPR_JTree tree;
    private final DrawPanel drawPanel;

    public LoadFileListener(Component parent, SPR_JTree tree, DrawPanel drawPanel){
        this.parent = parent;
        this.tree = tree;
        this.drawPanel = drawPanel;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(lastPath);
        fileChooser.setFileFilter(new SPR_FileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.showOpenDialog(parent);

        File selectedFile = fileChooser.getSelectedFile();
        if(selectedFile != null && selectedFile.exists()){
            lastPath = selectedFile.getParent();
        }

        //Cancel if nothing was selected
        if(selectedFile == null){ JOptionPane.showMessageDialog(parent,"Nothing selected"); return; }

        SPR_File sprFile = ProcessSPR.getSPR_File(selectedFile.getAbsolutePath());
        HashMap<String, BufferedImage[]> animations = ProcessSPR.convertSPR(selectedFile.getAbsolutePath());

        drawPanel.removeImage();

        tree.setModel(sprFile, animations);
    }


    private class SPR_FileFilter extends FileFilter {
        public boolean accept(File f) {
            if(f.isDirectory() || (f.getName().endsWith(".spr.msb") && f.isFile())){
                return true;
            }else{
                return false;
            }
        }
        public String getDescription() {
            return "*.spr.msb";
        }
    }
}
