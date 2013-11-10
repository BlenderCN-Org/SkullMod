package skullMod.lvlEdit.gui;

import org.apache.commons.io.IOUtils;
import skullMod.lvlEdit.dataStructures.DataStreamIn;
import skullMod.lvlEdit.dataStructures.SGM_File;
import skullMod.lvlEdit.temp.OneTriangle;
import skullMod.lvlEdit.utility.Utility;

import javax.imageio.ImageIO;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public class MainWindow extends JFrame {
    public static final String APPLICATION  = "LVL edit";
    public static final String AUTHOR       = "0xFAIL";
    public static final String VERSION      = "0.1";
    public static final String DATE         = "2013-29-02";
    public static final String GAME         = "Skullgirls (PC)";


    public final JMenuBar menuBar;
    public final JMenu fileMenu, toolsMenu, aboutMenu;
    public final JMenuItem newLevelMenuItem, loadMenuItem, saveMenuItem, saveAsMenuItem, importMenuItem, exportMenuItem, exitMenuItem;
    public final JMenuItem imageToDDSMenuItem;
    public final JMenuItem aboutMenuItem, helpMenuItem;

    public MainWindow(){
        super(APPLICATION + " " + VERSION); //Set title



        /**Set icon*/
        try {
            InputStream io = Thread.currentThread().getContextClassLoader().getResourceAsStream("appIcon.png");
            this.setIconImage(ImageIO.read(io));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"Couldn't load application icon","Error",JOptionPane.ERROR_MESSAGE);
        }

        /**Set look of the application to mimic the OS GUI*/
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            System.err.println("Setting look and feel failed"); //This should happen silently
        }

        /**Menubar*/
        menuBar = new JMenuBar();
        /*Menues*/
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('t');
        aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('a');
        /*MenuItems - File*/
        newLevelMenuItem = new JMenuItem("New level");
        loadMenuItem = new JMenuItem("Load");
        saveMenuItem = new JMenuItem("Save");
        saveAsMenuItem = new JMenuItem("Save as");
        importMenuItem = new JMenuItem("Import");
        exportMenuItem = new JMenuItem("Export");
        exitMenuItem = new JMenuItem("Exit");

        fileMenu.add(newLevelMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(importMenuItem);
        fileMenu.add(exportMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        /*MenuItems - Tools*/
        imageToDDSMenuItem = new JMenuItem("Image to DDS");

        toolsMenu.add(imageToDDSMenuItem);

        /*MenuItems - Help*/
        aboutMenuItem = new JMenuItem("About");
        helpMenuItem = new JMenuItem("Help");

        aboutMenu.add(aboutMenuItem);
        aboutMenu.add(helpMenuItem);

        /*Add menues to menubar*/
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(aboutMenu);

        this.setJMenuBar(menuBar);

        /**Get default font and make a bold/italic copy of it*/
        Font defaultFont = UIManager.getDefaults().getFont("Label.font");
        Font boldFont = defaultFont.deriveFont(Font.BOLD);
        Font italicFont = defaultFont.deriveFont(Font.ITALIC);

        /**
         * Mainpane
         */
        JTabbedPane contentPane = new JTabbedPane();

        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        glcanvas.setSize(300,300);
        glcanvas.addGLEventListener( new GLEventListener() {

            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                OneTriangle.setup(glautodrawable.getGL().getGL3(), width, height);
            }

            @Override
            public void init( GLAutoDrawable glautodrawable ) {
            }

            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {
            }

            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                OneTriangle.render( glautodrawable.getGL().getGL3(), glautodrawable.getWidth(), glautodrawable.getHeight() );
            }
        });

        contentPane.add("3D",glcanvas);
        contentPane.add("2D image",new JPanel());

        /**
         * Layout
         */
        this.add(contentPane);


        //*****Misc stuff*****
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();
        this.setVisible(true);

        //Issue a warning if Java is not found in required version
        if (Utility.JAVA_VERSION < 1.7) {
            JOptionPane.showMessageDialog(this, "Your Java version(" + System.getProperty("java.version") + ") is too low.\nJava 1.7 is required for this application to work properly!\nSome features might not work.", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        /**
        try {
            DataStreamIn dsi = new DataStreamIn("D:/test.sgm.msb");

            String fileFormatRevision = Utility.readLongPascalString(dsi.s);
            String textureName = Utility.readLongPascalString(dsi.s);
            float unknown1[] = Utility.readFloatArray(dsi.s,new float[13]); //13 entries
            String dataFormat = Utility.readLongPascalString(dsi.s);

            long bytesPerPolygon = dsi.s.readLong();
            long nOfPolygons = dsi.s.readLong();
            long nOfTriangles = dsi.s.readLong();
            long nOfJoints = dsi.s.readLong();

            byte polygonData[] = Utility.readByteArray(dsi.s,new byte[(int) (nOfPolygons*bytesPerPolygon)]);
            short triangleData[] = Utility.readShortArray(dsi.s,new short[(int) (nOfTriangles*3)]);
            float maybeBoundingBox[] = Utility.readFloatArray(dsi.s,new float[6]); //Length = 6 floats = 6*4 = 24 bytes

            String jointNames[] = Utility.readLongPascalStringArray(dsi.s,new String[(int) nOfJoints]);
            byte jointProperties[] = Utility.readByteArray(dsi.s,new byte[(int) (nOfJoints*16)]);



            dsi.close();

            SGM_File file = new SGM_File(fileFormatRevision,textureName,unknown1,dataFormat,bytesPerPolygon,
                    nOfPolygons,nOfTriangles,nOfJoints,polygonData,triangleData,maybeBoundingBox,jointNames,jointProperties);
            System.out.println(file);

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e){
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
         */
    }
}
