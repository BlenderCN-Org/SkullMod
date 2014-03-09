package skullMod.lvlEdit.dataStructures.SGA;


import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UnknownElement {
    public String name;
    public float[] unknownArray1;
    public float[] unknownArray2;
    public float[] unknownArray3;
    public UnknownElement(DataInputStream dis) throws IOException{
        name = Utility.readLongPascalString(dis);

        long length1 = dis.readLong();
        unknownArray1 = new float[(int) length1 * 3];
        for(int i=0;i < length1;i++){
            unknownArray1[i*3] = dis.readFloat();
            unknownArray1[i*3 + 1] = dis.readFloat();
            unknownArray1[i*3 + 2] = dis.readFloat();
        }

        long length2 = dis.readLong();
        unknownArray2 = new float[(int) length2 * 4];
        for(int i=0;i < length2;i++){
            unknownArray2[i*4] = dis.readFloat();
            unknownArray2[i*4 + 1] = dis.readFloat();
            unknownArray2[i*4 + 2] = dis.readFloat();
            unknownArray2[i*4 + 3] = dis.readFloat();
        }

        long length3 = dis.readLong();
        unknownArray3 = new float[(int) length3 * 3];
        for(int i=0;i < length3;i++){
            unknownArray3[i*3] = dis.readFloat();
            unknownArray3[i*3 + 1] = dis.readFloat();
            unknownArray3[i*3 + 2] = dis.readFloat();
        }
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, name);

        dos.writeLong(unknownArray1.length);
        for(float f : unknownArray1){
            dos.writeFloat(f);
        }
        dos.writeLong(unknownArray2.length);
        for(float f : unknownArray2){
            dos.writeFloat(f);
        }
        dos.writeLong(unknownArray3.length);
        for(float f : unknownArray3){
            dos.writeFloat(f);
        }
    }
}
