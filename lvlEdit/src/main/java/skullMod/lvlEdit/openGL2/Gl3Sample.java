package skullMod.lvlEdit.openGL2;

import static javax.media.opengl.GL.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.media.opengl.*;
import javax.media.opengl.awt.*;

import com.jogamp.common.nio.*;
import skullMod.lvlEdit.openGL.Mini_GLUT;

    /**
     * This is a port of some sample code from:
     * http://www.lighthouse3d.com/cg-topics/code-samples/opengl-3-3-glsl-1-5-sample/
     */
    public class Gl3Sample implements GLEventListener {
        // Data for drawing Axis
        float verticesAxis[] = { -20.0f, 0.0f, 0.0f, 1.0f, 20.0f, 0.0f, 0.0f, 1.0f,
                0.0f, -20.0f, 0.0f, 1.0f, 0.0f, 20.0f, 0.0f, 1.0f,
                0.0f, 0.0f, -20.0f, 1.0f, 0.0f, 0.0f, 20.0f, 1.0f };

        float colorAxis[] = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f };
        // Data for triangle 1
        float vertices1[] = { -3.0f, 0.0f, -5.0f, 1.0f, -1.0f, 0.0f, -5.0f, 1.0f,-2.0f, 2.0f, -5.0f, 1.0f };
        float colors1[] = { 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,0.0f, 1.0f, 1.0f };
        // Data for triangle 2
        float vertices2[] = { 1.0f, 0.0f, -5.0f, 1.0f, 3.0f, 0.0f, -5.0f, 1.0f,2.0f, 2.0f, -5.0f, 1.0f };
        float colors2[] = { 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f };

        // Program and Shader Identifiers
        int p, v, f;

        // Vertex Attribute Locations
        int vertexLoc, colorLoc;

        // Uniform variable Locations
        int projMatrixLoc, viewMatrixLoc;

        // Vertex Array Objects Identifiers
        int vao[] = new int[3];

        // storage for Matrices
        float projMatrix[] = new float[16];
        float viewMatrix[] = new float[16];

        //VECTOR METHODS
        // res = a cross b;
        void crossProduct(float a[], float b[], float res[]) {

            res[0] = a[1] * b[2] - b[1] * a[2];
            res[1] = a[2] * b[0] - b[2] * a[0];
            res[2] = a[0] * b[1] - b[0] * a[1];
        }

        // Normalize a vec3
        void normalize(float a[]) {

            float mag = (float) Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);

            a[0] /= mag;
            a[1] /= mag;
            a[2] /= mag;
        }
// MATRIX STUFF
        // sets the square matrix mat to the identity matrix,
// size refers to the number of rows (or columns)
        void setIdentityMatrix(float[] mat, int size) {

// fill matrix with 0s
            for (int i = 0; i < size * size; ++i)
                mat[i] = 0.0f;

// fill diagonal with 1s
            for (int i = 0; i < size; ++i)
                mat[i + i * size] = 1.0f;
        }

// a = a * b;
//
        void multMatrix(float[] a, float[] b) {

            float[] res = new float[16];

            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 4; ++j) {
                    res[j * 4 + i] = 0.0f;
                    for (int k = 0; k < 4; ++k) {
                        res[j * 4 + i] += a[k * 4 + i] * b[j * 4 + k];
                    }
                }
            }

            System.arraycopy(res, 0, a, 0, 16);
        }

        // Defines a transformation matrix mat with a translation
        void setTranslationMatrix(float[] mat, float x, float y, float z) {

            setIdentityMatrix(mat, 4);
            mat[12] = x;
            mat[13] = y;
            mat[14] = z;
        }
// Projection Matrix
        void buildProjectionMatrix(float fov, float ratio, float nearP, float farP) {

            float f = 1.0f / (float) Math.tan(fov * (Math.PI / 360.0));

            setIdentityMatrix(projMatrix, 4);

            projMatrix[0] = f / ratio;
            projMatrix[1 * 4 + 1] = f;
            projMatrix[2 * 4 + 2] = (farP + nearP) / (nearP - farP);
            projMatrix[3 * 4 + 2] = (2.0f * farP * nearP) / (nearP - farP);
            projMatrix[2 * 4 + 3] = -1.0f;
            projMatrix[3 * 4 + 3] = 0.0f;
        }
