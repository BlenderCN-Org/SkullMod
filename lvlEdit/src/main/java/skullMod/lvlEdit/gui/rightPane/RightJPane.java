package skullMod.lvlEdit.gui.rightPane;


import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class RightJPane extends JPanel{
    private final HashMapJTable testTree;

    public RightJPane(){
        this.setLayout(new BorderLayout());
        this.add(new JLabel("Metadata"), BorderLayout.NORTH);


        HashMap<String, Object> test = new HashMap<>();

        test.put("Name", "test.msb.sgm");
        test.put("Image Width","100");
        test.put("Image Height", "100");


        testTree = new HashMapJTable(test);

        this.add(new JScrollPane(testTree), BorderLayout.CENTER);
    }

    public Dimension getMinimumSize(){ return testTree.getMinimumSize(); }


    public Dimension getPreferredSize(){
        Dimension normalPreferredSize = super.getPreferredSize();
        Dimension minimumSize = testTree.getMinimumSize();

        return new Dimension((int) Math.max(normalPreferredSize.getWidth(), minimumSize.getWidth()), (int) Math.max(normalPreferredSize.getHeight(), minimumSize.getHeight()));
    }
}
