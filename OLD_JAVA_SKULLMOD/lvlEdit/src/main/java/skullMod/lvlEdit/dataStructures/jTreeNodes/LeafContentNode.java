package skullMod.lvlEdit.dataStructures.jTreeNodes;

import javax.swing.tree.TreeNode;

public class LeafContentNode<T> extends LeafAdapter{
    private final String key;
    private T value;
    public LeafContentNode(TreeNode parent, String key,T value) {
        super(parent);
        this.key = key;
        this.value = value;
    }

    public T getContent(){ return value; }
    public void setContent(T object){ this.value = object; }

    public String toString(){
        //TODO verify
        return key + ": " + value.toString();
    }
}
