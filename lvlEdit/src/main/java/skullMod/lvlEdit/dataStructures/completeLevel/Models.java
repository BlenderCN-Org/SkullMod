package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;

public class Models extends NodeAdapter {
    private final LinkedList<SGM_Model> models;

    public Models(TreeNode parent, SGM_Model[] models) {
        super(parent);

        //TODO verify input

        this.models = new LinkedList<>();

        for(SGM_Model model : models){
            this.models.add(model);
        }
    }

    public Models(TreeNode parent){
        super(parent);
        this.models = new LinkedList<>();
        this.models.add(new SGM_Model(this));
    }

    public String toString(){
        return "Models";
    }

    public int getChildCount() {
        return models.size();
    }

    public Enumeration children() {
        return Collections.enumeration(models);
    }
}
