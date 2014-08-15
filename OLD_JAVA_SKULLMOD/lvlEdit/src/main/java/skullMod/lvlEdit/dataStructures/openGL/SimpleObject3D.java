package skullMod.lvlEdit.dataStructures.openGL;

import com.jogamp.common.nio.Buffers;
import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.openGL.Mini_GLUT;
import skullMod.lvlEdit.openGL3.MiniGLUT2;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import java.nio.IntBuffer;

public class SimpleObject3D {
    public final int vao;
    public final VBOData vboData;
    public final Mat4 modelMatrix;

    public SimpleObject3D(GL3 gl3, VBOData vboData){
        this.vboData = vboData;
        /*Generate vao*/
        IntBuffer vaoBuffer = IntBuffer.allocate(1);
        gl3.glGenVertexArrays(1, vaoBuffer);
        this.vao = vaoBuffer.get(0);

        gl3.glBindVertexArray(vao);
        /*Generate vbo*/
        vboData.setup(gl3);

        modelMatrix = new Mat4();
        MiniGLUT2.setIdentityMatrix(modelMatrix.get(),4);
    }

    public void render(GL3 gl3){
        gl3.glBindVertexArray(vao);

        if(vboData.hasIBO()){
            gl3.glDrawElements(vboData.glDrawType, vboData.nOfElements, vboData.iboData.glType,0);
        }else{
            gl3.glDrawArrays(vboData.glDrawType, 0, vboData.nOfElements);
        }
    }

    public void deleteVBO(GL3 gl3){
        gl3.glBindVertexArray(0);

        gl3.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
        {
            int vbos[] = new int[1];
            vbos[0] = vboData.getVBO();
            gl3.glDeleteBuffers(1, Buffers.newDirectIntBuffer(vbos));
        }

        if(vboData.hasIBO()){
            gl3.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,0);
            int ibos[] = new int[1];
            ibos[0] = vboData.getIBO();
            gl3.glDeleteBuffers(1, Buffers.newDirectIntBuffer(ibos));
        }

        {
            int vaos[] = new int[1];
            vaos[0] = vao;
            gl3.glDeleteVertexArrays(1, Buffers.newDirectIntBuffer(vaos));
        }
    }
}
