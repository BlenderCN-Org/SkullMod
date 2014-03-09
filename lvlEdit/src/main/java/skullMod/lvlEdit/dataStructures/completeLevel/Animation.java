package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.SGA.SGA_File;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafContentNode;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

public class Animation extends NodeAdapter{
    public LeafContentNode<String> animationName;
    public LeafContentNode<String> animationFileName;

    public Animation(TreeNode parent) {
        super(parent);

        //TODO attributes won't work
    }

    public Animation(TreeNode parent, String animationName, String animationFileName, SGA_File animation){
        super(parent);

        this.animationName = new LeafContentNode<>(this, "Name", animationName);
        this.animationFileName = new LeafContentNode<>(this, "Filename", animationFileName);


    }


    public int getChildCount() {
        return 2;
    }


    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>(getChildCount());
        list.add(animationName);
        list.add(animationFileName);
        return Collections.enumeration(list);
    }

    public String toString(){
        return animationName.getContent();
    }


}
