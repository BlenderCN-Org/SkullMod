package skullMod.sprConv.menuListeners;

import skullMod.sprConv.dataStructures.SPR.ProcessSPR;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class OpenFileListener implements ActionListener {
    private String lastPath = ".";

    private final Component parent;

    public OpenFileListener(Component parent){
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e){
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

        try{
            HashMap<String, BufferedImage[]> animations = ProcessSPR.convertSPR(selectedFile.getAbsolutePath());

            String absDirPath = selectedFile.getAbsoluteFile().getParent();
            for(String key : animations.keySet()){
                int counter = 0;
                for(BufferedImage frame : animations.get(key)){
                    counter++;
                    ImageIO.write(frame, "png", new File(absDirPath + File.separator + selectedFile.getName().substring(0,selectedFile.getName().length() - ProcessSPR.sprExtension.length()) + "." + key + "." + counter + ".png"));
                }
            }
        }catch(IllegalArgumentException iae){
            JOptionPane.showMessageDialog(parent, iae.getCause().getMessage());
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
