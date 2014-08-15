package skullMod.lvlEdit.utility;

import java.io.Serializable;

/**
 * User: Markus
 * Date: 31.12.13
 * Time: 13:49
 */
public class UChar4 implements Serializable{
    private byte x;
    private byte y;
    private byte z;
    private byte w;

    public int xAsInt(){ return x & 0xFF; }
    public int yAsInt(){ return y & 0xFF; }
    public int zAsInt(){ return z & 0xFF; }
    public int wAsInt(){ return w & 0xFF; }

    public byte xAsSignedByte(){ return x; }
    public byte yAsSignedByte(){ return y; }
    public byte zAsSignedByte(){ return z; }
    public byte wxAsSignedByte(){ return w; }

    public void setX(byte x){ this.x = x; }
    public void setY(byte y){ this.y = y; }
    public void setZ(byte z){ this.z = z; }
    public void setW(byte w){ this.w = w; }



}
