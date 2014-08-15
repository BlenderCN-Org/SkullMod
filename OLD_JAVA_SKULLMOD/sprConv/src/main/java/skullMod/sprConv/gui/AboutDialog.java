package skullMod.sprConv.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialog extends JDialog implements ActionListener{
    private final JPanel panel;

    private final JButton button;

    public AboutDialog(JFrame parent, boolean modal){
        super(parent, modal);
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Test"));

        button = new JButton("bye bye");
        button.addActionListener(this);
        panel.add(button);

        this.add(panel);

        pack();
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == button){
            this.setVisible(false);
            dispose();
        }
    }
}
