package skullMod.lvlEdit.dataStructures.SGM;

import java.io.DataInputStream;
import java.io.IOException;

public class Normals {
    public float normalX;
    public float normalY;
    public float normalZ;

    public Normals(DataInputStream dis) throws IOException {
        normalX = dis.readFloat();
        normalY = dis.readFloat();
        normalZ = dis.readFloat();
    }
}
