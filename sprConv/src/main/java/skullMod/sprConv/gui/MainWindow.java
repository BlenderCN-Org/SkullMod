package skullMod.sprConv.gui;

import skullMod.sprConv.dataStructures.SPR.*;
import skullMod.sprConv.utility.Utility;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;


public class MainWindow extends JFrame{
    public static final String APPLICATION  = "sprConv";
    public static final String AUTHOR       = "0xFAIL";
    public static final String VERSION      = "0.1 testing";
    public static final String DATE         = "2014-01-02";
    public static final String GAME         = "Skullgirls (PC)";

    // http://stackoverflow.com/questions/10726594/bufferedimage-getrgbx-y-does-not-yield-alpha

    public MainWindow(){
        super(APPLICATION + " " + VERSION); //Set title

        if (Utility.getVersion() < 1.7) {
            JOptionPane.showMessageDialog(null, "Your Java version(" + System.getProperty("java.version") + ") is too low.\nJava 1.7 is required for this application to work properly!\nSome features might not work or crash.", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        /**Set look of the application to mimic the OS GUI*/
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            System.err.println("Setting look and feel failed"); //This should happen silently
        }


        //Create Menu
        JMenuBar menuBar = new JMenuBar();
        /*Menues*/
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        /*MenuItems - File*/
        JMenuItem newFileMenuItem = new JMenuItem("New file");
        JMenuItem saveFileMenuItem = new JMenuItem("Save");
        JMenuItem saveFileAsMenuItem = new JMenuItem("Save as");

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        /*MenuItems - help*/
        JMenuItem helpMenuItem = new JMenuItem("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");

        //Build menu
        fileMenu.add(newFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        fileMenu.add(saveFileAsMenuItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenuItem);

        helpMenu.add(helpMenuItem);
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);



        //Create panels
        JPanel panelLeft = new JPanel();
        DrawPanel panelRight = new DrawPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(panelLeft), new JScrollPane(panelRight));
        splitPane.setOneTouchExpandable(true);

        //Layout and adding
        this.setLayout(new BorderLayout());

        this.add(menuBar, BorderLayout.NORTH);
        this.add(splitPane, BorderLayout.CENTER);

        //*****Misc stuff*****
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //FIXME is this the problem of the awt crash with file/directory select dialogs


        this.setMinimumSize(new Dimension(400,100));
        this.setSize(400,100);

        this.pack();
        this.setVisible(true);



        //this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //TODO new AboutDialog(this, true);


        String fileWithNoExtension = "D:/test/lamia";
        HashMap<String, BufferedImage[]> animations  = convertSPR(fileWithNoExtension);

        panelRight.setImage(animations.get("lamia")[0]);


    }

    private static Dimension getMaxBounds(SPR_Entry[] entries, int blockOffset, int nOfBlocks, int blockWidth, int blockHeight) {
        int xMax = 0, yMax = 0;
        for(int i = blockOffset;i < blockOffset+nOfBlocks;i++){
            //Tile numbers start from 0, to make the start from one 1 is added
            xMax = Math.max(xMax, (entries[i].tile_x+1)*blockWidth);
            yMax = Math.max(yMax, (entries[i].tile_y+1)*blockHeight);
        }
        return new Dimension(xMax,yMax);
    }

    public static void copyRect(BufferedImage source, BufferedImage target, int xSource, int ySource, int xDest, int yDest, int blockWidth, int blockHeight){
        //System.out.println(xSource + " " + ySource + " " + xDest + " " + yDest + " ");
        for(int y = 0;y < blockHeight;y++){
            for(int x = 0;x < blockWidth;x++){
                target.setRGB(xDest*blockWidth + x, yDest*blockHeight + y,source.getRGB(xSource*blockWidth + x,ySource*blockHeight + y));
            }
        }
    }

    public BufferedImage read(File file, int imageIndex) throws IOException{
        Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix("dds");
        if (iterator.hasNext()){
            ImageReader imageReader = iterator.next();
            imageReader.setInput(new FileImageInputStream(file));
            int max = imageReader.getNumImages(true);
            if (imageIndex < max) return imageReader.read(imageIndex);
        }
        return null;
    }

    public static HashMap<String, BufferedImage[]> convertSPR(String sprPath){



        //Why hello there, this is test code
        String fileName = sprPath + ".dds";
        File file = new File(fileName);

        BufferedImage image = null;
        try{
            image = ImageIO.read(file);
        }catch(FileNotFoundException fnfe){
            System.out.println("File not found exception");
        }catch(IOException ioe){
            System.out.println("Error reading file");
            System.out.println(ioe.getMessage());
            ioe.printStackTrace();
        }catch(Exception e){
            System.out.println("Unknown error");
        }

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        System.out.println("Size: " + imageWidth + " " + imageHeight);





        //Loading spr

        String sprFileName = sprPath + ".spr.msb";
        DataStreamIn dsi = null;

        SPR_File spr_file = null;
        try{
            dsi = new DataStreamIn(sprFileName);
            spr_file = new SPR_File(dsi.s);
            dsi.close();

        }catch(IOException ioe){

        }






        HashMap<String, BufferedImage[]> animations = new HashMap<>();

        SPR_Entry[] entries = spr_file.entries;

        int blockWidth = (int) spr_file.blockWidth; //Downcast
        int blockHeight = (int) spr_file.blockHeight; //Downcast

        //Go through each animation
        for(int currentAnimationNumber = 0;currentAnimationNumber < spr_file.animations.length;currentAnimationNumber++){
            SPR_Animation currentAnimation = spr_file.animations[currentAnimationNumber];
            System.out.println("Animation name: " + currentAnimation.animationName);
            int nOfFrames = currentAnimation.nOfFrames;
            int frameOffset = currentAnimation.frameOffset;

            //Create store for frames of animation
            BufferedImage[] animationImages = new BufferedImage[nOfFrames];

            //Go through each frame of the current animation
            for(int frameNumber = 0;frameNumber < nOfFrames;frameNumber++){
                int currentFrameNumber = frameNumber + frameOffset;
                SPR_Frame currentFrame = spr_file.frames[currentFrameNumber];


                int blockOffset = currentFrame.blockOffset;
                int nOfBlocks = currentFrame.nOfBlocks;
                //Determine size by evaluating maximum x/y coordinate
                Dimension imageSize = getMaxBounds(entries, blockOffset, nOfBlocks, blockWidth, blockHeight);

                System.out.println("Frame " + frameNumber + ", size: " + (int) imageSize.getWidth() + "x" + (int) imageSize.getHeight());

                //Create image
                BufferedImage frameImage = new BufferedImage((int) imageSize.getWidth(), (int) imageSize.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

                System.out.println("Reading " + nOfBlocks + " Offset: " + blockOffset);

                for(int blockNumber = blockOffset;blockNumber < blockOffset + nOfBlocks;blockNumber++){
                    copyRect(image,frameImage,entries[blockNumber].tile_u & 0xFF, entries[blockNumber].tile_v & 0xFF, entries[blockNumber].tile_x & 0xFF, entries[blockNumber].tile_y & 0xFF, blockWidth, blockHeight);
                }
                animationImages[frameNumber] = frameImage;
            }

            animations.put(currentAnimation.animationName, animationImages);
        }
        return animations;
    }
}
