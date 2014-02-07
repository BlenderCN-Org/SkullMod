package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.SGA.SGA_File;
import skullMod.lvlEdit.dataStructures.SGI.SGI_Animation;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.*;

public class Animations extends NodeAdapter{
    private LinkedList<Animation> animations;

    public Animations(TreeNode parent) {
        super(parent);

        this.animations = new LinkedList<>();
    }

    public Animations(TreeNode parent, SGI_Animation[] animationMetadata, HashMap<String, SGA_File> animations) {
        super(parent);

        this.animations = new LinkedList<>();

        for(SGI_Animation metadata : animationMetadata){
            this.animations.add(new Animation(this, metadata.animationName, metadata.animationFileName, animations.get(metadata.animationName)));
        }
    }

    public int getChildCount() {
        return animations.size();
    }

    public Enumeration children() {
        return Collections.enumeration(animations);
    }

    public String toString(){
        return "Animations";
    }
}
