package skullMod.lvlEdit.dataStructures;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class SceneTreeModel implements TreeModel {
    ArrayList<TreeModelListener> listeners = new ArrayList<>();



    public Object getRoot() {
        return null;
    }
    public Object getChild(Object parent, int index) {
        return null;
    }
    public int getChildCount(Object parent) {
        return 0;
    }
    public boolean isLeaf(Object node) {
        return false;
    }
    public void valueForPathChanged(TreePath path, Object newValue) {
    }
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {


    }
}
