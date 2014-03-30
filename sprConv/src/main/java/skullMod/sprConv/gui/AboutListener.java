package skullMod.sprConv.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class AboutListener implements ActionListener {
    private final JFrame parent;
    public AboutListener(JFrame parent) {
        this.parent = parent;
    }

    private class AboutDialog extends JDialog{
        public AboutDialog(){
            super(parent,true);

            this.setTitle("About");

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            JLabel aboutLabel1 = new JLabel("<html>Made by " + MainWindow.AUTHOR + "<br><br>Current version: " + MainWindow.VERSION + " " + MainWindow.DATE + "<br>Game: " + MainWindow.GAME + "<html>");
            JLabel aboutLabel2 = new JLabel("<html><br>Newest version at: www.github.com/0xFAIL<html>");
            JLabel aboutLabel3 = new JLabel("<html><br>For the license see LICENSE.txt (BSD 2-Clause License)<br><br><br>Iconlink here<html>");

            //TODO beautify
            InputStream io = Thread.currentThread().getContextClassLoader().getResourceAsStream("excelsior_badge.png");
            BufferedImage myPicture = null;
            try {
                myPicture = ImageIO.read(io);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            picLabel.setBorder(BorderFactory.createEmptyBorder(20,10,20,0));


            picLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            picLabel.addMouseListener(new MouseURLAdapter("http://www.ExcelsiorJET.com/"));

            JLabel creditLine1 = new JLabel("Excelsior and Excelsior JET are trademarks of Excelsior LLC");
            JLabel creditLine2 = new JLabel("in the Russian Federation and other countries.");
            JLabel creditLine3 = new JLabel("The Excelsior JET Badge is a trademark of Excelsior LLC,");
            JLabel creditLine4 = new JLabel("used with permission.");

            //TODO add fonts
            //aboutLabel1.setFont(boldFont);
            //aboutLabel3.setFont(italicFont);

            content.add(aboutLabel1);
            content.add(aboutLabel2);
            content.add(aboutLabel3);

            content.add(picLabel);

            content.add(creditLine1);
            content.add(creditLine2);
            content.add(creditLine3);
            content.add(creditLine4);

            this.getContentPane().add(content);

            this.pack();
            setLocationRelativeTo(parent);
            this.setVisible(true);
        }
    }

    public void actionPerformed(ActionEvent e) {
        new AboutDialog();
    }
}
