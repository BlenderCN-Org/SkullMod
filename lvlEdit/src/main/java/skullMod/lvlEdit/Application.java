package skullMod.lvlEdit;

import skullMod.lvlEdit.gui.MainWindow;

import javax.swing.*;

/**
 * lvlEdit
 *
 * Written by 0xFAIL
 *
 * Requires Java 7 (for DataFlavor consistency, diamond generics, String switch)
 */
public class Application {
    public static void main(String[] args){
        //Reduce flickering when resizing on Windows 7 with aero
        //Won't get better than this AFAIK
        // http://stackoverflow.com/questions/3979800/disable-background-drawing-in-jframe-in-order-to-properly-display-aero-dwm-eff
        System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
        System.setProperty("sun.java2d.opengl=true", Boolean.TRUE.toString());
        //try{ TODO in production code uncomment this to get better exceptions from users
            new MainWindow();
        //}catch(Exception e){
            //JOptionPane.showMessageDialog(null,"Error while starting application\n" + e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        //}
    }
}
