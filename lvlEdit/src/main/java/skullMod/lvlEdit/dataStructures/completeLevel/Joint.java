package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.dataStructures.SGS.SGS_Joint;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafAdapter;

import javax.swing.tree.TreeNode;

public class Joint extends LeafAdapter{
    private TreeNode parent;
    public String name;
    public Mat4 matrix;


    public Joint(TreeNode parent, SGS_Joint sgsJoint) {
        super(parent);
        this.parent = parent;

        this.name = sgsJoint.name;

        this.matrix = sgsJoint.matrix;

        //FIXME there is still an unknown field
    }


}
