package skullMod.lvlEdit.dataStructures.SGM;

import java.io.DataInputStream;
import java.io.IOException;

public class UV {
    public float u;
    public float v;

    public UV(DataInputStream dis) throws IOException {
        u = dis.readFloat();
        v = dis.readFloat();
    }
}
