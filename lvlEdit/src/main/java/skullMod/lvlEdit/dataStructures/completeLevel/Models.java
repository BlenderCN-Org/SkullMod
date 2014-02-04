package skullMod.lvlEdit.dataStructures.completeLevel;


import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafAdapter;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class Models extends LeafAdapter {


    public Models(TreeNode parent) {
        super(parent);
    }
    public String toString(){
        return "Models";
    }
}
