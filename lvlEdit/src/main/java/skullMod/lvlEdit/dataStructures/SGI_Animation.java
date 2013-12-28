package skullMod.lvlEdit.dataStructures;

import skullMod.lvlEdit.utility.Utility;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.Serializable;

public class SGI_Animation implements Serializable {
    public String animationName;
    public String animationFileName;

    public SGI_Animation(DataStreamIn sgi_stream) throws IOException {
        animationName = Utility.readLongPascalString(sgi_stream.s);
        animationFileName = Utility.readLongPascalString(sgi_stream.s);
    }

    public void addToNode(DefaultMutableTreeNode currentNode) {
        DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode("Animation name: " + animationName);

        currentNode.add(thisNode);

        thisNode.add(new DefaultMutableTreeNode("File Name: " + animationFileName + ".sga.msb"));
    }
}
