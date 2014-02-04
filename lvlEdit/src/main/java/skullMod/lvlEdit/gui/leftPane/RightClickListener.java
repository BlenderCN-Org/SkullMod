package skullMod.lvlEdit.gui.leftPane;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RightClickListener extends MouseAdapter {
    private final JTree tree;

    public RightClickListener(JTree tree){
        this.tree = tree;
    }
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = tree.getRowForLocation(e.getX(), e.getY());
            System.out.println("ROW:" +row);
            //int row = tree.getClosestRowForLocation(e.getX(), e.getY());
            if(row == -1){
                tree.setSelectionRow(-1);
            }else{
                tree.setSelectionRow(row);
                ExamplePopup popupMenu = new ExamplePopup();
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

        }
    }
    private class ExamplePopup extends JPopupMenu{
        JMenuItem item;
        public ExamplePopup(){
            item = new JMenuItem("CLICK");
            item.
            add(item);
        }

    }
}

