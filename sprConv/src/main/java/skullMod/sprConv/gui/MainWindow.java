package skullMod.sprConv.gui;

import skullMod.sprConv.utility.Utility;

import javax.swing.*;
import java.awt.*;

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
        JPanel panelRight = new DrawPanel();

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
    }
}
