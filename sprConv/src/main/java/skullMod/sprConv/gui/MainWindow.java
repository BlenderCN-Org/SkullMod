package skullMod.sprConv.gui;

import skullMod.sprConv.dataStructures.SPR.DataStreamIn;
import skullMod.sprConv.dataStructures.SPR.SPR_Entry;
import skullMod.sprConv.dataStructures.SPR.SPR_File;
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

        this.pack();
        this.setVisible(true);

        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        //TODO new AboutDialog(this, true);



        //Why hello there, this is test code
        String fileName = "D:/test/loading_en.dds";
        File file = new File(fileName);

        BufferedImage image = null;
        try{
            image = ImageIO.read(file);
        }catch(FileNotFoundException fnfe){
            System.out.println("File not found exception");
        }catch(IOException ioe){
            System.out.println("Error reading file");
            System.out.println(ioe.getMessage());
        }catch(Exception e){
            System.out.println("Unknown error");
        }

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        System.out.println("Size: " + imageWidth + " " + imageHeight);


        //Loading spr

        String sprFileName = "D:/test/loading_en.spr.msb";
        DataStreamIn dsi = null;

        SPR_File spr_file = null;
        try{
            dsi = new DataStreamIn(sprFileName);
            spr_file = new SPR_File(dsi.s);

        }catch(IOException ioe){

        }

        BufferedImage result = new BufferedImage(imageWidth, imageHeight*5, BufferedImage.TYPE_INT_ARGB);

        int cellSize = 16;
        int counter = 0;
        int yOffset = 0;

        for(SPR_Entry entry : spr_file.entries){
            if(counter != 0 && counter % 480 == 0){
                yOffset+=300;
            }
            copyRect(image,result, entry.tile_u & 0xFF, entry.tile_v & 0xFF, entry.tile_x & 0xFF, entry.tile_y & 0xFF,cellSize, yOffset);
            counter++;
        }

        panelRight.setImage(result);
        panelRight.repaint();

    }

    public static void copyRect(BufferedImage source, BufferedImage target, int xSource, int ySource, int xDest, int yDest, int tileSize, int yOffset){
        System.out.println(xSource + " " + ySource + " " + xDest + " " + yDest + " ");
        for(int y = 0;y < tileSize;y++){
            for(int x = 0;x < tileSize;x++){
                target.setRGB(xDest*tileSize + x, yDest*tileSize + y  + yOffset,source.getRGB(xSource*tileSize + x,ySource*tileSize + y));
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
}
