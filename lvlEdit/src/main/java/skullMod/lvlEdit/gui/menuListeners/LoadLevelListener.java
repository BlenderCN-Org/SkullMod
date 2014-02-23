package skullMod.lvlEdit.gui.menuListeners;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.completeLevel.Level;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The file choosers aren't kept. They are buggy, spawning threads and whatnot.
 * Just hoping the garbage collector kills them asap.
 * http://stackoverflow.com/questions/2873449/occasional-interruptedexception-when-quitting-a-swing-application
 * (Not gonna bother fixing this, except people report problems because it's a shutdown problem)
 */
public class LoadLevelListener implements ActionListener{
    public LoadLevelListener(JFrame frame){
        this.parent = frame;
    }

    private JFrame parent;
    private String lastValidDirectory = ".";
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();

        //Those two should not be required, writing them anyway for now TODO test
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);

        chooser.setDialogTitle("Open level");
        chooser.setFileFilter(new ExtensionFileFilter(".lvl"));
        chooser.setCurrentDirectory(new File(lastValidDirectory));
        chooser.showOpenDialog(parent);

        File selectedFile = chooser.getSelectedFile();

        if(selectedFile != null && selectedFile.exists() && selectedFile.isFile()){
            lastValidDirectory = selectedFile.getParentFile().getAbsolutePath();

            String fileName = selectedFile.getName().replaceFirst("[.][^.]+$", ""); //Remove anything after the first '.'

            //FIXME Error checking (IllegalArgumentExcpetion etc.), updating opengl data (fov etc.)!
            CentralDataObject.level.setModel(new DefaultTreeModel(new Level(lastValidDirectory, fileName)));
        }
    }
}
