package skullMod.sprConv.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawPanel extends JPanel{
    public BufferedImage image;

    public DrawPanel(){
        this.setOpaque(true);
        this.setBackground(new Color(255,255,255));
    }

    public void setImage(BufferedImage image){
        if(image == null){ throw new IllegalArgumentException("Given image is null, use remove image"); }
        this.image = image;
    }

    public void removeImage(){
        this.image = null;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g); //Explain why

        final int translation = 10;
        g.translate(translation,translation);
        drawOrigin(g, this.getSize(), translation);


        //Look up synchronized on null
        synchronized (image){
            if(image != null){ g.drawImage(image, image.getWidth(), image.getHeight(),null); }
        }
    }
    public static void drawOrigin(Graphics g, Dimension d, int translation){
        g.setColor(new Color(0,0,0));
        g.drawLine(-translation,-1,(int) d.getWidth(),-1);
        g.drawLine(-1,-translation,-1,(int) d.getHeight());
        //g.getBounds ?
    }
}
