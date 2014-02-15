package skullMod.lvlEdit.dataStructures.openGL;

import com.jogamp.common.nio.Buffers;
import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.openGL.Mini_GLUT;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import java.awt.event.*;
import java.util.ArrayList;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;

public class OpenGL_Listener extends MouseAdapter implements GLEventListener, KeyListener, MouseMotionListener {
    float projMatrix[] = new float[16];
    float viewMatrix[] = new float[16];

    //TODO put camera stuff in a camera class
    float cameraX, cameraY, cameraZ, cameraYaw, cameraPitch, moveSpeed, sensitivity;
    long cameraTime;

    public void setNew3DData(GL3 gl3, VBOData[] vboData){
        for(SimpleObject3D model :models){
            model.deleteVBO(gl3);
        }

        models.clear();

        for(VBOData data: vboData){
            models.add(new SimpleObject3D(gl3,data));
        }

    }

    public void setupCamera(){
        cameraX = 2;
        cameraY = 0;
        cameraZ = -5;
        cameraYaw = 0;
        cameraPitch = 0;
        moveSpeed = 1f;
        sensitivity = 10f;

        cameraTime = System.currentTimeMillis();
        float[] array = {1,0,0,0,   0,1,0,0,    0,0,1,0,    0,0,0,1};
        viewMatrix = array;
    }

    public void updateCamera(){
        long currentTime = System.currentTimeMillis();
        float dt = (currentTime - cameraTime) / 1000.0f;
        cameraTime = currentTime;

        yaw(xDeltaDrag * sensitivity);
        pitch(yDeltaDrag * sensitivity);

        xDeltaDrag = 0;
        yDeltaDrag = 0;

        System.out.println("DT: " + dt);

        if (w) {
            walkForward(moveSpeed * dt);
        }
        if (s) {
            walkBackwards(moveSpeed * dt);
        }
        if (a) {
            strafeLeft(moveSpeed * dt);
        }
        if (d){
            strafeRight(moveSpeed * dt);
        }
    }
    public void pitch(float amount) { this.cameraPitch += amount; }

    public void yaw(float amount) {
        this.cameraYaw += amount;
    }

    public void walkForward(float distance) {
        cameraX -= distance * (float) Math.sin(Math.toRadians(cameraYaw));
        cameraZ += distance * (float) Math.cos(Math.toRadians(cameraYaw));
    }

    public void walkBackwards(float distance) {
        cameraX -= distance * (float) Math.sin(Math.toRadians(cameraYaw));
        cameraZ += distance * (float) Math.cos(Math.toRadians(cameraYaw));
    }

    public void strafeLeft(float distance) {
        cameraX -= distance * (float) Math.sin(Math.toRadians(cameraYaw - 90));
        cameraZ += distance * (float) Math.cos(Math.toRadians(cameraYaw - 90));
    }

    public void strafeRight(float distance) {
        cameraX -= distance * (float) Math.sin(Math.toRadians(cameraYaw + 90));
        cameraZ += distance * (float) Math.cos(Math.toRadians(cameraYaw + 90));
    }

    public static void setXYZ(float[] matrix, float x, float y, float z){
        matrix[12] = x;
        matrix[13] = y;
        matrix[14] = z;
    }

    public static void rotateX(float[] matrix, float rotation){
        matrix[5] += (float) Math.cos(rotation);
        matrix[6] += (float) -Math.sin(rotation);
        matrix[9] += (float) Math.sin(rotation);
        matrix[10] += (float) Math.cos(rotation);
    }

    public static void rotateY(float[] matrix, float rotation){
        matrix[0] += (float) Math.cos(rotation);
        matrix[2] += (float) Math.sin(rotation);
        matrix[8] += (float) -Math.sin(rotation);
        matrix[10] += (float) Math.cos(rotation);
    }




    float axisVertexData[] = {    -2000.0f, 0.0f, 0.0f,    /*Color*/  0.5f, 0.0f, 0.0f,       2000.0f, 0.0f, 0.0f,   /*Color*/   1.0f, 0.0f, 0.0f,
            0.0f, -2000.0f, 0.0f,  /*Color*/  0.0f, 0.5f, 0.0f,      0.0f, 2000.0f, 0.0f,  /*Color*/   0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, -2000.0f,  /*Color*/ 0.0f, 0.0f, 0.5f,     0.0f, 0.0f, 2000.0f,  /*Color*/     0.0f, 0.0f, 1.0f};
    int axisNumberOfElements = 6;
    int axisStride = Float.SIZE / 8  * 6;
    private SimpleObject3D axis;

    private ArrayList<SimpleObject3D> models = new ArrayList<>(1);

    private SimpleShaderProgram shader;
    private int projMatrixLoc;
    private int viewMatrixLoc;
    private int modelMatrixLoc;
    private int vertexLoc;
    private int colorLoc;

