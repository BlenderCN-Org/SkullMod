package skullMod.lvlEdit.dataStructures.SGM;

import java.io.DataInputStream;
import java.io.IOException;

public class Color {
    //TODO verifiy, currently only a guess

    public byte r;
    public byte g;
    public byte b;
    public byte a;

    public Color(DataInputStream dis) throws IOException {
        r = dis.readByte();
        g = dis.readByte();
        b = dis.readByte();
        a = dis.readByte();
    }
}
