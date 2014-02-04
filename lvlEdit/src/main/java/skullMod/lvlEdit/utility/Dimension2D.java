package skullMod.lvlEdit.utility;

import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafAdapter;

import javax.swing.tree.TreeNode;

public class Dimension2D<T extends Number> extends LeafAdapter{
    private T x,y;

    public Dimension2D(TreeNode node, T x, T y){
        super(node);
        setX(x);
        setY(y);
    }

    public T getX(){
        return x;
    }

    public T getY(){
        return y;
    }

    public void setX(T x){
        this.x = x;
    }

    public void setY(T y){
        this.y = y;
    }
}
