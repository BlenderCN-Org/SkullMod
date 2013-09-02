package skullMod.lvlEdit.gui;

import skullMod.lvlEdit.utility.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 */
public class MainWindow extends JFrame {
    public static final String APPLICATION  = "LVL edit";
    public static final String AUTHOR       = "0xFAIL";
    public static final String VERSION      = "0.1";
    public static final String DATE         = "2013-09-02";
    public static final String GAME         = "Skullgirls (PC)";

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
        }
        catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            System.err.println("Setting look and feel failed"); //This should happen silently
        }

        /**Get default font and make a bold/italic copy of it*/
        Font defaultFont = UIManager.getDefaults().getFont("Label.font");
        Font boldFont = defaultFont.deriveFont(Font.BOLD);
        Font italicFont = defaultFont.deriveFont(Font.ITALIC);

        this.add(new JPanel());

        //*****Misc stuff*****
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();
        this.setVisible(true);

        //Issue a warning if Java is not required version
        if (Utility.JAVA_VERSION < 1.7) {
            JOptionPane.showMessageDialog(this, "Your Java version(" + System.getProperty("java.version") + ") is too low.\nJava 1.7 is required for this application to work properly!\nDrag and drop might not work.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
