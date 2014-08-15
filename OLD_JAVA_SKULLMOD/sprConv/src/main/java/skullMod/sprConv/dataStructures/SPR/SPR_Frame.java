package skullMod.sprConv.dataStructures.SPR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SPR_Frame implements Serializable{
    //TODO uint32 make that class
    public int blockOffset;
    public int nOfBlocks;
    public int unknown1;
    public float xImageCenter;
    public float yImageCenter;

    /**
     * The frame number is for displaying the correct value in the GUI / for the toString() method
     * It's NOT part of the file format itself
     */
    public int frameNumber;

    public SPR_Frame(DataInputStream dis, int frameNumber) throws IOException {
        blockOffset = dis.readInt();
        nOfBlocks = dis.readInt();
        unknown1 = dis.readInt();
        xImageCenter = dis.readFloat();
        yImageCenter = dis.readFloat();

        this.frameNumber = frameNumber;
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        dos.writeInt(blockOffset);
        dos.writeInt(nOfBlocks);
        dos.writeInt(unknown1);
        dos.writeFloat(xImageCenter);
        dos.writeFloat(yImageCenter);
    }
    public String toString(){
        return Integer.toString(frameNumber);
    }
}
