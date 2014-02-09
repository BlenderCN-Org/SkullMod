package skullMod.lvlEdit.openGL;

import skullMod.lvlEdit.dataStructures.Mat4;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;



public class OneTriangle {
    public static Mat4 projectionMatrix;
    public static SkullModVAO sampleVAO;
    protected static void setup( GL3 gl3, int width, int height ) {
        gl3.glViewport( 0, 0, width, height );
        float ratio = (1.0f * width) / height;
        projectionMatrix = new Mat4();
        Mini_GLUT.buildProjectionMatrix(66,ratio,0.5f,1000f, projectionMatrix);

        System.out.println("Projection matrix: " + projectionMatrix.toString());


        float[] vertexDataFloat = { -1.0f,-1.0f,1.0f,  1.0f,-1.0f,1.0f, 1.0f,1.0f,1.0f};
        FloatBuffer vertexData = FloatBuffer.wrap(vertexDataFloat);

        short[] iboShortData = {(short) 0,(short)1,(short)2};
        ShortBuffer iboData = ShortBuffer.wrap(iboShortData);







        sampleVAO = new SkullModVAO(gl3.getGL(), false, vertexData, iboData);
    }

    protected static void render( GL3 gl3, int width, int height ) {
        gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        //SET CAMERA (not for now)
        gl3.glUseProgram(sampleVAO.shaderProgram.shaderProgramID);
        gl3.glUniformMatrix4fv(sampleVAO.shaderProgram.uniforms.uniforms.get("p"),1,false,projectionMatrix.get(),0);

        gl3.glBindVertexArray(sampleVAO.vaoID);
        gl3.glDrawElements(GL.GL_TRIANGLES, 3,GL.GL_UNSIGNED_SHORT, 0);
        gl3.glBindVertexArray(0);
        gl3.glUseProgram(0);


        Mini_GLUT.checkGlError(gl3.getGL());
    }
}