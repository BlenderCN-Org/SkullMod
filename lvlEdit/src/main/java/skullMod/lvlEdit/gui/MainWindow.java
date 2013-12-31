package skullMod.lvlEdit.gui;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.DataStreamIn;
import skullMod.lvlEdit.dataStructures.SGI.SGI_Element;
import skullMod.lvlEdit.dataStructures.SGI.SGI_File;
import skullMod.lvlEdit.gui.dds_info.Animation;
import skullMod.lvlEdit.gui.dds_info.InfoRectangle;
import skullMod.lvlEdit.gui.dds_info.PixelCoordinate;
import skullMod.lvlEdit.gui.leftPane.SelectorPanel;
import skullMod.lvlEdit.gui.modeChange.ModeChanger;
import skullMod.lvlEdit.gui.rightPane.RightJPane;
import skullMod.lvlEdit.utility.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public class MainWindow extends JFrame {
    public static final String APPLICATION  = "LVL edit";
    public static final String AUTHOR       = "0xFAIL";
    public static final String VERSION      = "0.1 testing";
    public static final String DATE         = "2013-12-28";
    public static final String GAME         = "Skullgirls (PC)";

    public static final String ANIMATION_PANEL = "Animation";
    public static final String IMAGE_PANEL = "2D Image";
    public static final String SCENE_PANEL = "3D";


    public final JMenuBar menuBar;
    public final JMenu fileMenu, toolsMenu, aboutMenu;
    public final JMenuItem newLevelMenuItem, loadMenuItem, saveMenuItem, saveAsMenuItem, closeLevelItem, importMenuItem, exportMenuItem, exitMenuItem;
    public final JMenuItem imageToDDSMenuItem;
    public final JMenuItem aboutMenuItem, helpMenuItem;

    //FIXME
    public final JMenu devMenu;
    public final JMenuItem loadSGI;

    public final JTabbedPane contentPane;

    public MainWindow(){
        super(APPLICATION + " " + VERSION); //Set title

        JOptionPane.showMessageDialog(null,"Hi, todays build has the following new features:\n" +
                "-)Exit button works in File menu\n" +
                "-)Saving sgi files is possible. Not enabled yet.\n" +
                "-)'Remember last path that was selected' in file chooser\n" +
                "-)All models are listed in the models tab\n" +
                "Next planned release date: January","Hi there", JOptionPane.INFORMATION_MESSAGE);

        //Issue a warning if Java is not found in required version
        if (Utility.JAVA_VERSION < 1.7) {
            JOptionPane.showMessageDialog(null, "Your Java version(" + System.getProperty("java.version") + ") is too low.\nJava 1.7 is required for this application to work properly!\nSome features might not work or crash.", "Warning", JOptionPane.WARNING_MESSAGE);
        }

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
        closeLevelItem = new JMenuItem("Close level");
        importMenuItem = new JMenuItem("Import");
        exportMenuItem = new JMenuItem("Export");
        exitMenuItem = new JMenuItem("Exit");


        //FIXME DEVOPTIONS
        devMenu = new JMenu("DEVOPTIONS (everything except this doesn't work yet)");
        loadSGI = new JMenuItem("Load .sgi.msb");

        devMenu.add(loadSGI);



        fileMenu.add(newLevelMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(closeLevelItem);
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

        //FIXME DEVOPTIONS
        menuBar.add(devMenu);
        loadSGI.addActionListener(new ReadSGI_Listener());

        this.setJMenuBar(menuBar);

        /**Get default font and make a bold/italic copy of it*/
        Font defaultFont = UIManager.getDefaults().getFont("Label.font");
        Font boldFont = defaultFont.deriveFont(Font.BOLD);
        Font italicFont = defaultFont.deriveFont(Font.ITALIC);

        /**
         * Mainpane
         */
        contentPane = new JTabbedPane();

        //FIXME currently a GL3 context is requested, find a "softer" way to get the desired context
        /*
        GLProfile glprofile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        final GLCanvas glcanvas = new GLCanvas( glcapabilities );
        glcanvas.setSize(300,300);
        glcanvas.addGLEventListener( new GLEventListener() {

            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                OneTriangle.setup(glautodrawable.getGL().getGL3(), width, height);
            }

            public void init( GLAutoDrawable glautodrawable ) {
            }


            public void dispose( GLAutoDrawable glautodrawable ) {
            }

            public void display( GLAutoDrawable glautodrawable ) {
                OneTriangle.render( glautodrawable.getGL().getGL3(), glautodrawable.getWidth(), glautodrawable.getHeight() );
            }
        });
        */

        CentralDataObject.scenePanel = new JPanel();

        contentPane.add(SCENE_PANEL, CentralDataObject.scenePanel);

        //TODO test data, remove it, dds panel is now global!
        DDS_Panel ddsPanel = CentralDataObject.imageView;

        InfoRectangle[] models = new InfoRectangle[2];
        models[0] = new InfoRectangle(new PixelCoordinate(5,5), new PixelCoordinate(50,50), "Test1");
        models[1] = new InfoRectangle(new PixelCoordinate(10,60), new PixelCoordinate(100,100), "Test2 model bla bla bla");
        ddsPanel.setModels(models);

        Animation[] animations = new Animation[1];
        InfoRectangle[] animation1 = new InfoRectangle[2];
        //Name doesn't matter so it's empty
        animation1[0] = new InfoRectangle(new PixelCoordinate(20,110), new PixelCoordinate(50,140));
        animation1[1] = new InfoRectangle(new PixelCoordinate(60,110), new PixelCoordinate(90,140));
        animations[0] = new Animation("test", animation1);
        ddsPanel.setAnimations(animations);


        CentralDataObject.modelPanel = new JScrollPane(ddsPanel);

        final int SCROLL_SPEED = 8; // TODO make this an option

        CentralDataObject.modelPanel.getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);
        CentralDataObject.modelPanel.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);

        CentralDataObject.animationPanel = new JScrollPane(new AnimationPanel());

        contentPane.setMinimumSize(new Dimension(200,200));

        /**
         * Layout
         */
        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new SelectorPanel(), contentPane);
        splitPane.setOneTouchExpandable(true);


        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);
        this.add(new RightJPane(), BorderLayout.EAST);

        CentralDataObject.modeList.addItemListener(new ModeListItemListenera());

        this.setSize(getPreferredSize());


        //*****Misc stuff*****
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //FIXME is this the problem of the awt crash with file/directory select dialogs


        this.setMinimumSize(new Dimension(400,100));

        this.pack();
        this.setVisible(true);

        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);


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

    public Dimension getPreferredSize(){
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        //When in doubt :-)
        double GOLDEN_RATIO = (1.0 + java.lang.Math.sqrt(5))/2.0;

        int suggestedWidth = (int) (width / GOLDEN_RATIO);
        int suggestedHeight = (int) (height / GOLDEN_RATIO);

        return new Dimension(suggestedWidth, suggestedHeight);
    }

    private class ModeListItemListenera implements ItemListener {
        public void itemStateChanged(ItemEvent itemEvent) {
            if(itemEvent.getStateChange() == ItemEvent.SELECTED){
                String item = (String) itemEvent.getItem();

                // TODO I don't know any good looking switch statement that can take care of this situation
                //We are gonna use many sweet ifs

                //TODO fix model / image panel names, choose one and stick with it

                if(item.equals(ModeChanger.Modes.SCENE.name)){
                    contentPane.removeAll();
                    contentPane.add(SCENE_PANEL, CentralDataObject.scenePanel);
                }
                if(item.equals(ModeChanger.Modes.MODEL.name)){
                    contentPane.removeAll();
                    contentPane.add(SCENE_PANEL, CentralDataObject.scenePanel);
                    contentPane.add(IMAGE_PANEL, CentralDataObject.modelPanel);

                }
                if(item.equals(ModeChanger.Modes.ANIMATION.name)){
                    contentPane.removeAll();
                    contentPane.add(ANIMATION_PANEL, CentralDataObject.animationPanel);

                }
                if(item.equals(ModeChanger.Modes.SHAPE.name)){
                    contentPane.removeAll();
                    //TODO temporary
                    contentPane.add(SCENE_PANEL, CentralDataObject.scenePanel);
                }
            }
        }
    }

    private class ExitApplicationListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == exitMenuItem){
                MainWindow.this.dispose();
            }
        }
    }


    private class ReadSGI_Listener implements ActionListener{
        private String lastPath = ".";

        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == loadSGI){
                JFileChooser fileChooser = new JFileChooser(lastPath);
                fileChooser.setFileFilter(new SGI_FileFilter());
                fileChooser.setAcceptAllFileFilterUsed(false);

                fileChooser.showOpenDialog(MainWindow.this);

                File selectedFile = fileChooser.getSelectedFile();
                if(selectedFile != null && selectedFile.exists()){
                    lastPath = selectedFile.getParent();
                }


                //Cancel if nothing was selected
                if(selectedFile == null){ JOptionPane.showMessageDialog(MainWindow.this,"Nothing selected"); return; }

                //Something was selected
                //Let's read it
                /*
                try{
                    DataStreamIn dsi = new DataStreamIn(selectedFile.getAbsolutePath());

                    String fileFormatRevision = Utility.readLongPascalString(dsi.s);
                    long nOfElements = dsi.s.readLong();

                    for(int i = 0;i < nOfElements;i++){
                        System.out.println("Element name: " + i + " " + Utility.readLongPascalString(dsi.s));
                        System.out.println("Shape name: " + i + " " + Utility.readLongPascalString(dsi.s));
                        dsi.s.read(new byte[66]); //Scrap unknown bytes

                        long nOfAnimations = dsi.s.readLong();
                        System.out.println("nOfElements: " + i + " " + nOfAnimations);
                        for(int j = 0;j < nOfAnimations;j++){
                            System.out.println("animationName: " + i + " " + j + " " + Utility.readLongPascalString(dsi.s));
                            System.out.println("animationFileName: " + i + " " + j + " " + Utility.readLongPascalString(dsi.s));

                        }
                    }

                    dsi.close();

                }catch(IOException ioe){
                    System.out.println("IOEXCEPTION");
                }
                */
                //Smarter reading!

                try{
                    DataStreamIn dsi = new DataStreamIn(selectedFile.getAbsolutePath());
                    SGI_File sgi = new SGI_File(dsi);

                    dsi.close();

                    CentralDataObject.sceneRoot.removeAllChildren();
                    sgi.addToNode(CentralDataObject.sceneRoot);

                    DefaultTreeModel treeModel = (DefaultTreeModel) CentralDataObject.sceneTree.getModel();
                    treeModel.reload();



                    SGI_Element[] models = sgi.elements;

                    String sgmFileName = models[0].modelFileName + ".sgm.msb";



                    String sgmPath = selectedFile.getParent() + File.separator + sgmFileName;
                    try{
                        // FIXME try-catch resource?
                        DataStreamIn sgmStream =  new DataStreamIn(sgmPath);

                        String fileVersion = Utility.readLongPascalString(sgmStream.s);

                        String textureFileName = Utility.readLongPascalString(sgmStream.s);

                        String texturePath = selectedFile.getParentFile().getParent() + File.separator + "textures" + File.separator +  textureFileName + ".dds";
                        sgmStream.close();

                        System.out.println(texturePath);

                        CentralDataObject.imageView.changeImage(texturePath);
                    }catch(IOException ioe){
                        System.out.println("IOEXCEPTION");
                    }


                }catch(IOException ioe){

                    JOptionPane.showMessageDialog(MainWindow.this, "Something went wrong while reading a SGI file");
                }

            }
        }

        private class SGI_FileFilter extends FileFilter{
            public boolean accept(File f) {
                if(f.isDirectory() || (f.getName().endsWith(".sgi.msb") && f.isFile())){
                    return true;
                }else{
                    return false;
                }
            }
            public String getDescription() {
                return "*.sgi.msb";
            }
        }
    }
}
