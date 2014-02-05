package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class SGA_Animation extends NodeAdapter{
    private String animationName;
    private String animationFileName;

    public SGA_Animation(TreeNode parent) {
        super(parent);
    }

    public int getChildCount() {
        return 0;
    }

    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>();
        return Collections.enumeration(list);
    }

    public String toString(){
        return "Animation";
    }
}
