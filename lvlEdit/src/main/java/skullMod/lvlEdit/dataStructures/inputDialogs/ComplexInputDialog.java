package skullMod.lvlEdit.dataStructures.inputDialogs;

import javax.swing.*;



public abstract class ComplexInputDialog extends JDialog{
    private final JButton okButton = new JButton("OK");
    private JPanel content;
    private JFrame parent;
    public ComplexInputDialog(JFrame parent, String title, String text){
        super(parent,title, true);

        this.parent = parent;

        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        content.add(new JLabel(text));

        setupGUI(content);
        content.add(okButton);
    }
    public void display(){
        this.getContentPane().add(content);
        this.setResizable(false);
        this.pack();

        setLocationRelativeTo(parent);
        this.setVisible(true);
    }
    public abstract void setupGUI(JPanel contentPane);
    public JButton getOKButton(){ return okButton; }   //For adding listeners
}
