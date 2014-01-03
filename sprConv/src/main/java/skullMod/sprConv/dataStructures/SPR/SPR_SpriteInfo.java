package skullMod.sprConv.dataStructures.SPR;

import skullMod.sprConv.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SPR_SpriteInfo implements Serializable{
    String spriteName;
    int unknown1;
    int nOfFrames; //Unsigned
    int unknown2;
    int unknown3;

    public SPR_SpriteInfo(DataInputStream dis) throws IOException {
        spriteName = Utility.readLongPascalString(dis);
        unknown1 = dis.readInt();
        nOfFrames = dis.readInt();
        unknown2 = dis.readInt();
        unknown3 = dis.readInt();
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, spriteName);
        dos.writeInt(unknown1);
        dos.writeInt(nOfFrames);
        dos.writeInt(unknown2);
        dos.writeInt(unknown3);
    }

}
