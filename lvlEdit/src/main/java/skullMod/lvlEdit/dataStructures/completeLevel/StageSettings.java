package skullMod.lvlEdit.dataStructures.completeLevel;


import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;
import skullMod.lvlEdit.utility.Dimension2D;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;

public class StageSettings extends NodeAdapter{
    public Dimension2D<Integer> stageSize;
    public int bottomClearance;
    public int startPlayer1;
    public int startPlayer2;

    public float fieldOfView; //In degrees
    public float zNear, zFar;

    public float tiltRate;
    public float tiltHeight1, tiltHeight2;

    private static final Dimension2D<Integer> stageSizeDefault = new Dimension2D<>(3750,2000);

    public StageSettings(){
        stageSize = stageSizeDefault;
        bottomClearance = 64;
        startPlayer1 = 1675;
        startPlayer2 = 2075;

        fieldOfView = 66;
        //Thanks mike
        zNear = 3;
        zFar = 20000;

        tiltRate = 0.001f;
        tiltHeight1 = 0;
        tiltHeight2 = 200;
    }

    public StageSettings(Dimension2D<Integer> stageSize, int bottomClearance, int startPlayer1, int startPlayer2,
                         float fieldOfView, float zNear, float zFar, float tiltRate, float tiltHeight1, float tiltHeight2){
        //TODO verfiy
        this.stageSize = stageSize;
        this.bottomClearance = bottomClearance;
        this.startPlayer1 = startPlayer1;
        this.startPlayer2 = startPlayer2;

        this.fieldOfView = fieldOfView;
        this.zNear = zNear;
        this.zFar = zFar;

        this.tiltRate = tiltRate;
        this.tiltHeight1 = tiltHeight1;
        this.tiltHeight2 = tiltHeight2;
    }

    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    public int getChildCount() { return 10; }

    public int getIndex(TreeNode node) {
        return 0;
    }

    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>();

        return null;
    }

    public String toString(){
        return "StageSettings";
    }
}
