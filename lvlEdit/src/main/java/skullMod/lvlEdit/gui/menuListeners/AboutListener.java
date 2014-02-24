package skullMod.lvlEdit.gui.menuListeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutListener implements ActionListener {
    private final JFrame parent;

    public AboutListener(JFrame parent) {
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {
        new AboutDialog();
    }

    private class AboutDialog extends JDialog{
        public AboutDialog(){
            super(parent,true);

            JPanel content = new JPanel();
            content.setLayout(new BorderLayout());
            content.add(new JLabel("HERE WILL BE AN ABOUT DIALOG BOX"), BorderLayout.CENTER);

            this.getContentPane().add(content);

            this.pack();
            setLocationRelativeTo(parent);
            this.setVisible(true);
        }
    }
}
