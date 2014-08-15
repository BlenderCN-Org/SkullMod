package skullMod.lvlEdit.dataStructures.inputDialogs;

import skullMod.lvlEdit.utility.SwingUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ComplexInputDialog<T> extends JDialog{
    private final JButton okButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");
    private JPanel content;
    private JFrame parent;

    private boolean isCanceled = false;

    protected final T oldValue;
    public ComplexInputDialog(JFrame parent, String title, String text, T oldValue){
        super(parent,title, true);

        this.oldValue = oldValue;
        this.parent = parent;

        //Panel, contains the entire content of the window
        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        content.add(new JLabel(text));

        //Button panel
        okButton.addActionListener(new ComplexDialogListener(false));
        cancelButton.addActionListener(new ComplexDialogListener(true));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        SwingUtility.setAlignmentTopLeft(buttonPanel);

        //Panel for child data, no layout is set
        JPanel middlePane = new JPanel();
        middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.Y_AXIS));
        setupGUI(middlePane);
        SwingUtility.topLeftAlignAllComponents(middlePane);


        content.add(middlePane, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);
    }
    public boolean isCanceled(){ return isCanceled; }
    public JButton getOKButton(){ return okButton; }   //For setting visibility

    public void display(){
        this.getContentPane().add(content);
        this.setResizable(false);
        this.pack();

        setLocationRelativeTo(parent);
        this.setVisible(true);
    }
    public abstract void setupGUI(JPanel middlePane);


    private class ComplexDialogListener implements ActionListener    {
        private final boolean cancel;
        public ComplexDialogListener(boolean cancel){
            this.cancel = cancel;
        }
        public void actionPerformed(ActionEvent e) {
           ComplexInputDialog.this.setVisible(false);
           if(cancel){ ComplexInputDialog.this.isCanceled = true; }
        }
    }


}
