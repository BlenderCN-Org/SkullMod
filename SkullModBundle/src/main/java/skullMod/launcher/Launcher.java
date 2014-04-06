package skullMod.launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Small launcher */
public class Launcher extends JFrame{
    public final static String APPLICATION = "SkullMod";
    public final static String VERSION = "1.0";
    public final static String DATE = "2014-03-23";

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
        super(APPLICATION  + " Launcher " + VERSION + "-" + DATE);

        /**Set look of the application to mimic the OS GUI*/
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            System.err.println("Setting look and feel failed"); //This should happen silently
        }

        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setLocation(100,100);

        gfsEditButton = new JButton("gfsEdit");
        gfsEditButton.setPreferredSize(new Dimension(100,25));
        lvlEditButton = new JButton("lvlEdit");
        lvlEditButton.setPreferredSize(new Dimension(100,25));
        sprConvButton = new JButton("sprConv");
        sprConvButton.setPreferredSize(new Dimension(100,25));

        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setAlignmentTopLeft(textPanel);

        JPanel gfsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setAlignmentTopLeft(gfsPanel);

        JPanel lvlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setAlignmentTopLeft(lvlPanel);
        JPanel sprPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setAlignmentTopLeft(sprPanel);

        textPanel.add(new JLabel("Tools for the game Skullgirls on PC"));

        gfsPanel.add(gfsEditButton);
        gfsPanel.add(new JLabel("Pack/Unpack .gfs files"));

        lvlPanel.add(lvlEditButton);
        lvlPanel.add(new JLabel("Create/Edit .lvl stages"));

        sprPanel.add(sprConvButton);
        sprPanel.add(new JLabel("Convert sprites (.spr.msb <-> .png)"));


        ButtonListener listener = new ButtonListener();

        gfsEditButton.addActionListener(listener);
        lvlEditButton.addActionListener(listener);
        sprConvButton.addActionListener(listener);

        this.add(textPanel);
        this.add(gfsPanel);
        this.add(lvlPanel);
        this.add(sprPanel);

        this.pack();
        this.setVisible(true);

        this.setResizable(false);
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

    public static void setAlignmentTopLeft(JComponent c) {
        c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        c.setAlignmentY(JComponent.TOP_ALIGNMENT);
    }
}
