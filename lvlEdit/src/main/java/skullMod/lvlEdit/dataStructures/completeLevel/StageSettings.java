package skullMod.lvlEdit.dataStructures.completeLevel;


import skullMod.lvlEdit.dataStructures.LVL.LVL_File;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafContentNode;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;
import skullMod.lvlEdit.utility.Dimension2D;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class StageSettings extends NodeAdapter{
    public LeafContentNode<Dimension2D<Integer>> stageSize;
    public LeafContentNode<Integer> bottomClearance;
    public LeafContentNode<Integer> startPlayer1;
    public LeafContentNode<Integer> startPlayer2;

    public LeafContentNode<Float> fieldOfView; //In degrees
    public LeafContentNode<Float> zNear, zFar;

    public LeafContentNode<Float> tiltRate;
    public LeafContentNode<Float> tiltHeight1, tiltHeight2;

    public LeafContentNode<Integer> shadowDistance;

    public LeafContentNode<String> rel2dFileName;

    //Thanks MikeZ
    private static final int stageSizeDefaultX = 3750, stageSizeDefaultY = 2000;
    public static final int defaultShadowDistance = -400;

    public StageSettings(TreeNode parent){
        this(parent,stageSizeDefaultX, stageSizeDefaultY,64,1675,2075,66f,3f,20000f,0.001f,0f,200f,defaultShadowDistance,"temp/levels/2D/default2DFile");
    }

    public StageSettings(TreeNode parent, LVL_File lvl){
        super(parent);

        //TODO verfiy
        this.stageSize = new LeafContentNode<>(this,"Stage size",new Dimension2D<>(null, lvl.stageSize.width, lvl.stageSize.height));
        this.bottomClearance = new LeafContentNode<>(this,"Bottom clearance",lvl.bottomClearance);
        this.startPlayer1 = new LeafContentNode<>(this,"Start player 1", lvl.start1);
        this.startPlayer2 = new LeafContentNode<>(this,"Start player 2", lvl.start2);

        this.fieldOfView = new LeafContentNode<>(this,"Field of view", lvl.cameraSetup.fieldOfView);
        this.zNear = new LeafContentNode<>(this,"zNear", lvl.cameraSetup.zNear);
        this.zFar = new LeafContentNode<>(this,"zFar", lvl.cameraSetup.zFar);

        this.tiltRate = new LeafContentNode<>(this,"Tilt rate", lvl.cameraTiltOptions.tiltRate);
        //TODO float only
        this.tiltHeight1 = new LeafContentNode<>(this,"Tilt height 1", (float) lvl.cameraTiltOptions.tiltHeight1);
        this.tiltHeight2 = new LeafContentNode<>(this,"Tilt height 2", (float) lvl.cameraTiltOptions.tiltHeight2);

        this.shadowDistance = new LeafContentNode<>(this, "Shadow distance", lvl.shadowDistance);
        this.rel2dFileName = new LeafContentNode<>(this, "2D background file name", lvl.stageRelPath2D);
    }

    //TODO still relevant? alternativly scrap constructor above
    public StageSettings(TreeNode parent, int xStageSize, int yStageSize, int bottomClearance, int startPlayer1, int startPlayer2,
                         float fieldOfView, float zNear, float zFar, float tiltRate, float tiltHeight1, float tiltHeight2, int shadowDistance, String rel2dFileName){
        super(parent);
        //TODO verfiy
        this.stageSize = new LeafContentNode<>(this,"Stage size",new Dimension2D<>(null, xStageSize,yStageSize));
        this.bottomClearance = new LeafContentNode<>(this,"Bottom clearance",bottomClearance);
        this.startPlayer1 = new LeafContentNode<>(this,"Start player 1", startPlayer1);
        this.startPlayer2 = new LeafContentNode<>(this,"Start player 2", startPlayer2);

        this.fieldOfView = new LeafContentNode<>(this,"Field of view", fieldOfView);
        this.zNear = new LeafContentNode<>(this,"zNear", zNear);
        this.zFar = new LeafContentNode<>(this,"zFar", zFar);

        this.tiltRate = new LeafContentNode<>(this,"Tilt rate", tiltRate);
        this.tiltHeight1 = new LeafContentNode<>(this,"Tilt height 1", tiltHeight1);
        this.tiltHeight2 = new LeafContentNode<>(this,"Tilt height 2", tiltHeight2);

        this.shadowDistance = new LeafContentNode<>(this, "Shadow distance", shadowDistance);
        this.rel2dFileName = new LeafContentNode<>(this, "2D background file name",rel2dFileName);
    }

    public int getChildCount() { return 12; }

    public Enumeration<TreeNode> children() {
        ArrayList<TreeNode> list = new ArrayList<>(getChildCount());
        list.add(stageSize);
        list.add(bottomClearance);
        list.add(startPlayer1);
        list.add(startPlayer2);
        list.add(fieldOfView);
        list.add(zNear);
        list.add(zFar);
        list.add(tiltRate);
        list.add(tiltHeight1);
        list.add(tiltHeight2);
        list.add(shadowDistance);
        list.add(rel2dFileName);
        return Collections.enumeration(list);
    }

    public String toString(){
        return "StageSettings";
    }
}
