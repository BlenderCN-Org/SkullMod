package skullMod.lvlEdit.openGL4;

import com.jogamp.common.nio.Buffers;
import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.dataStructures.openGL.IBOData;
import skullMod.lvlEdit.dataStructures.openGL.SimpleObject3D;
import skullMod.lvlEdit.dataStructures.openGL.VBOData;
import skullMod.lvlEdit.openGL.Mini_GLUT;
import skullMod.lvlEdit.dataStructures.openGL.SimpleShaderProgram;
import skullMod.lvlEdit.openGL3.MiniGLUT2;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import static javax.media.opengl.GL.*;

/**
 * http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
 */
public class FinalTest3 implements GLEventListener {
    SimpleObject3D axis, triangle1;

    // Data for drawing Axis
    float axisVertexData[] = {    -20.0f, 0.0f, 0.0f,    /*Color*/  0.5f, 0.0f, 0.0f,       20.0f, 0.0f, 0.0f,   /*Color*/   1.0f, 0.0f, 0.0f,
                                0.0f, -20.0f, 0.0f,  /*Color*/  0.0f, 0.5f, 0.0f,      0.0f, 20.0f, 0.0f,  /*Color*/   0.0f, 1.0f, 0.0f,
                                0.0f, 0.0f, -20.0f,  /*Color*/ 0.0f, 0.0f, 0.5f,     0.0f, 0.0f, 20.0f,  /*Color*/     0.0f, 0.0f, 1.0f};
    int axisStride = Float.SIZE / 8  * 6;

    // Data for triangle 1
    float triangle1VertexData[] = { -3.0f, 0.0f, -5.0f, /*Color*/ 1.0f, 0.0f, 0.0f,       -1.0f, 0.0f, -5.0f,    /*Color*/ 0.0f, 1.0f, 0.0f,     -2.0f, 2.0f, -5.0f,  /*COLOR*/   0.0f,0.0f, 1.0f,
            -2.0f, 2.0f, -10.0f,  /*COLOR*/   0.0f,1.0f, 1.0f};
    int triangle1IndicesData[] = { 0,1,2, 2,1,3};

    int triangleStride = Float.SIZE / 8 * 6;

    // Data for triangle 2
    float triangle2VertexData[] = { 1.0f, 0.0f, -5.0f, /*Color*/ 1.0f, 0.0f, 0.0f,      3.0f, 0.0f, -5.0f,   /*Color*/1.0f, 0.0f, 0.0f,      2.0f, 2.0f, -5.0f,   /*COLOR*/ 1.0f, 0.0f, 0.0f};
    int triangle2Stride = Float.SIZE / 8 * 6;
    //int triangle2IndicesData[] = { 0,1,2};

    SimpleShaderProgram shaderProgram;

    // Vertex Attribute Locations
    int vertexLoc, colorLoc;

    // Uniform variable Locations
    int projMatrixLoc, viewMatrixLoc, modelMatrixLoc;

    // Vertex Array Objects Identifiers
    int vao[] = new int[3];

    // storage for Matrices
    float projMatrix[] = new float[16];
    float viewMatrix[] = new float[16];
    float modelMatrix[] = new float[16];

    void changeSize(GL3 gl, int w, int h) {

        float ratio;
// Prevent a divide by zero, when window is too short
// (you cant make a window of zero width).
        if (h == 0)
            h = 1;

// Set the viewport to be the entire window
        gl.glViewport(0, 0, w, h);

        ratio = (1.0f * w) / h;

        Mat4 temp = new Mat4(projMatrix);
        Mini_GLUT.recalculateProjectionMatrix(w,h,66f, 1.0f, 30.0f, temp);
        projMatrix = temp.get();
    }

