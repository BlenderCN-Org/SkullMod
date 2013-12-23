package skullMod.lvlEdit.gui.leftPane;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

import static skullMod.lvlEdit.gui.modeChange.ModeChanger.Modes.*;

public class CenterSelectorPanel extends JPanel {
    private JScrollPane scene, model, animation, shape;

    private CardLayout layout;


    public CenterSelectorPanel(){
        layout = new CardLayout();
        this.setLayout(layout);


        //FIXME temp data

        DefaultMutableTreeNode sceneRoot = new DefaultMutableTreeNode("sceneRoot");

        DefaultMutableTreeNode one = new DefaultMutableTreeNode("newaaaaaaaaaaaaaaaaaaa");

        DefaultMutableTreeNode two = new DefaultMutableTreeNode("new");
        DefaultMutableTreeNode three = new DefaultMutableTreeNode("new");
        DefaultMutableTreeNode four = new DefaultMutableTreeNode("new");
        DefaultMutableTreeNode five = new DefaultMutableTreeNode("new");
        DefaultMutableTreeNode six = new DefaultMutableTreeNode("new");
        DefaultMutableTreeNode seven = new DefaultMutableTreeNode("new");


        one.add(two);
        one.add(three);
        three.add(four);
        one.add(five);
        one.add(six);
        one.add(seven);

        sceneRoot.add(one);


        DefaultMutableTreeNode modelRoot = new DefaultMutableTreeNode("modelRoot");
        DefaultMutableTreeNode animationRoot = new DefaultMutableTreeNode("animationRoot");
        DefaultMutableTreeNode shapeRoot = new DefaultMutableTreeNode("shapeRoot");


        scene = new JScrollPane(new JTree(sceneRoot));
        model = new JScrollPane(new JTree(modelRoot));
        animation = new JScrollPane(new JTree(animationRoot));
        shape = new JScrollPane(new JTree(shapeRoot)); //FIXME unknown if this is the correct word for the content

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
