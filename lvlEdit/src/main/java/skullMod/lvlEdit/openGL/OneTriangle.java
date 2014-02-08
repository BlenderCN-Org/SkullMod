package skullMod.lvlEdit.openGL;

import skullMod.lvlEdit.dataStructures.Mat4;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

public class OneTriangle {
    public static Mat4 projectionMatrix;
    protected static void setup( GL3 gl3, int width, int height ) {
        gl3.glViewport( 0, 0, width, height );
        float ratio = (1.0f * width) / height;
        projectionMatrix = new Mat4();
        Mini_GLUT.buildProjectionMatrix(66,ratio,1f,1000f, projectionMatrix);

        System.out.println(projectionMatrix.toString());
    }

    protected static void render( GL3 gl3, int width, int height ) {
        gl3.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);


    }
}