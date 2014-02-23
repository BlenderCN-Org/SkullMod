package skullMod.lvlEdit.gui;

import skullMod.lvlEdit.dataStructures.SGM.UV;
import skullMod.lvlEdit.gui.animationPane.Animation;
import skullMod.lvlEdit.gui.animationPane.InfoRectangle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DDS_Panel extends JPanel{
    private final Color modelColor = new Color(255,0,0,128);
    private final Color animationColor = new Color(255,255,0,128);

    private final Color darkGrayColor = new Color(100,100,100);
    private final Color lightGrayColor = new Color(150,150,150);


    private int drawOffset = 50; //Offset for all draw operations

    private String fileName;
    private BufferedImage image;
    private InfoRectangle[] models;
    private Animation[] animations;
    private UV_Triangle[] triangles;

    public DDS_Panel(){
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
    }

    public DDS_Panel(String fileName){
        this();
        //TODO check incoming params and throw fitting exceptions

        if(fileName == null){ this.fileName = fileName; }
        changeImage(fileName);
    }

    //TODO param is actually path?
    public void changeImage(String fileName){
        if(fileName != null){
            File file = new File(fileName);
            try{
                image = ImageIO.read(file);
                this.fileName = fileName;
                this.repaint();
            }catch(FileNotFoundException fnfe){
                System.out.println("File not found exception");
            }catch(IOException ioe){
                System.out.println("Error reading file");
            }

        }

    }

    public BufferedImage getImage(){ return image; }

    //FIXME remove after testing
    public void setModels(InfoRectangle[] models){
        this.models = models;
    }

    public void setUV_Triangles(UV_Triangle[] triangles){
        this.triangles = triangles;
        repaint();
    }

    //FIXME remove after testing
    public void setAnimations(Animation[] animations){
        this.animations = animations;
    }

    //FIXME size of box with content? (-1 and +1 pixel on the outer parts)
    //FIXME check if models and animations exist before drawing, is this even necessary with for-each and null?
    //FIXME text may go beyond the border, checkbox for fitting with size/cutting off, checkbox for hiding text
    public void paintComponent(Graphics g){
        super.paintComponent(g);


        g.translate(drawOffset, drawOffset);

        //drawChecker(g,drawOffset,drawOffset,10, new Dimension(image.getWidth(), image.getHeight()));

        drawOrigin(g);

        if(image != null){ g.drawImage(image, 0, 0, null); }else{
            System.out.println("No image to draw");
        }

        int fontHeight = g.getFont().getSize(); //FIXME there has to be a better way to determine font height in px

        g.setColor(modelColor);



        for(InfoRectangle rectangle : models){

            g.drawRect(rectangle.getPoint1().getX()-1, rectangle.getPoint1().getY()-1, rectangle.getWidth()+2, rectangle.getHeight()+2);
            g.drawString(rectangle.getName(),rectangle.getPoint1().getX(), rectangle.getPoint1().getY() + fontHeight);
        }
        if(triangles != null){
            for(UV_Triangle triangle : triangles){
                int width = image.getWidth();
                int height = image.getHeight();

                g.drawLine((int) (triangle.uv1.u*width), (int) (height - triangle.uv1.v*height),     (int) (triangle.uv2.u*width), (int) (height - triangle.uv2.v*height));
                g.drawLine((int) (triangle.uv2.u*width), (int) (height - triangle.uv2.v*height),     (int) (triangle.uv3.u*width), (int) (height - triangle.uv3.v*height));
                g.drawLine((int) (triangle.uv3.u*width), (int) (height - triangle.uv3.v*height),     (int) (triangle.uv1.u*width), (int) (height - triangle.uv1.v*height));

            }
        }

        g.setColor(animationColor);
        if(animations != null){
            for(Animation animation : animations){
                for(InfoRectangle rectangle : animation.getFramesArray()){
                    drawOutlineOfRectangle(g, rectangle);
                    g.drawString(rectangle.getName(), rectangle.getPoint1().getX(), rectangle.getPoint1().getY() + fontHeight);
                }
            }
        }
    }

    private void drawChecker(Graphics g, int xOffset, int yOffset, int size, Dimension imageSize) {
        Dimension jPanel = this.getSize();

        g.setColor(lightGrayColor);

        int imageWidth = (int) imageSize.getWidth();
        int imageHeight = (int) imageSize.getHeight();

        final int nOfRows = (int) Math.ceil(((imageHeight - yOffset) / (double) size) / 2.0);
        System.out.println(nOfRows);

        for(int i = 0;i < nOfRows/2;i++){
            g.fillRect(0,i*2*size,size,size);
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
        if(image == null){
            return getMinimumSize();
        }else{
            return new Dimension(image.getWidth() + drawOffset*2, image.getHeight() + drawOffset*2);
        }
    }

    public Dimension getMinimumSize(){
        return new Dimension(100,100);
    }

    public Dimension getMaximumSize(){
        return getPreferredSize();
    }

    public static class UV_Triangle{
        public UV uv1, uv2, uv3;

        public UV_Triangle(UV uv1, UV uv2, UV uv3){
            this.uv1 = uv1;
            this.uv2 = uv2;
            this.uv3 = uv3;
        }

    }
}
