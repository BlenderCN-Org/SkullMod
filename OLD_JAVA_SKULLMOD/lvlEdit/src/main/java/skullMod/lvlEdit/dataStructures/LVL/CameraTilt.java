package skullMod.lvlEdit.dataStructures.LVL;

import java.io.Serializable;

public class CameraTilt implements Serializable{


    public float tiltRate;
    public float tiltHeight1;
    public float tiltHeight2;


    public CameraTilt(float tiltRate, float tiltHeight1, float tiltHeight2){
        this.tiltRate = tiltRate;
        this.tiltHeight1 = tiltHeight1;
        this.tiltHeight2 = tiltHeight2;
    }
}
