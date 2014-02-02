package skullMod.lvlEdit.dataStructures.LVL;

import java.io.Serializable;

public class CameraTilt implements Serializable{


    public float tiltRate;
    public int tiltHeight1;
    public int tiltHeight2;


    public CameraTilt(float tiltRate, int tiltHeight1, int tiltHeight2){
        this.tiltRate = tiltRate;
        this.tiltHeight1 = tiltHeight1;
        this.tiltHeight2 = tiltHeight2;
    }
}
