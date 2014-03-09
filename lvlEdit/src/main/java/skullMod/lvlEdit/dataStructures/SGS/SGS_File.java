package skullMod.lvlEdit.dataStructures.SGS;


import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

public class SGS_File {
    public final LinkedList<SGS_Joint> bones;

    public static final String SGS_KNOWN_VERSION = "2.0";

    public SGS_File(DataInputStream dis) throws IOException {
        String version = Utility.readLongPascalString(dis);
        if(version.equals(SGS_KNOWN_VERSION)){
            long nOfBones = dis.readLong();
            long unknown = dis.readLong();

            bones = new LinkedList<>();

            for(int i=0;i < nOfBones;i++){
                bones.add(new SGS_Joint(dis));
            }
        }else{
            throw new IllegalArgumentException("Incorrect version number in sgs file");
        }
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, SGS_KNOWN_VERSION);
        dos.writeLong(bones.size());
        dos.writeLong(0); //FIXME currently unknown
        for(SGS_Joint bone : bones){
            bone.writeToStream(dos);
        }
    }
}
