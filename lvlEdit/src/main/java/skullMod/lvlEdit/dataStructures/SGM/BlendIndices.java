package skullMod.lvlEdit.dataStructures.SGM;

import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

public class BlendIndices implements Serializable{
    //Definatly wrong, shoudl be 4*1 + 4*1
    float[] blendIndices = new float[3*4];

    public BlendIndices(DataInputStream dis) throws IOException {
        Utility.readFloatArray(dis, blendIndices); //Writes into given array
    }
}
