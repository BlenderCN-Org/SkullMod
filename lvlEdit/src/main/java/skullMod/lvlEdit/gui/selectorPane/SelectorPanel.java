package skullMod.lvlEdit.gui.selectorPane;

import javax.swing.*;
import java.awt.*;

public class SelectorPanel extends JPanel {
    public SelectorPanel(JFrame frame){
        this.setLayout(new BorderLayout());

        CenterSelectorPanel centerPanel = new CenterSelectorPanel(frame);

        this.add(centerPanel, BorderLayout.CENTER);
    }
}
