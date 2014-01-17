package skullMod.sprConv.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DrawPanel extends JPanel{
    public BufferedImage image;

    public final ReentrantLock imageLock = new ReentrantLock();

    public static final Color darkBackground = new Color(100,100,100);
    public static final Color lightBackground = new Color(150,150,150);

    public DrawPanel(){
        this.setOpaque(true);
        this.setBackground(new Color(255,255,255));
    }

    public void setImage(BufferedImage image){
        if(image == null){ throw new IllegalArgumentException("Given image is null, use remove image"); }
        imageLock.lock();
        this.image = image;
        imageLock.unlock();
        this.repaint();
    }

    public void removeImage(){
        imageLock.lock();
        this.image = null;
        imageLock.unlock();
        this.repaint();
    }

    public Dimension getPreferredSize(){
        imageLock.lock();

        if(image != null){

            Dimension dimension = new Dimension(image.getWidth() + 16*2, image.getHeight() + 16*2);
            imageLock.unlock();
            return dimension;
        }else{
            imageLock.unlock();
            return getMinimumSize();
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g); //Explain why

        final int translation = 10;
        g.translate(translation,translation);
        drawOrigin(g, this.getSize(), translation);


        //Draw grid (16x16 the size of each block)
        drawCheckerGrid(g,this.getSize(), translation,16);
        //g.fillRect(0,0,100,100);

        try{
            if(imageLock.tryLock(1, TimeUnit.SECONDS)) {
                if(image != null){
                    g.drawImage(image, 0, 0, null);

                    System.out.println("Image was drawn");
                }
                imageLock.unlock();
            }else{
                this.repaint(); //TODO is this a bad idea, probably...
            }
        }catch(InterruptedException ie){
        }finally{
        }
    }
    public static void drawOrigin(Graphics g, Dimension d, int translation){
        g.setColor(new Color(0,0,0));
        g.drawLine(-translation,-1,(int) d.getWidth(),-1);
        g.drawLine(-1,-translation,-1,(int) d.getHeight());
        //g.getBounds ?
    }

    public static void drawCheckerGrid(Graphics g, Dimension d, int translation, int checkerSize){
        int fieldsToTheRight = (int) Math.ceil((d.getWidth()-translation)/checkerSize); //Check ceil
        int fieldsToTheLeft = (int) Math.ceil((double)translation/checkerSize);
        int fieldsBelow = (int) Math.ceil((d.getHeight()-translation)/checkerSize);
        int fieldsAbove = (int) Math.ceil((double) translation/checkerSize);

        /*
        System.out.println("Fields to the right: " + fieldsToTheRight);
        System.out.println("Fields to the left: " + fieldsToTheLeft);
        System.out.println("Fields above: " + fieldsAbove);
        System.out.println("Fields below: " + fieldsBelow);
        System.out.println("====================");
        */
        //Step 1, draw everything RIGHT and BELOW the origin
        for(int y = 0;y < fieldsBelow;y++){
            for(int x = 0;x < fieldsToTheRight;x++){
                if( (x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)){
                    g.setColor(lightBackground);
                }else{
                    g.setColor(darkBackground);
                }
                g.fillRect(x*checkerSize,y*checkerSize,checkerSize,checkerSize);
            }

        }

        //TODO does this look good?
        //Step 2, draw everything LEFT and BELOW the origin

        //Step 3, draw everything LEFT and ABOVE the origin

        //Step 4, draw everything RIGHT and ABOVE the origin

    }
}
