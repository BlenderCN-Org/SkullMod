package skullMod.lvlEdit.dataStructures.inputDialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SimpleInputDialog extends JDialog{
    protected final JFormattedTextField textField;
    protected final JButton okButton;

    String result;

    public SimpleInputDialog(JFrame parent, String title, String text, String previousValue){
        super(parent,title, true);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);




        content.add(buttonPanel, BorderLayout.SOUTH);



        okButton.addActionListener(new HideListener());



        this.textField = new JFormattedTextField(previousValue);
        this.textField.addKeyListener(new EnterKeyAdapter());

        content.add(new JLabel(text), BorderLayout.NORTH);
        content.add(textField, BorderLayout.CENTER);

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
                SimpleInputDialog.this.okButton.doClick();
            }
        }
    }
}
