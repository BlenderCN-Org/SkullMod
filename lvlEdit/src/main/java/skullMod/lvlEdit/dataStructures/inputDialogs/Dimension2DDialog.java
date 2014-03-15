package skullMod.lvlEdit.dataStructures.inputDialogs;

import skullMod.lvlEdit.utility.Dimension2D;

import javax.swing.*;

public class Dimension2DDialog extends ComplexInputDialog{
    private final JLabel xLabel = new JLabel("X");
    private final JLabel yLabel = new JLabel("Y");
    public Dimension2DDialog(JFrame parent, String title, String text, Dimension2D oldValue){  //TODO For more infos, add label
        super(parent, title, "Input dimensions", oldValue);
    }
    public void setupGUI(JPanel contentPane) {
        contentPane.add(new JTextField());
        contentPane.add(new JTextField());
    }
}
