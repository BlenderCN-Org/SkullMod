package skullMod.lvlEdit.gui.selectorPane;

import javax.swing.*;
import java.awt.*;

public class SelectorPanel extends JPanel {
    public SelectorPanel(){
        this.setLayout(new BorderLayout());

        CenterSelectorPanel centerPanel = new CenterSelectorPanel();

        this.add(centerPanel, BorderLayout.CENTER);
    }
}
