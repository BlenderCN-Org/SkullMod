package skullMod.gfsEdit;

import skullMod.gfsEdit.gui.MainWindow;

import javax.swing.*;

/**
 * GFS edit
 *
 * Written by 0xFAIL
 *
 * Requires Java 7 (DataFlavor consistency, diamond generics, nimbus l&f)
 */
public class Application {
    public static void main(String[] args){

        //Start properly from event dispatcher thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainWindow();
            }
        });
    }
}
