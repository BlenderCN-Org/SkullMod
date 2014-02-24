package skullMod.lvlEdit.gui.menuListeners;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpListener implements ActionListener{
    private final JFrame parent;

    public HelpListener(JFrame parent) {
        this.parent = parent;
    }

    public void actionPerformed(ActionEvent e) {

    }

    private class HelpDialog extends JDialog{
        public HelpDialog(){
            super(parent,true);

            JPanel content = new JPanel();
            content.setLayout(new BorderLayout());
            content.add(new JLabel("HERE WILL BE A HELP DIALOG BOX"), BorderLayout.CENTER);

            this.getContentPane().add(content);

            this.pack();
            setLocationRelativeTo(parent);
            this.setVisible(true);
        }
    }
}
