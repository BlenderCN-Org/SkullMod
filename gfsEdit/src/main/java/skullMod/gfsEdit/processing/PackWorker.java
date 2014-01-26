package skullMod.gfsEdit.processing;

import skullMod.gfsEdit.gui.ModalProgressBarDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PackWorker extends ModalProgressBarDialog.ProgressWorker{
    private final Component parent;
    private final String directoryPath, outputName;
    private final boolean alignment4k, includeDirectoryName;
    public PackWorker(Component parent, String directoryPath,String outputName,  boolean alignment4k, boolean includeDirectoryName){
        this.parent = parent;
        this.directoryPath = directoryPath;
        this.outputName = outputName;
        this.alignment4k  = alignment4k;
        this.includeDirectoryName = includeDirectoryName;
    }

    protected Object doInBackground() {
        //Fill bar
        firePropertyChange("setMaxProgress", 0,1);
        setProgress(1);

        if(directoryPath == null){
            JOptionPane.showMessageDialog(parent, "No directory selected", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        File directory = new File(directoryPath);

        if(!directory.exists()){
            JOptionPane.showMessageDialog(parent,"Directory doesn't exist","Error",JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if(!directory.isDirectory()){
            JOptionPane.showMessageDialog(parent,"Path leads to a file instead of a directory","Error",JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if(outputName != null && !outputName.equals("") && !(outputName.matches("[\\w\\-\\.]+"))){
            if(outputName.startsWith(".") || outputName.endsWith(".")){
                JOptionPane.showMessageDialog(parent,"Leading or tailing dots are not allowed in the output name","Error",JOptionPane.ERROR_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(parent,"Given output filename is not valid","Error",JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }

        if(outputName == null && !directory.getName().matches("[\\w\\-\\.]+")){
            JOptionPane.showMessageDialog(parent,"Input folder name is not valid, rules for it are below \"Output filename\"","Error",JOptionPane.ERROR_MESSAGE);
            return null;
        }

        publish(directory.getName());

        GFS.pack(directory,outputName,includeDirectoryName,alignment4k);

        String outputFile;
        if(outputName == null || outputName.equals("")){
            outputFile = directory.getAbsolutePath() + ".gfs";
        }else{
            outputFile = directory.getAbsolutePath() + File.separator + outputName;
        }

        if(!isCancelled()){ JOptionPane.showMessageDialog(parent,"File created at:\n" + outputFile,"Success",JOptionPane.INFORMATION_MESSAGE); }

        return null;
    }
}
