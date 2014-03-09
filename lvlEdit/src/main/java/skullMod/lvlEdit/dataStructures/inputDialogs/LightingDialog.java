package skullMod.lvlEdit.dataStructures.inputDialogs;

import javax.swing.*;

public class LightingDialog extends ComplexInputDialog{
    public LightingDialog(JFrame parent) {
        super(parent, "Lighting", "Edit lighting");
    }

    public void setupGUI(JPanel contentPane) {
        //TODO fix that layout


        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new BoxLayout(positionPanel, BoxLayout.X_AXIS));

        JPanel miscPanel = new JPanel();
        miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.X_AXIS));



        colorPanel.add(new JLabel("Color (RGB): "));
        JTextField redTextField = new JTextField(3);
        JTextField greenTextField = new JTextField(3);
        JTextField blueTextField = new JTextField(3);

        positionPanel.add(new JLabel("Position (XYZ"));
        JTextField xTextField = new JTextField(4);
        JTextField yTextField = new JTextField(4);
        JTextField zTextField = new JTextField(4);

        miscPanel.add(new JLabel("Misc"));
        JTextField lightDistanceTextField = new JTextField(5);
        JCheckBox neverCullCheckBox = new JCheckBox();

        colorPanel.add(redTextField);
        colorPanel.add(greenTextField);
        colorPanel.add(blueTextField);

        positionPanel.add(xTextField);
        positionPanel.add(yTextField);
        positionPanel.add(zTextField);

        miscPanel.add(lightDistanceTextField);
        miscPanel.add(neverCullCheckBox);


        contentPane.add(colorPanel);
        contentPane.add(positionPanel);
        contentPane.add(miscPanel);

        contentPane.add(new JLabel("Input RGB ==> Ambient light"));
        contentPane.add(new JLabel("Input RGB+XYZ ==> Directional light"));
        contentPane.add(new JLabel("Input RGB+XYZ+Light distance+neverCull ==> Point light"));
    }
}
