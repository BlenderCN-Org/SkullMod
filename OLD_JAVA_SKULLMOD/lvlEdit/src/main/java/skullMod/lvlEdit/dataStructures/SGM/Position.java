package skullMod.lvlEdit.dataStructures.SGM;

import java.io.DataInputStream;
import java.io.IOException;

public class Position {
    public float xPos;
    public float yPos;
    public float zPos;
    public Position(DataInputStream dis) throws IOException {
        xPos = dis.readFloat();
        yPos = dis.readFloat();
        zPos = dis.readFloat();
    }

    public Position(){
        this.xPos = 0;
        this.yPos = 0;
        this.zPos = 0;
    }
}
