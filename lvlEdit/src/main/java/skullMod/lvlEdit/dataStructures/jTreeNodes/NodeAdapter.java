package skullMod.lvlEdit.dataStructures.jTreeNodes;

import javax.swing.tree.TreeNode;

public abstract class NodeAdapter implements TreeNode{
    private final TreeNode parent;
    public NodeAdapter(TreeNode parent){ this.parent = parent; }
    public NodeAdapter(){ this.parent = null; }                     //Root node
    public TreeNode getParent() { return parent; }
    public boolean getAllowsChildren() { return true; }
    public boolean isLeaf() { return false; }
}
