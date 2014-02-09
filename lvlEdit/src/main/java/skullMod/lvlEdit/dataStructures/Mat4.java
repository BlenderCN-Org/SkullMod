package skullMod.lvlEdit.dataStructures;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * A 4x4 matrix
 *
 * COLUMN major?
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

    public Mat4() {
        this.matrix = new float[16];
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

    public static Mat4 readFromStream(DataInputStream s) throws IOException{
        float[] result = new float[16];
        for(int i = 0;i < 16;i++){
            result[i] = s.readFloat();
        }
        return new Mat4(result);
    }

    public void writeToStream(DataOutputStream s) throws IOException{
        for(float component : matrix){
            s.writeFloat(component);
        }
    }

    public static String toString(Mat4 matrix){
        String result = "";

        float[] matrixFloats = matrix.get();

        result += matrixFloats[0] + " " + matrixFloats[4] + " " + matrixFloats[8] + " " + matrixFloats[12] + "\n";
        result += matrixFloats[1] + " " + matrixFloats[5] + " " + matrixFloats[9] + " " + matrixFloats[13] + "\n";
        result += matrixFloats[2] + " " + matrixFloats[6] + " " + matrixFloats[10] + " " + matrixFloats[14] + "\n";
        result += matrixFloats[3] + " " + matrixFloats[7] + " " + matrixFloats[11] + " " + matrixFloats[15] + "\n";

        return result;
    }

    public String toString(){
        return toString(this);
    }
}
