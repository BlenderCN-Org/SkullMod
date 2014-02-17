package skullMod.lvlEdit.gui.leftPane;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.completeLevel.Level;

import javax.swing.*;
import java.awt.*;


public class CenterSelectorPanel extends JPanel {
    private BorderLayout layout;


    public CenterSelectorPanel(){
        layout = new BorderLayout();
        this.setLayout(layout);

        JTree levelTree = new JTree(new Level("C:\\levels\\temp\\levels","class_notes_3d"));
        //JTree levelTree = new JTree(new Level("/home/netbook/Working_files/Skullgirls_extracted/levels/","class_notes_3d"));
        levelTree.addMouseListener(new RightClickListener(levelTree));
        CentralDataObject.level = levelTree;
        this.add(new JScrollPane(levelTree),BorderLayout.CENTER);

    }
}
