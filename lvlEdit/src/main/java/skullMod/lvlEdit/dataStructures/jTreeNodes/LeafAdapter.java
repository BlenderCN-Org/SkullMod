package skullMod.lvlEdit.dataStructures.jTreeNodes;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public class LeafAdapter implements TreeNode{
    private final TreeNode parent;

    public LeafAdapter(TreeNode parent){
        this.parent = parent;
    }
    public TreeNode getChildAt(int childIndex) {
        throw new IllegalArgumentException();
    }
    public int getChildCount() {
        return 0;
    }
    public TreeNode getParent() { return parent; }
    public int getIndex(TreeNode node) { throw new IllegalArgumentException(); }
    public boolean getAllowsChildren() { return false; }
    public boolean isLeaf() { return true; }
    public Enumeration children() { throw new IllegalArgumentException(); }
}