// View Matrix
//
// note: it assumes the camera is not tilted,
// i.e. a vertical up vector (remmeber gluLookAt?)
        void setCamera(float posX, float posY, float posZ, float lookAtX,
                       float lookAtY, float lookAtZ) {

            float[] dir = new float[3];
            float[] right = new float[3];
            float[] up = new float[3];

            up[0] = 0.0f;
            up[1] = 1.0f;
            up[2] = 0.0f;

            dir[0] = (lookAtX - posX);
            dir[1] = (lookAtY - posY);
            dir[2] = (lookAtZ - posZ);
            normalize(dir);

            crossProduct(dir, up, right);
            normalize(right);

            crossProduct(right, dir, up);
            normalize(up);

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

            setTranslationMatrix(aux, -posX, -posY, -posZ);

            multMatrix(viewMatrix, aux);
        }

// —————————————————-
        void changeSize(GL3 gl, int w, int h) {

            float ratio;
// Prevent a divide by zero, when window is too short
// (you cant make a window of zero width).
            if (h == 0)
                h = 1;

// Set the viewport to be the entire window
            gl.glViewport(0, 0, w, h);

            ratio = (1.0f * w) / h;
            buildProjectionMatrix(53.13f, ratio, 1.0f, 30.0f);
        }

        void setupBuffers(GL3 gl) {

            int buffers[] = new int[2];
            gl.glGenVertexArrays(3, vao, 0);
//
// VAO for first triangle
//
            gl.glBindVertexArray(vao[0]);
// Generate two slots for the vertex and color buffers
            gl.glGenBuffers(2, buffers, 0);
// bind buffer for vertices and copy data into buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
            gl.glBufferData(GL_ARRAY_BUFFER, vertices1.length * Float.SIZE / 8,
                    Buffers.newDirectFloatBuffer(vertices1), GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(vertexLoc);
            gl.glVertexAttribPointer(vertexLoc, 4, GL_FLOAT, false, 0, 0);

// bind buffer for colors and copy data into buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
            gl.glBufferData(GL_ARRAY_BUFFER, colors1.length * Float.SIZE / 8,
                    Buffers.newDirectFloatBuffer(colors1), GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(colorLoc);
            gl.glVertexAttribPointer(colorLoc, 4, GL_FLOAT, false, 0, 0);

//
// VAO for second triangle
//
            gl.glBindVertexArray(vao[1]);
// Generate two slots for the vertex and color buffers
            gl.glGenBuffers(2, buffers, 0);

// bind buffer for vertices and copy data into buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
            gl.glBufferData(GL_ARRAY_BUFFER, vertices2.length * Float.SIZE / 8,
                    Buffers.newDirectFloatBuffer(vertices2), GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(vertexLoc);
            gl.glVertexAttribPointer(vertexLoc, 4, GL_FLOAT, false, 0, 0);

// bind buffer for colors and copy data into buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
            gl.glBufferData(GL_ARRAY_BUFFER, colors2.length * Float.SIZE / 8,
                    Buffers.newDirectFloatBuffer(colors2), GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(colorLoc);
            gl.glVertexAttribPointer(colorLoc, 4, GL_FLOAT, false, 0, 0);

//
// This VAO is for the Axis
//
            gl.glBindVertexArray(vao[2]);
// Generate two slots for the vertex and color buffers
            gl.glGenBuffers(2, buffers, 0);
// bind buffer for vertices and copy data into buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
            gl.glBufferData(GL_ARRAY_BUFFER, verticesAxis.length * Float.SIZE / 8,
                    Buffers.newDirectFloatBuffer(verticesAxis), GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(vertexLoc);
            gl.glVertexAttribPointer(vertexLoc, 4, GL_FLOAT, false, 0, 0);

// bind buffer for colors and copy data into buffer
            gl.glBindBuffer(GL_ARRAY_BUFFER, buffers[1]);
            gl.glBufferData(GL_ARRAY_BUFFER, colorAxis.length * Float.SIZE / 8,
                    Buffers.newDirectFloatBuffer(colorAxis), GL_STATIC_DRAW);
            gl.glEnableVertexAttribArray(colorLoc);
            gl.glVertexAttribPointer(colorLoc, 4, GL_FLOAT, false, 0, 0);

        }

        void setUniforms(GL3 gl) {

// must be called after glUseProgram
            gl.glUniformMatrix4fv(projMatrixLoc, 1, false, projMatrix, 0);
            gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, viewMatrix, 0);
        }

        void renderScene(GL3 gl) {

            gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            setCamera(10, 2, 10, 0, 2, -5);

            gl.glUseProgram(p);
            setUniforms(gl);

            gl.glBindVertexArray(vao[0]);
            gl.glDrawArrays(GL_TRIANGLES, 0, 3);

            gl.glBindVertexArray(vao[1]);
            gl.glDrawArrays(GL_TRIANGLES, 0, 3);

            gl.glBindVertexArray(vao[2]);
            gl.glDrawArrays(GL_LINES, 0, 6);
        }

        /** Retrieves the info log for the shader */
        public String printShaderInfoLog(GL3 gl, int obj) {
// Otherwise, we'll get the GL info log
            final int logLen = getShaderParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
            if (logLen <= 0)
                return "";

// Get the log
            final int[] retLength = new int[1];
            final byte[] bytes = new byte[logLen + 1];
            gl.glGetShaderInfoLog(obj, logLen, retLength, 0, bytes, 0);
            final String logMessage = new String(bytes);

            return String.format("ShaderLog: %s", logMessage);
        }

        /** Get a shader parameter value. See 'glGetShaderiv' */
        private int getShaderParameter(GL3 gl, int obj, int paramName) {
            final int params[] = new int[1];
            gl.glGetShaderiv(obj, paramName, params, 0);
            return params[0];
        }

        /** Retrieves the info log for the program */
        public String printProgramInfoLog(GL3 gl, int obj) {
// get the GL info log
            final int logLen = getProgramParameter(gl, obj, GL3.GL_INFO_LOG_LENGTH);
            if (logLen <= 0)
                return "";

            // Get the log
            final int[] retLength = new int[1];
            final byte[] bytes = new byte[logLen + 1];
            gl.glGetProgramInfoLog(obj, logLen, retLength, 0, bytes, 0);
            final String logMessage = new String(bytes);

            return logMessage;
        }

        /** Gets a program parameter value */
        public int getProgramParameter(GL3 gl, int obj, int paramName) {
            final int params[] = new int[1];
            gl.glGetProgramiv(obj, paramName, params, 0);
            return params[0];
        }

        int setupShaders(GL3 gl) {

            String vs = null;
            String fs = null;
            String fs2 = null;

            int p, v, f;

            v = gl.glCreateShader(GL3.GL_VERTEX_SHADER);
            f = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);

            //vs = Mini_GLUT.loadFileAsString("C:\\shaders\\color.vert");
            //fs = Mini_GLUT.loadFileAsString("C:\\shaders\\color.frag");

            vs = Mini_GLUT.loadFileAsString("/home/netbook/Working_files/testEnvironment/color.vert");
            fs = Mini_GLUT.loadFileAsString("/home/netbook/Working_files/testEnvironment/color.frag");

            String vv = vs;
            String ff = fs;

            gl.glShaderSource(v, 1, new String[] { vv }, null);
            gl.glShaderSource(f, 1, new String[] { ff }, null);

            gl.glCompileShader(v);
            gl.glCompileShader(f);

            printShaderInfoLog(gl, v);
            printShaderInfoLog(gl, f);

            p = gl.glCreateProgram();
            gl.glAttachShader(p, v);
            gl.glAttachShader(p, f);

            gl.glBindFragDataLocation(p, 0, "outputF");
            gl.glLinkProgram(p);
            printProgramInfoLog(gl, p);

            vertexLoc = gl.glGetAttribLocation(p, "position");
            colorLoc = gl.glGetAttribLocation(p, "color");

            projMatrixLoc = gl.glGetUniformLocation(p, "projMatrix");
            viewMatrixLoc = gl.glGetUniformLocation(p, "viewMatrix");

            return (p);
        }
        /** GL Init */
        public void init(GLAutoDrawable drawable) {
            GL3 gl = drawable.getGL().getGL3();
            gl.glEnable(GL_DEPTH_TEST);
            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

            p = setupShaders(gl);
            setupBuffers(gl);
        }

        /** GL Window Reshape */
        public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                            int height) {
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
            Gl3Sample sample = new Gl3Sample();

            GLProfile glp = GLProfile.get(GLProfile.GL3);
            GLCapabilities glCapabilities = new GLCapabilities(glp);
            final GLCanvas glCanvas = new GLCanvas(glCapabilities);
            final Frame frame = new Frame("GL3 Test");
            glCanvas.addGLEventListener(sample);

            frame.add(glCanvas);
            frame.addWindowListener(new WindowAdapter() {
                @Override
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
