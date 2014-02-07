package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.dataStructures.SGA.SGA_File;
import skullMod.lvlEdit.dataStructures.SGI.SGI_Animation;
import skullMod.lvlEdit.dataStructures.SGI.SGI_Element;
import skullMod.lvlEdit.dataStructures.SGM.SGM_File;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafContentNode;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.*;

public class Model extends NodeAdapter{
    private final Animations animations;


    private LeafContentNode<String> modelName;
    private LeafContentNode<String> fileName;

    private LeafContentNode<Mat4> transformationMatrix;
    //TODO unknown 2 bytes see SGI_Element


    public Model(TreeNode parent){
        super(parent);

        //TODO add other requirements

        animations = new Animations(this);
    }

    public Model(TreeNode parent, SGI_Element modelMetadata, SGM_File model, HashMap<String, SGA_File> animations) {
        super(parent);

        this.modelName = new LeafContentNode<>(this, "Name", modelMetadata.elementName);
        this.fileName = new LeafContentNode<>(this, "File",modelMetadata.modelFileName);

        this.transformationMatrix = new LeafContentNode<>(this, "Transformation matrix", modelMetadata.transformationMatrix);
        this.animations = new Animations(this, modelMetadata.animations, animations);

        //TODO add model attributes

    }


    public int getChildCount() {
        return 4;
    }

    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>(getChildCount());
        list.add(animations);
        list.add(modelName);
        list.add(fileName);
        list.add(transformationMatrix);
        return Collections.enumeration(list);
    }

    public String toString(){
        return modelName.getContent();
    }
}
