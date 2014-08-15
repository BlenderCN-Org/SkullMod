package skullMod.lvlEdit.dataStructures.SGM;

import java.io.DataInputStream;
import java.io.IOException;

public class UV {
    public float u;
    public float v;

    public UV(){
        this.u = 0;
        this.v = 0;
    }

    public UV(DataInputStream dis) throws IOException {
        u = dis.readFloat();
        v = dis.readFloat();
    }

    public UV(float u, float v){
        this.u = u;
        this.v = v;
    }
}
