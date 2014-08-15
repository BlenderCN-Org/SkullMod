package skullMod.lvlEdit.dataStructures.SGS;


import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Bone {
    public String boneName;
    public int boneID;
    public Mat4 matrix;

    public static final int ROOT_BONE_ID = 0xFFFFFFFF;

    public Bone(String boneName, int boneID, Mat4 matrix){
        this.boneName = boneName;
        this.boneID = boneID;
        this.matrix = matrix;
    }

    public Bone(DataInputStream dis) throws IOException {
        boneName = Utility.readLongPascalString(dis);
        boneID = dis.readInt();
        matrix = Mat4.readFromStream(dis);
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, boneName);
        dos.writeInt(boneID);
        for(float f : matrix.get()){
            dos.writeFloat(f);
        }
    }
}
