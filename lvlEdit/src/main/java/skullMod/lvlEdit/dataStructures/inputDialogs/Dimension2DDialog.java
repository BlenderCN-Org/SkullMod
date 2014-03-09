package skullMod.lvlEdit.dataStructures.inputDialogs;

import javax.swing.*;

public class Dimension2DDialog extends ComplexInputDialog{
    private final JLabel xLabel = new JLabel("X");
    private final JLabel yLabel = new JLabel("Y");
    public Dimension2DDialog(JFrame parent, String title, String text){  //TODO For more infos, add label
        super(parent, title, "Input dimensions");
    }
    public void setupGUI(JPanel contentPane) {
        contentPane.add(new JTextField());
        contentPane.add(new JTextField());
    }
}
