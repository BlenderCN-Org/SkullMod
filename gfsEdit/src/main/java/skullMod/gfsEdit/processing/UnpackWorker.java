package skullMod.gfsEdit.processing;

import skullMod.gfsEdit.gui.ModalProgressBarDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UnpackWorker extends ModalProgressBarDialog.ProgressWorker{
    private final ComboBoxModel<File> files;

    private final Component parent;

    private final boolean createDirectoryWithFilename, overwriteFiles;

    public UnpackWorker(ComboBoxModel<File> files, boolean createDirectoryWithFilename, boolean overwriteFiles, Component parent){
        this.files = files;
        this.parent = parent;

        this.createDirectoryWithFilename = createDirectoryWithFilename;
        this.overwriteFiles = overwriteFiles;
    }

    protected Object doInBackground() {
        int size = files.getSize();

        firePropertyChange("setMaxProgress",0,size);

        for(int i=0;i < size;i++){
            if(isCancelled()){
                //Quit thread if cancelled
                return null;
            }

            setProgress(i+1);
            publish(files.getElementAt(i).getName());

            try {
                GFS.unpack(files.getElementAt(i), createDirectoryWithFilename, overwriteFiles);
            } catch (FileNotFoundException fnfe ) {
                JOptionPane.showMessageDialog(null,fnfe.getMessage(),"Error, file not found",JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioe){
                JOptionPane.showMessageDialog(null,ioe.getMessage(),"Error, unknown IO Exception",JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException iae){
                JOptionPane.showMessageDialog(null,iae.getMessage(),"Error, illegal argument exception",JOptionPane.ERROR_MESSAGE);
            }
        }
        if(size>0){
            JOptionPane.showMessageDialog(parent,"Extraction finished\nFiles are in the same directory as the .gfs files","Info",JOptionPane.INFORMATION_MESSAGE);
        }

        return null;
    }


}
