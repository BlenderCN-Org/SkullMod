package skullMod.launcher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Small launcher */
public class Launcher extends JFrame{
    public final static String APPLICATION = "SkullMod";
    public final static String VERSION = "0.1";

    /** Applcations that can be laucnhed */
    private enum Applications{
        GFS_EDIT, LVL_EDIT, SPR_CONV
    }

    /** Buttons */
    private final JButton gfsEditButton, lvlEditButton, sprConvButton;

    /** Main method */
    public static void main(String[] args){
        new Launcher();
    }

    /** Create a launcher */
    public Launcher(){
        super(APPLICATION  + " Launcher " + VERSION);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        gfsEditButton = new JButton("gfsEdit");
        lvlEditButton = new JButton("lvlEdit");
        sprConvButton = new JButton("sprConv");

        ButtonListener listener = new ButtonListener();

        gfsEditButton.addActionListener(listener);
        lvlEditButton.addActionListener(listener);
        sprConvButton.addActionListener(listener);

        this.add(gfsEditButton);
        this.add(lvlEditButton);
        this.add(sprConvButton);

        this.pack();
        this.setVisible(true);
    }

    /** Execute application and dispose of Launcher */
    private class ButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == gfsEditButton){
                SwingUtilities.invokeLater(new StartApplicationThread(Applications.GFS_EDIT));
            }

            if(e.getSource() == lvlEditButton){
                SwingUtilities.invokeLater(new StartApplicationThread(Applications.LVL_EDIT));
            }

            if(e.getSource() == sprConvButton){
                SwingUtilities.invokeLater(new StartApplicationThread(Applications.SPR_CONV));
            }

            Launcher.this.dispose();
        }
    }

    private class StartApplicationThread extends Thread{
        private final Applications selectedApp;

        public StartApplicationThread(Applications a){
            selectedApp = a;
        }

        public void run(){
            switch(selectedApp){
                case GFS_EDIT:
                    skullMod.gfsEdit.Application.main(new String[0]);
                    break;
                case LVL_EDIT:
                    skullMod.lvlEdit.Application.main(new String[0]);
                    break;
                case SPR_CONV:
                    skullMod.sprConv.Application.main(new String[0]);
                    break;
            }
        }
    }
}
