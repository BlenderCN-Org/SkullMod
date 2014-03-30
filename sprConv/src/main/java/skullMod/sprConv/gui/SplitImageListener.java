package skullMod.sprConv.gui;

import skullMod.sprConv.dataStructures.imageProcessing.ImageProcessing;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class SplitImageListener implements ActionListener {
    private String lastPath = ".";
    private final JFrame parent;

    public SplitImageListener(JFrame parent) {
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(lastPath);
        fileChooser.setFileFilter(new Image_FileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.showOpenDialog(parent);

        File selectedFile = fileChooser.getSelectedFile();
        if(selectedFile != null && selectedFile.exists()){
            lastPath = selectedFile.getParent();
        }

        //Cancel if nothing was selected
        if(selectedFile == null){ JOptionPane.showMessageDialog(parent,"Nothing selected"); return; }

        HashMap<String, BufferedImage> channels = ImageProcessing.splitImage(selectedFile);

        String basePath = selectedFile.getAbsolutePath().substring(0,selectedFile.getAbsolutePath().length()-4);
        try {
            ImageIO.write(channels.get("r"), "png", new File(basePath + ".r.png"));
            ImageIO.write(channels.get("g"), "png", new File(basePath + ".g.png"));
            ImageIO.write(channels.get("b"), "png", new File(basePath + ".b.png"));
            ImageIO.write(channels.get("a"), "png", new File(basePath + ".a.png"));
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private class Image_FileFilter extends FileFilter {
        public boolean accept(File f) {
            if(f.isDirectory() || f.getName().endsWith(".png") || f.getName().endsWith(".dds")){
                return true;
            }else{
                return false;
            }
        }
        public String getDescription() {
            return "*.png, *.dds";
        }
    }
}
