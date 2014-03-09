package skullMod.lvlEdit.dataStructures.inputDialogs;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SimpleInputDialog extends JDialog{
    protected final JTextField textField;
    protected final JButton button;

    String result;

    public SimpleInputDialog(JFrame parent, String title, String text){
        super(parent,title, true);

        this.textField = new JTextField("TEST");
        this.textField.addKeyListener(new EnterKeyAdapter());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        content.add(new JLabel(text));
        content.add(textField);

        button = new JButton("OK");
        button.addActionListener(new HideListener());
        content.add(button);

        this.getContentPane().add(content);
        this.setResizable(false);
        this.pack();

        setLocationRelativeTo(parent);
        this.setVisible(true);
    }

    private class HideListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            result = textField.getText();
            SimpleInputDialog.this.setVisible(false);
        }
    }

    private class EnterKeyAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent ke){
            if (ke.getKeyCode()==KeyEvent.VK_ENTER){
                SimpleInputDialog.this.button.doClick();
            }
        }
    }
}
