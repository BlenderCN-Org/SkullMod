package skullMod.lvlEdit.dataStructures.SGA;

import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

public class SGA_File implements Serializable{
    //TODO too simple, parse complex files like candles

    public String fileFormatRevision;
    public byte[] unknown; //20 bytes
    public float animationLength;
    public String animationType;

    public long nOfEntriesBlock1;
    public float[] unknownBlock1; //length = nOfEntriesBlock1 * 2 (floats)

    public long nOfEntriesBlock2;
    public float[] unknownBlock2; //length = nOfEntriesBlock2 * 1 (floats?)

    public long nOfOffsets; //UV
    public float[] uvOffsets; //UVs for animation (length = nOfOffsets (or frames) * 2 (floats, u and v)

    public SGA_File(DataInputStream dis) throws IOException {
        //TODO verify

        fileFormatRevision = Utility.readLongPascalString(dis);
        unknown = Utility.readByteArray(dis, new byte[20]);
        animationLength = dis.readFloat();
        animationType = Utility.readLongPascalString(dis);

        nOfEntriesBlock1 = dis.readLong();
        unknownBlock1 = Utility.readFloatArray(dis, new float[2* (int)nOfEntriesBlock1]);

        nOfEntriesBlock2 = dis.readLong();
        unknownBlock2 = Utility.readFloatArray(dis, new float[(int)nOfEntriesBlock2]);

        nOfOffsets = dis.readLong();
        uvOffsets = Utility.readFloatArray(dis, new float[(int) nOfOffsets * 2]);
    }
}
