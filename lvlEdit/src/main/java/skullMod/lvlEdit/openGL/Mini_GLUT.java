package skullMod.lvlEdit.openGL;

import skullMod.lvlEdit.dataStructures.Mat4;

public final class Mini_GLUT {
    private Mini_GLUT(){}

    public static float[] glhPerspectivef2(Mat4 matrix, float fovyInDegrees, float aspectRatio,
                          float znear, float zfar){
        float ymax, xmax;
        float temp, temp2, temp3, temp4;
        ymax = znear * (float) Math.tan(fovyInDegrees * Math.PI / 360.0);
        //ymin = -ymax;
        //xmin = -ymax * aspectRatio;
        xmax = ymax * aspectRatio;
        return glhFrustum(matrix, -xmax, xmax, -ymax, ymax, znear, zfar);
    }
    public static float[] glhFrustum(Mat4 matrix, float left, float right, float bottom, float top,
                      float zNear, float zFar){
        float temp, temp2, temp3, temp4;
        temp = 2.0f * zNear;
        temp2 = right - left;
        temp3 = top - bottom;
        temp4 = zFar - zNear;

        float[] matrixArray = new float[16];

        matrixArray[0] = temp / temp2;
        matrixArray[1] = 0.0f;
        matrixArray[2] = 0.0f;
        matrixArray[3] = 0.0f;
        matrixArray[4] = 0.0f;
        matrixArray[5] = temp / temp3;
        matrixArray[6] = 0.0f;
        matrixArray[7] = 0.0f;
        matrixArray[8] = (right + left) / temp2;
        matrixArray[9] = (top + bottom) / temp3;
        matrixArray[10] = (-zFar - zNear) / temp4;
        matrixArray[11] = -1.0f;
        matrixArray[12] = 0.0f;
        matrixArray[13] = 0.0f;
        matrixArray[14] = (-temp * zFar) / temp4;
        matrixArray[15] = 0.0f;

        return matrixArray;
    }


}
