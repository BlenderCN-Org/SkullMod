package skullMod.lvlEdit.gui.leftPane;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.completeLevel.Level;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

import static skullMod.lvlEdit.gui.modeChange.ModeChanger.Modes.*;

public class CenterSelectorPanel extends JPanel {
    private CardLayout layout;


    public CenterSelectorPanel(){
        layout = new CardLayout();
        this.setLayout(layout);



        //sceneTree.expandRow(0);
        //sceneTree.setRootVisible(false);

        this.add(new JScrollPane(new JTree(new Level())),SCENE.toString());
        this.add(new JScrollPane(CentralDataObject.modelTree),MODEL.toString());
        this.add(new JScrollPane(CentralDataObject.animationTree),ANIMATION.toString());
        this.add(new JScrollPane(CentralDataObject.shapeTree),SHAPE.toString());

        layout.show(this, SCENE.toString());
    }

    public void showCard(String cardName){
        if(cardName == null){ throw new IllegalArgumentException("Given cardName is null"); }
        //FIXME make this dynamic
        if(cardName.equals(SCENE.toString()) || cardName.equals(MODEL.toString()) || cardName.equals(ANIMATION.toString()) || cardName.equals(SHAPE.toString())){
            layout.show(this, cardName);
        }else{
            throw new IllegalArgumentException("Invalid cardName identifier: " + cardName);
        }
    }
}
