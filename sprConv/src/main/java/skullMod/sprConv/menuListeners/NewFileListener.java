package skullMod.sprConv.menuListeners;

import skullMod.sprConv.dataStructures.SPR.SPR_File;
import skullMod.sprConv.gui.SPR_JTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class NewFileListener implements ActionListener{
    SPR_JTree tree;

    public NewFileListener(SPR_JTree tree){
        this.tree = tree;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        tree.setModel(new SPR_File(), new HashMap<String, BufferedImage[]>());
    }
}
