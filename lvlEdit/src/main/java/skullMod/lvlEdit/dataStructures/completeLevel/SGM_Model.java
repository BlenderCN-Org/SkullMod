package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;

public class SGM_Model extends NodeAdapter{
    private final LinkedList<SGA_Animation> animations;

    private String modelName;
    private String fileName;

    private Mat4 transformationMatrix;
    //TODO unknown 2 bytes see SGI_Element


    public SGM_Model(TreeNode parent, String modelName,SGA_Animation[] animations) {
        super(parent);

        this.animations = new LinkedList<>();

        for(SGA_Animation animation : animations){
            this.animations.add(animation);
        }
    }

    public SGM_Model(TreeNode parent){
        super(parent);

        animations = new LinkedList<>();
    }




    public int getChildCount() {
        return animations.size();
    }

    public Enumeration children() {
        return Collections.enumeration(animations);
    }

    public String toString(){
        return "SGM MODEL (CHANGE)";
    }
}
