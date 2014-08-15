package skullMod.lvlEdit.dataStructures.SGS;


import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class SGS_File {
    public static final String SGS_KNOWN_VERSION = "2.0";

    public ArrayList<Bone> bones;

    public SGS_File(DataInputStream dis) throws IOException {

        String version = Utility.readLongPascalString(dis);
        if(version.equals(SGS_KNOWN_VERSION)){
            long nOfBones = dis.readLong();

            bones = new ArrayList<>((int) nOfBones);

            for(int i=0;i < nOfBones;i++){
                bones.add(new Bone(dis));
            }
        }else{
            throw new IllegalArgumentException("Incorrect version number in sgs file");
        }
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, SGS_KNOWN_VERSION);
        dos.writeLong(bones.size());
        for(Bone bone : bones){
            bone.writeToStream(dos);
        }
    }
}
