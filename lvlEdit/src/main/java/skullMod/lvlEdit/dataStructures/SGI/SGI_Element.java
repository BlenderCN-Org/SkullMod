package skullMod.lvlEdit.dataStructures.SGI;

import skullMod.lvlEdit.dataStructures.DataStreamIn;
import skullMod.lvlEdit.dataStructures.DataStreamOut;
import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.utility.Utility;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.io.Serializable;

public class SGI_Element implements Serializable {

    public String elementName;
    public String modelFileName;

    public Mat4 transformationMatrix;
    public byte[] unknown; //2 bytes

    public SGI_Animation[] animations;

    public SGI_Element(DataStreamIn sgi_stream) throws IOException {
        elementName = Utility.readLongPascalString(sgi_stream.s);
        modelFileName = Utility.readLongPascalString(sgi_stream.s);

        transformationMatrix = new Mat4(sgi_stream.s);
        unknown = Utility.readByteArray(sgi_stream.s, new byte[2]); //FIXME figure them out

        int nOfAnimations = (int) sgi_stream.s.readLong();

        animations = new SGI_Animation[nOfAnimations];

        for(int i = 0;i < nOfAnimations;i++){
            animations[i] = new SGI_Animation(sgi_stream);
        }
    }

    public void addToNode(DefaultMutableTreeNode currentNode) {
        DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(elementName);

        currentNode.add(thisNode);

        thisNode.add(new DefaultMutableTreeNode("Model file: " + modelFileName + ".sgm.msb"));
        thisNode.add(new DefaultMutableTreeNode("Unknown (66 bytes)"));

        for(SGI_Animation animation : animations){
            animation.addToNode(thisNode);
        }

    }

    public void writeToStream(DataStreamOut dso) throws IOException{
        Utility.writeLongPascalString(dso.s, elementName);
        Utility.writeLongPascalString(dso.s, modelFileName);
        transformationMatrix.writeToStream(dso.s);
        dso.writeBytes(unknown);

        //FIXME change unknown once known
        //FIXME missing rest of write implementation


    }
}