    public void init(GLAutoDrawable drawable) {
        setupCamera();
        GL3 gl3 = drawable.getGL().getGL3();
        gl3.glEnable(GL_DEPTH_TEST);
        gl3.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        shader = new SimpleShaderProgram("default", Mini_GLUT.loadFileAsString("shaders/vertexColor.vs",true),Mini_GLUT.loadFileAsString("shaders/vertexColor.fs",true), gl3);

        vertexLoc = gl3.glGetAttribLocation(shader.shaderProgramID, "vertexPosition");
        colorLoc = gl3.glGetAttribLocation(shader.shaderProgramID, "vertexColor");

        projMatrixLoc = gl3.glGetUniformLocation(shader.shaderProgramID, "projectionMatrix");
        viewMatrixLoc = gl3.glGetUniformLocation(shader.shaderProgramID, "viewMatrix");
        modelMatrixLoc = gl3.glGetUniformLocation(shader.shaderProgramID, "modelMatrix");



        ArrayList<VBOData.VertexAttribute> attributes = new ArrayList<>(1);

        attributes.add(new VBOData.VertexAttribute(vertexLoc, axisStride, 0, GL_FLOAT, 3));
        attributes.add(new VBOData.VertexAttribute(colorLoc, axisStride, 12, GL_FLOAT, 3));

        VBOData vboData = new VBOData(Buffers.newDirectFloatBuffer(axisVertexData), attributes,  null,axisVertexData.length * Float.SIZE / 8, 6,  GL_LINES);
        axis = new SimpleObject3D(gl3, vboData);
    }

    private int windowWidth, windowHeight;

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("RESHAPE");
        GL3 gl3 = drawable.getGL().getGL3();

        this.windowWidth = width;
        this.windowHeight = height;
        // Prevent a divide by zero, when window is too short
        // (you cant make a window of zero width).
        if (windowHeight == 0){ windowHeight = 1; }

        // Set the viewport to be the entire window
        gl3.glViewport(0, 0, width, height);



        Mat4 temp = new Mat4(projMatrix);
        Mini_GLUT.recalculateProjectionMatrix(width, height, 66f, 3.0f, 2000.0f, temp);
        projMatrix = temp.get();
    }

    public void display(GLAutoDrawable drawable) {
        float[] array = {1,0,0,0,   0,1,0,0,    0,0,1,0,    0,0,0,1};
        viewMatrix = array;
        updateCamera();
        System.out.println(cameraX + " " + cameraY + " " + cameraZ + " PITCH " + cameraPitch + " YAW " + cameraYaw);


        GL3 gl3 = drawable.getGL().getGL3();

        gl3.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


        //MiniGLUT2.setCamera(cameraX, cameraY, 10, 0, 2, -5, viewMatrix);
        rotateX(viewMatrix, (float) Math.toRadians(cameraPitch));
        rotateY(viewMatrix, (float) Math.toRadians(cameraYaw));
        setXYZ(viewMatrix, cameraX, cameraY, cameraZ);


        gl3.glUseProgram(shader.shaderProgramID);
        setUniforms(gl3);

        //{1,0,0,0,    0,1,0,0,     0,0,1,0,    0,0,0,1}
        float[] tempFloat = {1,0,0,0,    0,1,0,0,     0,0,1,0,    0,0,0,1};
        gl3.glUniformMatrix4fv(modelMatrixLoc,1, false, tempFloat,0);
        axis.render(gl3);

        for(SimpleObject3D model : models){
            model.render(gl3);
        }
    }



    public void dispose(GLAutoDrawable drawable) {
    }


    void setUniforms(GL3 gl) {
        // must be called after glUseProgram
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix, 0);
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix, 0);
    }

    boolean mouseActive = false;
    public void mouseClicked(MouseEvent e) {
        System.out.println("mouseClicked");
    }
    public void mousePressed(MouseEvent e) {
        System.out.println("mousePressed");
        mouseActive = true;

        this.xLastDrag = e.getX();
        this.yLastDrag = e.getY();

    }
    public void mouseReleased(MouseEvent e) {
        System.out.println("mouseReleased");
        mouseActive = false;
        this.xDeltaDrag = xLastDrag - e.getX();
        this.yDeltaDrag = yLastDrag - e.getY();
    }
    public void mouseEntered(MouseEvent e) {
        System.out.println("mouseEntered");
    }
    public void mouseExited(MouseEvent e) {
        System.out.println("mouseExited");
        if(mouseActive){
            this.xDeltaDrag = xLastDrag - e.getX();
            this.yDeltaDrag = yLastDrag - e.getY();
        }
        mouseActive = false;
    }

    private boolean w,a,s,d;
    public void keyTyped(KeyEvent e) {
        //System.out.println("keyTyped");
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
        char keyChar = Character.toLowerCase(e.getKeyChar());
        //FIXME this is ugly, fix it
        switch(keyChar){
            case 'w':
                w = true;
                break;
            case 'a':
                a = true;
                break;
            case 's':
                s = true;
                break;
            case 'd':
                d = true;
                break;
            default:
        }
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
        char keyChar = Character.toLowerCase(e.getKeyChar());
        //FIXME this is ugly, fix it
        switch(keyChar){
            case 'w':
                w = false;
                break;
            case 'a':
                a = false;
                break;
            case 's':
                s = false;
                break;
            case 'd':
                d = false;
                break;
            default:
        }
    }


    private int currentX, currentY, xLastDrag, yLastDrag, xDeltaDrag, yDeltaDrag;
    public void mouseDragged(MouseEvent e) {
        //System.out.println("Mouse dragged");

        currentX = e.getX();
        currentY = e.getY();

        this.xDeltaDrag = xLastDrag - e.getX();
        this.yDeltaDrag = yLastDrag - e.getY();

        xLastDrag = e.getX();
        yLastDrag = e.getY();


    }

    public void mouseMoved(MouseEvent e) {


        //System.out.println("Mouse moved to: " + currentX + " " + currentY);
    }
}
