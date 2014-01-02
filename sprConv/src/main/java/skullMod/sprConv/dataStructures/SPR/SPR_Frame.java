package skullMod.sprConv.dataStructures.SPR;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

public class SPR_Frame implements Serializable{
    //TODO uint32 make that class
    int blockOffset;
    int nOfBlocks;
    int unknown1;
    float unknown2;
    float unknown3;

    public SPR_Frame(DataInputStream dis) throws IOException {
        blockOffset = dis.readInt();
        nOfBlocks = dis.readInt();
        unknown1 = dis.readInt();
        unknown2 = dis.readFloat();
        unknown3 = dis.readFloat();
    }
}
