package skullMod.lvlEdit.dataStructures.SGI;

import skullMod.lvlEdit.dataStructures.DataStreamIn;
import skullMod.lvlEdit.dataStructures.DataStreamOut;
import skullMod.lvlEdit.utility.Utility;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.Serializable;

public class SGI_File implements Serializable{
    public static final String DEFAULT_SGI_FILENAME = "background.sgi.msb";

    public static final String KNOWN_FILE_FORMAT_REVISION = "2.0";
    public String fileFormatRevision;
    public SGI_Element[] elements;

    //TODO do for all classes?
    public SGI_File(DataStreamIn sgi_stream) throws IOException {
        fileFormatRevision = Utility.readLongPascalString(sgi_stream.s);

        if(!fileFormatRevision.equals(KNOWN_FILE_FORMAT_REVISION)){ throw new IllegalArgumentException("Bad file"); }

        int nOfElements = (int) sgi_stream.s.readLong();

        elements = new SGI_Element[nOfElements];
        for(int i = 0;i < nOfElements;i++){
            elements[i] = new SGI_Element(sgi_stream);
        }
    }

    public void writeToStream(DataStreamOut dso) throws IOException{
        Utility.writeLongPascalString(dso.s, fileFormatRevision);
        dso.s.writeLong(elements.length);
        for(SGI_Element element : elements){
            element.writeToStream(dso);
        }
    }

    //Doesn't have to be a root node, but everything else seems pointless
    public void addToNode(DefaultMutableTreeNode root){
        DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode("Elements");

        root.add(currentNode);

        currentNode.add(new DefaultMutableTreeNode("File format revision: " + fileFormatRevision));

        for(SGI_Element currentElement : elements){
            currentElement.addToNode(currentNode);
        }
    }

    public String toString(){
        return "lvl file";
    }
}
