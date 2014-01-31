package skullMod.lvlEdit.dataStructures;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * A 4x4 matrix
 */
public class Mat4 implements Serializable {
    float[] matrix = new float[16]; //Goes from top to bottom then one right
    public Mat4(DataInputStream dis) throws IOException {
        if(dis == null){ throw new IllegalArgumentException("Given stream is null"); }
        for(int i = 0;i < 16;i++){
            matrix[i] = dis.readFloat();
        }
    }

    public Mat4(float[] matrix){
        set(matrix);
    }

    public float[] get(){
        return matrix.clone();
    }

    public void set(float[] matrix){
        if(matrix == null){ throw new IllegalArgumentException("Given matrix is null"); }
        if(matrix.length != 16){ throw new IllegalArgumentException("Given matrix is not a 4x4 matrix, length is: " + matrix.length); }
        //TODO can one entry be null?
        this.matrix = matrix.clone();
    }

    public void writeToStream(DataOutputStream s) throws IOException{
        for(float component : matrix){
            s.writeFloat(component);
        }
    }
}
