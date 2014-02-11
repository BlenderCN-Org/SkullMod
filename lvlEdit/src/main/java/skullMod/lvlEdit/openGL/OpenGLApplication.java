package skullMod.lvlEdit.openGL;

import com.jogamp.common.nio.Buffers;
import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.openGL3.MiniGLUT2;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class OpenGLApplication implements GLEventListener{
    public static Mat4 projectionMatrix;
    public static Mat4 viewMatrix;



    public float fieldOfView, zNear, zFar;

    public final float defaultFieldOfView = 66, defaultZNear = 3, defaultZFar = 20000;

    public SimpleShaderProgram shaderProgram;

    public Object3D object3D;

    public OpenGLApplication(){
        //TODO this is not flexible for now
        fieldOfView = defaultFieldOfView;
        zNear = defaultZNear;
        zFar = defaultZFar;

        viewMatrix = new Mat4();
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        System.out.println("INIT");
        GL3 gl3 = glAutoDrawable.getGL().getGL3();
        gl3.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
        gl3.glEnable(GL3.GL_DEPTH_TEST); //TODO is this required?



        //Load shader
        String vertexShaderSource = Mini_GLUT.loadFileAsString("C:\\shaders\\color.vert");
        String fragmentShaderSource = Mini_GLUT.loadFileAsString("C:\\shaders\\color.frag");

        shaderProgram = new SimpleShaderProgram("TEST", vertexShaderSource, fragmentShaderSource, glAutoDrawable.getGL());

        //TODO remove fixed data
        // Data for triangle 1
        float vertices1[] = { -3.0f, 0.0f, -5.0f, 1.0f, -1.0f, 0.0f, -5.0f, 1.0f,-2.0f, 2.0f, -5.0f, 1.0f };
        float colors1[] = { 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,0.0f, 1.0f, 1.0f };

        object3D = new Object3D(glAutoDrawable.getGL(), false, Buffers.newDirectFloatBuffer(vertices1), Buffers.newDirectFloatBuffer(colors1), shaderProgram.shaderProgramID);
        //sampleVAO = new Object3D(gl3.getGL(), false, vertexData, iboData);
    }
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }
    public void display(GLAutoDrawable glAutoDrawable) {
        System.out.println("DISPLAY");
        renderScene(glAutoDrawable.getGL().getGL3());
        //OneTriangle.render(glAutoDrawable.getGL().getGL3(),glAutoDrawable.getWidth(),glAutoDrawable.getHeight());
    }
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        Mini_GLUT.changeViewport(glAutoDrawable.getGL().getGL3(), width, height);
        //projectionMatrix = Mini_GLUT.recalculateProjectionMatrix(width, height, fieldOfView, zNear,zFar,projectionMatrix);
        projectionMatrix = Mini_GLUT.recalculateProjectionMatrix(width, height, 53.13f, 1.0f, 30.0f,projectionMatrix);

        System.out.println("Projection matrix: " + projectionMatrix.toString());
    }

    void setUniforms(GL3 gl) {
        int projMatrixLoc = gl.glGetUniformLocation(shaderProgram.shaderProgramID, "projMatrix");
        int viewMatrixLoc = gl.glGetUniformLocation(shaderProgram.shaderProgramID, "viewMatrix");

        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projectionMatrix.get(), 0);
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix.get(), 0);
    }

    void setCamera(float posX, float posY, float posZ, float lookAtX,
                   float lookAtY, float lookAtZ, float[] viewMatrix) {

        float[] dir = new float[3];
        float[] right = new float[3];
        float[] up = new float[3];

        up[0] = 0.0f;
        up[1] = 1.0f;
        up[2] = 0.0f;

        dir[0] = (lookAtX - posX);
        dir[1] = (lookAtY - posY);
        dir[2] = (lookAtZ - posZ);
        MiniGLUT2.normalize(dir);

        MiniGLUT2.crossProduct(dir, up, right);
        MiniGLUT2.normalize(right);

        MiniGLUT2.crossProduct(right, dir, up);
        MiniGLUT2.normalize(up);

        float[] aux = new float[16];

        viewMatrix[0] = right[0];
        viewMatrix[4] = right[1];
        viewMatrix[8] = right[2];
        viewMatrix[12] = 0.0f;

        viewMatrix[1] = up[0];
        viewMatrix[5] = up[1];
        viewMatrix[9] = up[2];
        viewMatrix[13] = 0.0f;

        viewMatrix[2] = -dir[0];
        viewMatrix[6] = -dir[1];
        viewMatrix[10] = -dir[2];
        viewMatrix[14] = 0.0f;

        viewMatrix[3] = 0.0f;
        viewMatrix[7] = 0.0f;
        viewMatrix[11] = 0.0f;
        viewMatrix[15] = 1.0f;

        MiniGLUT2.setTranslationMatrix(aux, -posX, -posY, -posZ);

        MiniGLUT2.multMatrix(viewMatrix, aux);

    }


    public void renderScene(GL3 gl3){
        gl3.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        setCamera(10,2,10,0,2,-5, viewMatrix.get());

        gl3.glUseProgram(shaderProgram.shaderProgramID);
        setUniforms(gl3);

        gl3.glBindVertexArray(object3D.vaoID);
        gl3.glDrawArrays(GL3.GL_TRIANGLES, 0, 3);

    }
}
