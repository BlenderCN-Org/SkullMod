package skullMod.gfsEdit.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Maximum height always equals preferred height
 *
 * This is a workaround for some components (JComboBox, JRadioButton, JTextField,...)
 * which report an unbounded max height
 *
 * See http://stackoverflow.com/questions/7581846/swing-boxlayout-problem-with-jcombobox-without-using-setxxxsize
 */
public class FixedSizeJPanel extends JPanel {
    public FixedSizeJPanel(){ super(); }
    public FixedSizeJPanel(LayoutManager lm){ super(lm); }
    public Dimension getMaximumSize(){
        Dimension prefDim = getPreferredSize();
        Dimension superDim = super.getMaximumSize();
        return new Dimension(superDim.width,prefDim.height);
    }
}
