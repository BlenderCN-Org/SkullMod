package skullMod.lvlEdit.gui;

import skullMod.lvlEdit.dataStructures.completeLevel.Animation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AnimationPanel extends JPanel{
    private final Color animationColor = new Color(255,255,0,128);

    private Animation animation;
    private BufferedImage image;
    private String fileName;

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


    //TODO param is actually path?
    //TODO don't load image it it is the same as the already loaded one
    //This is quite simple, make a new thread that loads the image, when the image is loaded run the repaint on the EDT thread again
    public void changeImage(final String fileName){
        Runnable changeImage = new Runnable() {
            public void run() {
                if(fileName != null && (!fileName.equals(AnimationPanel.this.fileName))){
                    File file = new File(fileName);
                    try{
                        image = ImageIO.read(file);
                        AnimationPanel.this.fileName = fileName;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                AnimationPanel.this.repaint();
                            }
                        });

                    }catch(FileNotFoundException fnfe){
                        System.out.println("File not found exception");
                    }catch(IOException ioe){
                        System.out.println("Error reading file");
                    }

                }
            }
        };
        new Thread(changeImage).start();
    }

    public BufferedImage getImage(){ return image; }

    public void paint(Graphics g){


        g.setColor(new Color(0,0,0));
        g.fillRect(0,0,10,10);
    }
}
