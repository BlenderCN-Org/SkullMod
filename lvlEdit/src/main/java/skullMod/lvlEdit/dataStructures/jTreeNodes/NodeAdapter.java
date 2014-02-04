package skullMod.lvlEdit.dataStructures.jTreeNodes;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

public abstract class NodeAdapter implements TreeNode{
    private final TreeNode parent;
    public NodeAdapter(TreeNode parent){ this.parent = parent; }
    public TreeNode getParent() { return parent; }
    public boolean getAllowsChildren() { return true; }
    public boolean isLeaf() { return false; }

    public TreeNode getChildAt(int childIndex) {
        Enumeration<TreeNode> nodes = children();
        int index = 0;
        while(nodes.hasMoreElements()){
            TreeNode childNode = nodes.nextElement();
            if(childIndex == index){
                //DEBUG System.out.println("Node " + this.toString() + " requests node number " + childIndex + " and gets " + childNode.toString() + " at " + index);
                return childNode;
            }
            index++;
        }

        return null;
    }

    public int getIndex(TreeNode node) {
        Enumeration<TreeNode> nodes = children();
        int index = 0;
        while(nodes.hasMoreElements()){
            if(node == nodes.nextElement()){
                return index;
            }
            index++;
        }

        return -1;
    }
}
