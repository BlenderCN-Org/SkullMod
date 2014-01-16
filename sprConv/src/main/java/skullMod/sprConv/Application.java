package skullMod.sprConv;

import skullMod.sprConv.gui.MainWindow;


/**
 * sprConv
 *
 * Written by 0xFAIL
 *
 * Requires Java 7 (for DataFlavor consistency)
 */
public class Application {
    public static void main(String[] args){
        //Reduce flickering when resizing on Windows 7 with aero
        // http://stackoverflow.com/questions/3979800/disable-background-drawing-in-jframe-in-order-to-properly-display-aero-dwm-eff
        System.setProperty("sun.java2d.noddraw", Boolean.TRUE.toString());
        System.setProperty("sun.java2d.opengl=true", Boolean.TRUE.toString());

        new MainWindow();
    }
}
