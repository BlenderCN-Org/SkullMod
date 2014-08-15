package skullMod.lvlEdit.openGL3;

import com.jogamp.common.nio.Buffers;
import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.openGL.Mini_GLUT;
import skullMod.lvlEdit.dataStructures.openGL.SimpleShaderProgram;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static javax.media.opengl.GL.*;

/**
 * http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
 */
public class FinalTest implements GLEventListener {
    // Data for drawing Axis
    float verticesAxis[] = {    -20.0f, 0.0f, 0.0f,     20.0f, 0.0f, 0.0f,
                                0.0f, -20.0f, 0.0f,     0.0f, 20.0f, 0.0f ,
                                0.0f, 0.0f, -20.0f,     0.0f, 0.0f, 20.0f };


    float colorAxis[] = { 0.5f, 0.0f, 0.0f,    1.0f, 0.0f, 0.0f,
                          0.0f, 0.5f, 0.0f,    0.0f, 1.0f, 0.0f,
                          0.0f, 0.0f, 0.5f,    0.0f, 0.0f, 1.0f };
    // Data for triangle 1
    float vertices1[] = { -3.0f, 0.0f, -5.0f,     -1.0f, 0.0f, -5.0f,    -2.0f, 2.0f, -5.0f    };
    int vertices1IBO[] = { 0,1,2};
    float colors1[] = { 0.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f,0.0f, 1.0f};
    // Data for triangle 2
    float vertices2[] = { 1.0f, 0.0f, -5.0f,     3.0f, 0.0f, -5.0f,   2.0f, 2.0f, -5.0f    };
    int vertices2IBO[] = {0,1,2};
    float colors2[] = { 1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f};

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
// VAO for first triangle
        gl.glBindVertexArray(vao[0]);
// Generate two slots for the vertex and color buffers
        gl.glGenBuffers(2, buffers, 0);
// bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertices1.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(vertices1), GL_STATIC_DRAW);

        int[] iboBuffer = new int[1];
        gl.glGenBuffers(1,iboBuffer,0);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboBuffer[0]);
        gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, Integer.SIZE /8 * 3 , Buffers.newDirectIntBuffer(vertices1IBO), GL_STATIC_DRAW);



        gl.glEnableVertexAttribArray(vertexLoc);
        gl.glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, 0, 0);

// bind buffer for colors and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
        gl.glBufferData(GL_ARRAY_BUFFER, colors1.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(colors1), GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(colorLoc);
        gl.glVertexAttribPointer(colorLoc, 3, GL_FLOAT, false, 0, 0);

// VAO for second triangle
        gl.glBindVertexArray(vao[1]);
// Generate two slots for the vertex and color buffers
        gl.glGenBuffers(2, buffers, 0);

// bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertices2.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(vertices2), GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(vertexLoc);
        gl.glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, 0, 0);

// bind buffer for colors and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
        gl.glBufferData(GL_ARRAY_BUFFER, colors2.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(colors2), GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(colorLoc);
        gl.glVertexAttribPointer(colorLoc, 3, GL_FLOAT, false, 0, 0);

// This VAO is for the Axis
        gl.glBindVertexArray(vao[2]);
// Generate two slots for the vertex and color buffers
        gl.glGenBuffers(2, buffers, 0);
// bind buffer for vertices and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, verticesAxis.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(verticesAxis), GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(vertexLoc);
        gl.glVertexAttribPointer(vertexLoc, 3, GL_FLOAT, false, 0, 0);

// bind buffer for colors and copy data into buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
        gl.glBufferData(GL_ARRAY_BUFFER, colorAxis.length * Float.SIZE / 8,
                Buffers.newDirectFloatBuffer(colorAxis), GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(colorLoc);
        gl.glVertexAttribPointer(colorLoc, 3, GL_FLOAT, false, 0, 0);

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

        gl.glBindVertexArray(vao[0]);
        //gl.glDrawArrays(GL_TRIANGLES, 0, 3);
        gl.glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT,0);

        gl.glBindVertexArray(vao[1]);
        gl.glDrawArrays(GL_TRIANGLES, 0, 3);

        gl.glBindVertexArray(vao[2]);
        gl.glDrawArrays(GL_LINES, 0, 6);
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
        FinalTest sample = new FinalTest();

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
