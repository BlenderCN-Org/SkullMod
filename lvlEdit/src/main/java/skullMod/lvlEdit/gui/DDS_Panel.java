package skullMod.lvlEdit.gui;


import skullMod.lvlEdit.gui.dds_info.Animation;
import skullMod.lvlEdit.gui.dds_info.InfoRectangle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DDS_Panel extends JPanel{
    private final Color modelColor = new Color(255,0,0,128);
    private final Color animationColor = new Color(255,255,0,128);


    private int drawOffset = 10; //Offset for all draw operations

    private String fileName;
    private BufferedImage image;
    private InfoRectangle[] models;
    private Animation[] animations;

    public DDS_Panel(String fileName){
        //TODO check incoming params and throw fitting exceptions

        this.fileName = fileName;

        File file = new File(fileName);
        try{
            image = ImageIO.read(file);
        }catch(IOException ioe){
            System.out.println("IO Exception");
        }
    }

    //remove after testing
    public void setModels(InfoRectangle[] models){
        this.models = models;
    }

    //remove after testing
    public void setAnimations(Animation[] animations){
        this.animations = animations;
    }

    //FIXME size of box with content? (-1 and +1 pixel on the outer parts)
    //FIXME check if models and animations exist before drawing, is this even necessary with for-each and null?
    //FIXME text may go beyond the border, checkbox for fitting with size/cutting off, checkbox for hiding text
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        g.translate(drawOffset, drawOffset);

        drawOrigin(g);

        if(image != null){ g.drawImage(image, 0, 0, null); }

        int fontHeight = g.getFont().getSize(); //FIXME there has to be a better way to determine font height in px
        System.out.println(fontHeight);

        g.setColor(modelColor);
        for(InfoRectangle rectangle : models){

            g.drawRect(rectangle.getPoint1().getX()-1, rectangle.getPoint1().getY()-1, rectangle.getWidth()+2, rectangle.getHeight()+2);
            g.drawString(rectangle.getName(),rectangle.getPoint1().getX(), rectangle.getPoint1().getY() + fontHeight);
        }

        g.setColor(animationColor);
        for(Animation animation : animations){
            for(InfoRectangle rectangle : animation.getFramesArray()){
                drawOutlineOfRectangle(g, rectangle);
                g.drawString(rectangle.getName(), rectangle.getPoint1().getX(), rectangle.getPoint1().getY() + fontHeight);
            }
        }
    }

    //Draw the outline of the given rectangle (this means everything that is INSIDE the outline is visible
    public void drawOutlineOfRectangle(Graphics g, InfoRectangle rectangle){
        g.drawRect(rectangle.getPoint1().getX() - 1, rectangle.getPoint1().getY() - 1, rectangle.getWidth() + 2, rectangle.getHeight() + 2);
    }

    public void drawOrigin(Graphics g){
        g.drawLine(-5,-1,5,-1);
        g.drawLine(-1,-5,-1,5);
        g.drawString("0",(-1) * g.getFontMetrics().charWidth('0') - 1,-1); // (-1) * for invert
    }




    public Dimension getPreferredSize(){
        if(image != null){
            return new Dimension(image.getWidth() + drawOffset*2, image.getHeight() + drawOffset*2);
        }else{
            return new Dimension(0,0);
        }
    }
}
