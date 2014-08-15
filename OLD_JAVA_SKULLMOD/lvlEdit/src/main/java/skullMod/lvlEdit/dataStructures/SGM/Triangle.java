package skullMod.lvlEdit.dataStructures.SGM;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

public class Triangle implements Serializable{
    public short vertexIndex1;
    public short vertexIndex2;
    public short vertexIndex3;

    public Triangle(DataInputStream dis) throws IOException {
        vertexIndex1 = dis.readShort();
        vertexIndex2 = dis.readShort();
        vertexIndex3 = dis.readShort();
    }
}
