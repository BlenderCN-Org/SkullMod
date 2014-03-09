package skullMod.lvlEdit.dataStructures.SGS;

import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SGS_Joint {
    public String name;
    public byte[] unknown; //4 bytes of unknown stuff
    public Mat4 matrix;

    public SGS_Joint(DataInputStream dis) throws IOException {
        name = Utility.readLongPascalString(dis);
        unknown = new byte[4];
        matrix = Mat4.readFromStream(dis);
    }


    public SGS_Joint(){
        name = "DEFAULT BONE";
        unknown = new byte[4];
        unknown[0] = 0;
        unknown[1] = 0;
        unknown[2] = 0;
        unknown[3] = 0;

        matrix = new Mat4(new float[]{1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1});
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, name);
        dos.write(unknown);
        for(Float f : matrix.get()){
            dos.writeFloat(f);
        }
    }
}
