package skullMod.lvlEdit.gui.menuListeners;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.completeLevel.Level;
import skullMod.lvlEdit.dataStructures.completeLevel.SkullmodJTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewLevelListener implements ActionListener{
    public void actionPerformed(ActionEvent e) {
        CentralDataObject.level.setModel(new SkullmodJTree.SkullmodTreeModel(new Level()));
        //FIXME update opengl parts (see todo)
    }
}
