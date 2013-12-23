package skullMod.lvlEdit.gui.rightPane;


import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class RightJPane extends JPanel{

    public RightJPane(){
        this.setLayout(new BorderLayout());
        this.add(new JLabel("Metadata"), BorderLayout.NORTH);

        this.add(new RightCenterPane(), BorderLayout.CENTER);
    }


    public Dimension getPreferredSize(){
        Dimension normalPreferredSize = super.getPreferredSize();
        Dimension minimumSize = this.getMinimumSize();

        return new Dimension((int) Math.max(normalPreferredSize.getWidth(), minimumSize.getWidth()), (int) Math.max(normalPreferredSize.getHeight(), minimumSize.getHeight()));
    }
}
