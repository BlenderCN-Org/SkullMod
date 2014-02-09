package skullMod.lvlEdit.openGL;

import skullMod.lvlEdit.dataStructures.Mat4;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static javax.media.opengl.GL.*;

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


    public static void checkGlError(GL gl) {
        ArrayList<String> errorList = new ArrayList<>();

        int errorID = gl.glGetError();

        String currentError;

        while(errorID != GL_NO_ERROR) {
            switch(errorID) {
                case GL_INVALID_OPERATION:
                    currentError = "INVALID_OPERATION";
                    break;
                case GL_INVALID_ENUM:
                    currentError = "INVALID_ENUM";
                    break;
                case GL_INVALID_VALUE:
                    currentError = "INVALID_VALUE";
                    break;
                case GL_OUT_OF_MEMORY:
                    currentError = "OUT_OF_MEMORY";
                    break;
                case GL_INVALID_FRAMEBUFFER_OPERATION:
                    currentError = "INVALID_FRAMEBUFFER_OPERATION";
                    break;
                default:
                    currentError = "Unknown error";
            }

            //TODO no robust enough
            System.out.println(currentError);



            errorID = gl.glGetError();
        }

        if(errorList.size() > 0){ throw new OGLException(errorList.toArray(new String[errorList.size()])); }
    }

    public static String checkGLShaderCompileError(GL gl, int shaderID){
        GL3 gl3 = gl.getGL3();

        int status[] = { 0 };
        gl3.glGetShaderiv(shaderID,GL3.GL_COMPILE_STATUS, status,0);

        if (status[0] == GL_FALSE){
            int[] val = { 0 };
            gl3.glGetShaderiv(shaderID, GL3.GL_INFO_LOG_LENGTH, val, 0);
            int length = val[0];

            byte[] log = new byte[length];
            gl3.glGetShaderInfoLog(shaderID, length, val, 0, log, 0);
            return new String(log);
        }else{
            return null;
        }
    }

    public static String checkGLShaderLinkError(GL gl, int shaderProgramID){
        GL3 gl3 = gl.getGL3();
        IntBuffer linkSuccess = IntBuffer.allocate(1);
        gl3.glGetProgramiv(shaderProgramID, GL3.GL_LINK_STATUS, linkSuccess);


        if(linkSuccess.array()[0] == GL3.GL_FALSE){
            IntBuffer infoLogLength = IntBuffer.allocate(1);
            gl3.glGetProgramiv(shaderProgramID, GL3.GL_INFO_LOG_LENGTH, infoLogLength);
            int size = infoLogLength.get(0);

            String exceptionText;
            if (size > 0){
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl3.glGetProgramInfoLog(shaderProgramID, size, infoLogLength, byteBuffer);
                exceptionText = "Program link error: " + new String(byteBuffer.array());
            }else{
                exceptionText = "Unknown shader link error, no info log provided";
            }
            return exceptionText;
        }else{
            return null;
        }
    }




    public static void buildProjectionMatrix(float fov, float ratio, float nearP, float farP, Mat4 projectionMatrix) {
        float[] result = { 1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1 }; //Start with identity matrix
        float f =  (float) (1.0f / Math.tan(fov * (Math.PI / 360.0)));

        result[0] = f / ratio;
        result[1 * 4 + 1] = f;
        result[2 * 4 + 2] = (farP + nearP) / (nearP - farP);
        result[3 * 4 + 2] = (2.0f * farP * nearP) / (nearP - farP);
        result[2 * 4 + 3] = -1.0f;
        result[3 * 4 + 3] = 0.0f;

        projectionMatrix.set(result);
    }


    public static String loadFileAsString(String fileName) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }
}
