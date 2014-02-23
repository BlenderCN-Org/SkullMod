package skullMod.lvlEdit.gui.selectorPane;

import skullMod.lvlEdit.dataStructures.CentralDataObject;

import javax.swing.*;
import java.awt.*;


public class CenterSelectorPanel extends JPanel {
    private BorderLayout layout;


    public CenterSelectorPanel(){
        layout = new BorderLayout();
        this.setLayout(layout);

        JTree levelTree = CentralDataObject.level;
        levelTree.addMouseListener(new RightClickListener(levelTree));
        this.add(new JScrollPane(levelTree),BorderLayout.CENTER);

    }
}
