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

    //znear and far defaults 3 and 20000

    private static final Dimension2D<Integer> stageSizeDefault = new Dimension2D<>(3750,2000);
    private static final int stageSizeDefaultX = 3750;
    private static final int stageSizeDefaultY = 2000;

    public StageSettings(){
        stageSize = new Dimension2D<>(stageSizeDefaultX,stageSizeDefaultY);
        bottomClearance = 64;
        startPlayer1 = 1675;
        startPlayer2 = 2075;
    }

    public StageSettings(Dimension2D<Integer> stageSize, int bottomClearance, int startPlayer1, int startPlayer2){
        //TODO verfiy
        this.stageSize = stageSize;
        this.bottomClearance = bottomClearance;
        this.startPlayer1 = startPlayer1;
        this.startPlayer2 = startPlayer2;
    }

    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    public int getChildCount() { return 4; }

    public int getIndex(TreeNode node) {
        return 0;
    }

    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>();

        return null;
    }
}
