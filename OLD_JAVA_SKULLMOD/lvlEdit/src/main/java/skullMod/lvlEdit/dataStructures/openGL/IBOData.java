package skullMod.lvlEdit.dataStructures.openGL;

import java.nio.Buffer;

public class IBOData {
    public final Buffer data;
    public final int sizeInBytes;
    public final int glType;

    public IBOData(Buffer data, int sizeInBytes, int glType){
        this.data = data;
        this.sizeInBytes = sizeInBytes;
        this.glType = glType;
    }
}
