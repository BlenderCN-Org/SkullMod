package skullMod.sprConv.dataStructures.SPR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

//This class assumes the standard layout found in all spr files with 4 bytes per entry
public class SPR_Entry implements Serializable {
    //TODO add unsigned byte as class
    public byte tile_x; //Destination x (in the current Frame)
    public byte tile_y; //Destination y (in the current Frame)
    public byte tile_u; //Source x (in dds file)
    public byte tile_v; //Source y (in dds file)

    public SPR_Entry(DataInputStream dis) throws IOException {
        tile_x = dis.readByte();
        tile_y = dis.readByte();
        tile_u = dis.readByte();
        tile_v = dis.readByte();
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        byte[] tileData = new byte[4];
        tileData[0] = tile_x;
        tileData[1] = tile_y;
        tileData[2] = tile_u;
        tileData[3] = tile_v;
        dos.write(tileData);
    }
}
