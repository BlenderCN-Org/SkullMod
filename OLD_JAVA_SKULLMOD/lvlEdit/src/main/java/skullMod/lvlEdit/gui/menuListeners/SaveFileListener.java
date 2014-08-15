package skullMod.lvlEdit.gui.menuListeners;


import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.completeLevel.Level;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SaveFileListener implements ActionListener{
    public void actionPerformed(ActionEvent e) {
        Level level = (Level) CentralDataObject.level.getModel().getRoot();
        level.saveLevel();
    }
}
