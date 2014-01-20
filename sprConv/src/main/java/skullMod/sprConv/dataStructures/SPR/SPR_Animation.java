package skullMod.sprConv.dataStructures.SPR;

import skullMod.sprConv.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SPR_Animation implements Serializable{
    public String animationName;
    public int frameOffset; //Unsigned
    public int nOfFrames; //Unsigned
    public int unknown1;
    public int lastFrame; //Unsigned

    public SPR_Animation(DataInputStream dis) throws IOException {
        animationName = Utility.readLongPascalString(dis);
        frameOffset = dis.readInt();
        nOfFrames = dis.readInt();
        unknown1 = dis.readInt();
        lastFrame = dis.readInt();
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, animationName);
        dos.writeInt(frameOffset);
        dos.writeInt(nOfFrames);
        dos.writeInt(unknown1);
        dos.writeInt(lastFrame);
    }

    public String toString(){
        return animationName;
    }
}
