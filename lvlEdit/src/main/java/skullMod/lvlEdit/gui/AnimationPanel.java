package skullMod.lvlEdit.gui;

import skullMod.lvlEdit.dataStructures.completeLevel.Animation;

import javax.swing.*;
import java.awt.*;

public class AnimationPanel extends JPanel{
    private Animation animation;


    public AnimationPanel(Animation animation){
        this();
        this.animation = animation;

    }

    public AnimationPanel(){
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
    }

    public void setAnimation(Animation animation){
        if(animation == null){ throw new IllegalArgumentException("Remove animations with removeAnimation()"); }
        this.animation = animation;
    }

    public void removeAnimation(){
        this.animation = null;
        this.repaint();
    }

    public void paint(Graphics g){
        g.setColor(new Color(0,0,0));
        g.fillRect(0,0,10,10);
    }
}
