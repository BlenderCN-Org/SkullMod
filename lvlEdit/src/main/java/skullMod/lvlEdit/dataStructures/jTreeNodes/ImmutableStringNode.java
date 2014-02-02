package skullMod.lvlEdit.dataStructures.jTreeNodes;

import javax.swing.tree.TreeNode;

public class ImmutableStringNode extends LeafAdapter{
    public final String key, value;

    public ImmutableStringNode(TreeNode parent, String key, String value){
        super(parent);

        //TODO verfiy
        this.key = key;
        this.value = value;
    }
    public String toString(){ return value; }
    public String getKey(){ return key; }

    public boolean equals(Object o){
        if(o == null){ return false; }
        if(o instanceof ImmutableStringNode){
            ImmutableStringNode otherNode = (ImmutableStringNode) o;
            if(super.getParent().equals(otherNode.getParent()) && key.equals(otherNode.key) &&
                    value.equals(otherNode.value)){ return true; }
        }
        return false;
    }
}
