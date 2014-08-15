package skullMod.lvlEdit.dataStructures.LVL;

import java.io.Serializable;

public class CameraSetup implements Serializable{
    public float fieldOfView;
    public float zNear, zFar;

    public CameraSetup(float fieldOfView, float zNear, float zFar){
        this.fieldOfView = fieldOfView;
        this.zNear = zNear;
        this.zFar = zFar;
    }
}
