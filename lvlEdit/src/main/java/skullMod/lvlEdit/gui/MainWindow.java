package skullMod.lvlEdit.gui;

import skullMod.lvlEdit.dataStructures.CentralDataObject;
import skullMod.lvlEdit.dataStructures.DataStreamIn;
import skullMod.lvlEdit.dataStructures.SGM.SGM_File;
import skullMod.lvlEdit.dataStructures.SGM.Triangle;
import skullMod.lvlEdit.dataStructures.SGM.Vertex;
import skullMod.lvlEdit.dataStructures.completeLevel.Level;
import skullMod.lvlEdit.dataStructures.openGL.OpenGL_Frame;
import skullMod.lvlEdit.gui.animationPane.InfoRectangle;
import skullMod.lvlEdit.gui.animationPane.PixelCoordinate;
import skullMod.lvlEdit.gui.menuListeners.*;
import skullMod.lvlEdit.gui.selectorPane.SelectorPanel;
import skullMod.lvlEdit.utility.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public class MainWindow extends JFrame {
    public static final String APPLICATION  = "LVL edit";
    public static final String AUTHOR       = "0xFAIL";
    public static final String VERSION      = "0.5";
    public static final String DATE         = "2014-02-23";
    public static final String GAME         = "Skullgirls (PC)";

    public static final String ANIMATION_PANEL = "Animation";
    public static final String IMAGE_PANEL = "2D Image";
    public static final String SCENE_PANEL = "3D";


    public final JMenuBar menuBar;
    public final JMenu fileMenu, toolsMenu, aboutMenu;
    public final JMenuItem newLevelMenuItem, loadMenuItem, saveMenuItem, saveAsMenuItem, importMenuItem, exportMenuItem, exitMenuItem;
    public final JMenuItem imageToDDSMenuItem;
    public final JMenuItem aboutMenuItem, helpMenuItem;

    public final JTabbedPane contentPane;

    public MainWindow(){
        super(APPLICATION + " " + VERSION); //Set title

        //Issue a warning if Java is not found in required version
        if (Utility.getVersion() < 1.7) {
            JOptionPane.showMessageDialog(null, "Your Java version(" + System.getProperty("java.version") + ") is too low.\nJava 7 is required for this application to work properly!\nSome features might not work or crash.", "Warning", JOptionPane.WARNING_MESSAGE);
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
        /*Menues, it is important to make them heavyweight so they display infront of the opengl canvas*/
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        fileMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('t');
        toolsMenu.getPopupMenu().setLightWeightPopupEnabled(false);
        aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('a');
        aboutMenu.getPopupMenu().setLightWeightPopupEnabled(false);
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

        /* Add listeners */
        newLevelMenuItem.addActionListener(new NewLevelListener());
        loadMenuItem.addActionListener(new LoadLevelListener(this));
        saveMenuItem.addActionListener(new SaveFileListener());
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.dispose();
            }
        });


        aboutMenuItem.addActionListener(new AboutListener(this));
        helpMenuItem.addActionListener(new HelpListener(this));

        this.setJMenuBar(menuBar);


        CentralDataObject.ddsPanel = new DDS_Panel();

        /**
         * Mainpane
         */
        contentPane = new JTabbedPane();

        CentralDataObject.scenePanel = OpenGL_Frame.getNewPanel(this);

        //Make a scrollpane around ddsPanel
        final int SCROLL_SPEED = 8; // TODO make this an option
        JScrollPane ddsScrollPanel = new JScrollPane(CentralDataObject.ddsPanel);
        ddsScrollPanel.getHorizontalScrollBar().setUnitIncrement(SCROLL_SPEED);
        ddsScrollPanel.getVerticalScrollBar().setUnitIncrement(SCROLL_SPEED);


        contentPane.add(SCENE_PANEL, CentralDataObject.scenePanel);
        contentPane.add(IMAGE_PANEL, ddsScrollPanel);
        contentPane.add(ANIMATION_PANEL, CentralDataObject.animationPanel);

        contentPane.setMinimumSize(new Dimension(200, 200));

        /**
         * Layout
         */
        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new SelectorPanel(this), contentPane);
        splitPane.setOneTouchExpandable(true);


        this.setLayout(new BorderLayout());
        this.add(splitPane, BorderLayout.CENTER);

        this.setSize(getPreferredSize());

        //*****Misc stuff*****
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        this.setMinimumSize(new Dimension(400,100));


        this.pack();
        this.setVisible(true);


        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);


        //WIN new Level("C:\\levels\\temp\\levels","class_notes_3d");
        //UNIX new Level("/home/netbook/Working_files/Skullgirls_extracted/levels", "class_notes_3d");
        //CentralDataObject.level.setModel(new DefaultTreeModel(new Level("C:\\levels\\temp\\levels","class_notes_3d")));

        //ddsPanel.changeImage("/home/netbook/Working_files/Skullgirls_extracted/levels/textures/class_notes_3d.dds");
        //CentralDataObject.ddsPanel.changeImage("C:\\levels\\temp\\levels\\textures\\class_notes_3d.dds");
        //ddsPanel.changeImage("C:\\levels\\temp\\levels\\textures\\innsmouth_day_fgnpc_02.dds");
        /*
        int width = CentralDataObject.ddsPanel.getImage().getWidth();
        int height = CentralDataObject.ddsPanel.getImage().getHeight();

        try {
            //DataStreamIn dsi = new DataStreamIn("/home/netbook/Working_files/Skullgirls_extracted/levels/class_notes_3d/class_notes_npcs_01_shape.sgm.msb");
            DataStreamIn dsi = new DataStreamIn("C:\\levels\\temp\\levels\\class_notes_3d\\class_notes_npcs_01_shape.sgm.msb");
            //DataStreamIn dsi = new DataStreamIn("C:\\levels\\temp\\levels\\innsmouth_day_3d\\innsmouth_minneteShape.sgm.msb");

            SGM_File sgm = new SGM_File(dsi.s);

            InfoRectangle[] points = new InfoRectangle[sgm.vertices.length];

            for(int i=0;i < sgm.vertices.length;i++){
                Vertex currentVertex = sgm.vertices[i];

                PixelCoordinate point = new PixelCoordinate( (int)((double)currentVertex.uv.u * (double)width), (int) (height - ((double)currentVertex.uv.v * (double)height)) );


                points[i] = new InfoRectangle(point,point,Integer.toString(i));
            }

            CentralDataObject.ddsPanel.setModels(points);

            DDS_Panel.UV_Triangle[] triangles = new DDS_Panel.UV_Triangle[sgm.triangles.length];
            for(int i = 0;i < sgm.triangles.length;i++){
                Triangle currentTriangle = sgm.triangles[i];

                triangles[i] = new DDS_Panel.UV_Triangle(sgm.vertices[currentTriangle.vertexIndex1].uv,sgm.vertices[currentTriangle.vertexIndex2].uv,sgm.vertices[currentTriangle.vertexIndex3].uv);
            }

            CentralDataObject.ddsPanel.setUV_Triangles(triangles);

            dsi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
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



    private class ExitApplicationListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == exitMenuItem){
                MainWindow.this.dispose();
            }
        }
    }
    /*
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

*/
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
                /*
                try{
                    DataStreamIn dsi = new DataStreamIn(selectedFile.getAbsolutePath());
                    SGI_File sgi = new SGI_File(dsi);

                    dsi.close();


                    SGI_Element[] models = sgi.elements;

                    String sgmFileName = models[0].modelFileName + ".sgm.msb";



                    String sgmPath = selectedFile.getParent() + File.separator + sgmFileName;
                    try{
                        // FIXME try-catch resource? Error handling?
                        DataStreamIn sgmStream =  new DataStreamIn(sgmPath);

                        String fileVersion = Utility.readLongPascalString(sgmStream.s);

                        String textureFileName = Utility.readLongPascalString(sgmStream.s);

                        String texturePath = selectedFile.getParentFile().getParent() + File.separator + "textures" + File.separator +  textureFileName + ".dds";
                        sgmStream.close();

                        System.out.println(texturePath);

                        CentralDataObject.ddsPanel.changeImage(texturePath);
                    }catch(IOException ioe){
                        System.out.println("IOEXCEPTION");
                    }


                }catch(IOException ioe){

                    JOptionPane.showMessageDialog(MainWindow.this, "Something went wrong while reading a SGI file");
                }

            }
        }
        */


}
