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
    public float unknown2;
    public float unknown3;

    /**
     * The frame number is for displaying the correct value in the GUI / for the toString() method
     * It's NOT part of the file format itself
     */
    public int frameNumber;

    public SPR_Frame(DataInputStream dis, int frameNumber) throws IOException {
        blockOffset = dis.readInt();
        nOfBlocks = dis.readInt();
        unknown1 = dis.readInt();
        unknown2 = dis.readFloat();
        unknown3 = dis.readFloat();

        this.frameNumber = frameNumber;
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        dos.writeInt(blockOffset);
        dos.writeInt(nOfBlocks);
        dos.writeInt(unknown1);
        dos.writeFloat(unknown2);
        dos.writeFloat(unknown3);
    }
    public String toString(){
        return Integer.toString(frameNumber);
    }
}
