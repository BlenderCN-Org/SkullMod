package skullMod.lvlEdit.gui.leftPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

import static skullMod.lvlEdit.gui.modeChange.ModeChanger.Modes.*;

public class CenterSelectorPanel extends JPanel {
    private JTree scene, model, animation, shape;

    private CardLayout layout;


    public CenterSelectorPanel(){
        layout = new CardLayout();
        this.setLayout(layout);


        //FIXME temp data

        DefaultMutableTreeNode sceneRoot = new DefaultMutableTreeNode("sceneRoot");
        DefaultMutableTreeNode modelRoot = new DefaultMutableTreeNode("modelRoot");
        DefaultMutableTreeNode animationRoot = new DefaultMutableTreeNode("animationRoot");
        DefaultMutableTreeNode shapeRoot = new DefaultMutableTreeNode("shapeRoot");


        scene = new JTree(sceneRoot);
        model = new JTree(modelRoot);
        animation = new JTree(animationRoot);
        shape = new JTree(shapeRoot); //FIXME unknown if this is the correct word for the content

        this.add(scene,SCENE.toString());
        this.add(model,MODEL.toString());
        this.add(animation,ANIMATION.toString());
        this.add(shape,SHAPE.toString());

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
