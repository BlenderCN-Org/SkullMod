package skullMod.lvlEdit.dataStructures.SGA;


import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UV_Track {
    public static final String UV_TRACK_IDENTIFIER = "uv_track";
    public float[] unknownBlock1; //length = nOfEntriesBlock1 * 2 (floats)

    public float[] unknownBlock2; //length = nOfEntriesBlock2 * 1 (floats?)

    public float[] uvOffsets; //UVs for animation (length = nOfOffsets (or frames) * 2 (floats, u and v)

    public UV_Track(DataInputStream dis) throws IOException{
        String identifier = Utility.readLongPascalString(dis);
        if(identifier.equals(UV_TRACK_IDENTIFIER)){
            long nOfEntriesBlock1 = dis.readLong();
            unknownBlock1 = Utility.readFloatArray(dis, new float[2 * (int) nOfEntriesBlock1]);

            long nOfEntriesBlock2 = dis.readLong();
            unknownBlock2 = Utility.readFloatArray(dis, new float[(int)nOfEntriesBlock2]);

            long nOfOffsets = dis.readLong();
            uvOffsets = Utility.readFloatArray(dis, new float[(int) nOfOffsets * 2]);
        }else{
            throw new IllegalArgumentException("Given uv_track has an invalid identifier");
        }
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, UV_TRACK_IDENTIFIER);

        dos.writeLong(unknownBlock1.length);
        for(float f : unknownBlock1){
            dos.writeFloat(f);
        }
        dos.writeLong(unknownBlock2.length);
        for(float f : unknownBlock2){
            dos.writeFloat(f);
        }
        dos.writeLong(uvOffsets.length);
        for(float f : uvOffsets){
            dos.writeFloat(f);
        }
    }
}
