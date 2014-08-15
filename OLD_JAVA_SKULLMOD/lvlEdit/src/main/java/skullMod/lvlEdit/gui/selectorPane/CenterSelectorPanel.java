package skullMod.lvlEdit.gui.selectorPane;

import skullMod.lvlEdit.dataStructures.CentralDataObject;

import javax.swing.*;
import java.awt.*;


public class CenterSelectorPanel extends JPanel {
    private BorderLayout layout;


    public CenterSelectorPanel(JFrame frame){
        layout = new BorderLayout();
        this.setLayout(layout);

        JTree levelTree = CentralDataObject.level;
        levelTree.addMouseListener(new RightClickListener(levelTree,frame));
        this.add(new JScrollPane(levelTree),BorderLayout.CENTER);

    }
}