    void setupBuffers(GL3 gl) {


        int buffers[] = new int[2];
        gl.glGenVertexArrays(3, vao, 0);


        //gl.glBindVertexArray(vao[2]);


        ArrayList<VBOData.VertexAttribute> attributes = new ArrayList<>(1);

        attributes.add(new VBOData.VertexAttribute(vertexLoc, axisStride, 0, GL_FLOAT, 3));
        attributes.add(new VBOData.VertexAttribute(colorLoc, axisStride, 12, GL_FLOAT, 3));

        VBOData vboData = new VBOData(Buffers.newDirectFloatBuffer(axisVertexData), attributes,  null,axisVertexData.length * Float.SIZE / 8, 6,  GL_LINES);
        axis = new SimpleObject3D(gl, vboData);






        ArrayList<VBOData.VertexAttribute> attributesTriangle1 = new ArrayList<>(1);

        attributesTriangle1.add(new VBOData.VertexAttribute(vertexLoc, triangleStride, 0, GL_FLOAT, 3));
        attributesTriangle1.add(new VBOData.VertexAttribute(colorLoc, triangleStride, 12, GL_FLOAT, 3));

        IBOData iboData = new IBOData(Buffers.newDirectIntBuffer(triangle1IndicesData), triangle1IndicesData.length * Integer.SIZE / 8, GL_UNSIGNED_INT);
        VBOData vboDataTriangle = new VBOData(Buffers.newDirectFloatBuffer(triangle1VertexData), attributesTriangle1 , iboData,  triangle1VertexData.length * Float.SIZE / 8,6,GL_TRIANGLES);

        triangle1 = new SimpleObject3D(gl, vboDataTriangle);

/*
// VAO for first triangle
        gl.glBindVertexArray(vao[0]);
// Generate two slots for the vertex and color buffers
        gl.glGenBuffers(1, buffers, 0);
// bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, triangle1VertexData.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(triangle1VertexData), GL_STATIC_DRAW);

        int[] iboBuffer = new int[1];
        gl.glGenBuffers(1,iboBuffer,0);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBuffer[0]);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, Integer.SIZE /8 * 3 , Buffers.newDirectIntBuffer(triangle1IndicesData), GL_STATIC_DRAW);



        gl.glEnableVertexAttribArray(vertexLoc);
        gl.glEnableVertexAttribArray(colorLoc);

        gl.glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, triangle1Stride, 0);                    //Layout float 0-3
        gl.glVertexAttribPointer(colorLoc, 3, GL_FLOAT, false, triangle1Stride, 3 * Float.SIZE / 8);    //Layout float 4-6
*/
// VAO for second triangle
        gl.glBindVertexArray(vao[1]);
// Generate two slots for the vertex and color buffers
        gl.glGenBuffers(1, buffers, 0);

// bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, triangle2VertexData.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(triangle2VertexData), GL_STATIC_DRAW);


        gl.glEnableVertexAttribArray(vertexLoc);
        gl.glEnableVertexAttribArray(colorLoc);

        gl.glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, triangle2Stride, 0);
        gl.glVertexAttribPointer(colorLoc,  3, GL_FLOAT, false, triangle2Stride, 3 * (Float.SIZE / 8));

// This VAO is for the Axis
        /*gl.glBindVertexArray(vao[2]);
// Generate two slots for the vertex and color buffers
        gl.glGenBuffers(2, buffers, 0);
// bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, axisVertexData.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(axisVertexData), GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(vertexLoc);
        gl.glEnableVertexAttribArray(colorLoc);

        gl.glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, axisStride, 0);
        gl.glVertexAttribPointer(colorLoc,  3, GL_FLOAT, false, axisStride, 3 * Float.SIZE / 8);  */
    }

    void setUniforms(GL3 gl) {

// must be called after glUseProgram
        gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix, 0);
        gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix, 0);
        gl.glUniformMatrix4fv(modelMatrixLoc,1, false, modelMatrix,0);
    }

    void renderScene(GL3 gl) {
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        MiniGLUT2.setCamera(10, 2, 10, 0, 2, -5, viewMatrix);

        gl.glUseProgram(shaderProgram.shaderProgramID);
        setUniforms(gl);

        //gl.glBindVertexArray(vao[0]);
        //gl.glDrawArrays(GL_TRIANGLES, 0, 3);
        //gl.glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT,0);
        triangle1.render(gl);

        gl.glBindVertexArray(vao[1]);
        gl.glDrawArrays(GL_TRIANGLES, 0, 3);

        //gl.glBindVertexArray(vao[2]);
        //gl.glDrawArrays(GL_LINES, 0, 6);

        axis.render(gl);

    }

    void setupShaders(GL3 gl) {
        SimpleShaderProgram shader = new SimpleShaderProgram("test", Mini_GLUT.loadFileAsString("shaders/vertexColor.vs",true),Mini_GLUT.loadFileAsString("shaders/vertexColor.fs",true), gl);

        MiniGLUT2.setIdentityMatrix(modelMatrix,4);

        this.shaderProgram = shader;

        vertexLoc = gl.glGetAttribLocation(shaderProgram.shaderProgramID, "vertexPosition");
        colorLoc = gl.glGetAttribLocation(shaderProgram.shaderProgramID, "vertexColor");

        projMatrixLoc = gl.glGetUniformLocation(shaderProgram.shaderProgramID, "projectionMatrix");
        viewMatrixLoc = gl.glGetUniformLocation(shaderProgram.shaderProgramID, "viewMatrix");
        modelMatrixLoc = gl.glGetUniformLocation(shaderProgram.shaderProgramID, "modelMatrix");
    }
    /** GL Init */
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glEnable(GL_DEPTH_TEST);
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        setupShaders(gl);
        setupBuffers(gl);
    }

    /** GL Window Reshape */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        changeSize(gl, width, height);
    }

    /** GL Render loop */
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        renderScene(gl);
    }

    /** GL Complete */
    public void dispose(GLAutoDrawable drawable) {
    }

    /** Main entry point for the application */
    public static void main(String[] args) {
        FinalTest3 sample = new FinalTest3();

        if(GLProfile.isAvailable("GL3")){
            JOptionPane.showMessageDialog(null,"OpenGL 3 is supported");
        }else{
            JOptionPane.showMessageDialog(null,"OpenGL 3 is NOT supported");
        }




        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glp);
        final GLCanvas glCanvas = new GLCanvas(glCapabilities);
        final Frame frame = new Frame("GL3 Test");
        glCanvas.addGLEventListener(sample);

        frame.add(glCanvas);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                frame.remove(glCanvas);
                frame.dispose();
                System.exit(0);
            }
        });

        frame.setSize(320, 320);
        frame.setVisible(true);
    }
}
