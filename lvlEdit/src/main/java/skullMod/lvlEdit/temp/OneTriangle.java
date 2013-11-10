package skullMod.lvlEdit.temp;

import com.jogamp.common.nio.Buffers;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.glu.GLU;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_STATIC_DRAW;


public class OneTriangle {
    /**
     * The current projection matrix
     */
    static float projectionMatrix[] = new float[16];

    /**
     * The current projection matrix
     */
    static float modelviewMatrix[] = new float[16];


    private float translationX = 0;
    private float translationY = 0;
    private float translationZ = -4;
    //in degrees
    private float rotationX = 40;
    private float rotationY = 30;
    private float rotationZ = 0;

    public static void init( GL3 glContext, int width, int height ) {
        // Perform the default GL initialization

        glContext.setSwapInterval(0);

        glContext.glEnable(GL_DEPTH_TEST);
        glContext.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);

        //Shaders
        int shaderProgramID = glContext.glCreateProgram();
        int vertexShaderHandle = glContext.glCreateShader(GL3.GL_VERTEX_SHADER);
        int fragmentShaderHandle = glContext.glCreateShader(GL3.GL_FRAGMENT_SHADER);

        //Uniform and attrib
        int mvpMatrixUniformHandle = glContext.glGetUniformLocation(shaderProgramID,"mvp");
        int vertexPosAttribHandle = glContext.glGetAttribLocation(shaderProgramID,"pos");



        String vertexShaderSource = "#version 150 core" + "\n" +
                "in  vec4 inVertex;" + "\n" +
                "in  vec3 inColor;" + "\n" +
                "uniform mat4 modelviewMatrix;" + "\n" +
                "uniform mat4 projectionMatrix;" + "\n" +
                "void main(void)" + "\n" +
                "{" + "\n" +
                "    gl_Position = " + "\n" +
                "        projectionMatrix * modelviewMatrix * inVertex;" + "\n" +
                "}";
        String fragmentShaderSource = "#version 150 core" + "\n" +
                "out vec4 outColor;" + "\n" +
                "void main(void)" + "\n" +
                "{" + "\n" +
                "    outColor = vec4(1.0,0.0,0.0,1.0);" + "\n" +
                "}";

        glContext.glShaderSource(vertexShaderHandle, 1, new String[]{vertexShaderSource}, null);
        glContext.glCompileShader(vertexShaderHandle);

        glContext.glShaderSource(fragmentShaderHandle, 1, new String[]{fragmentShaderSource}, null);
        glContext.glCompileShader(fragmentShaderHandle);

        glContext.glAttachShader(shaderProgramID, vertexShaderHandle);
        glContext.glAttachShader(shaderProgramID, fragmentShaderHandle);
        glContext.glLinkProgram(shaderProgramID);

        glContext.glUseProgram(shaderProgramID);

        setup(glContext, width, height);

        //Create buffers
        int[] iboBuffer = {0}; //Element array buffer
        int[] vboBuffer = {0};

        glContext.glGenBuffers(1,iboBuffer,0);
        glContext.glGenBuffers(1,vboBuffer,0);

        //Faces --> CCW (counter clock wise)
        short[] iboDataRaw = {0,1,2,2,1,3};
        float[] vboDataRaw = {-1, 1, -3,
                              -1,-1, -3,
                               1, 1, -3,
                               1,-1, -3};

        FloatBuffer vboData = Buffers.newDirectFloatBuffer(vboDataRaw);
        ShortBuffer iboData = Buffers.newDirectShortBuffer(iboDataRaw);

        glContext.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER,iboBuffer[0]);
        glContext.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER,iboDataRaw.length * Short.SIZE,iboData,GL3.GL_STATIC_DRAW);

        glContext.glBindBuffer(GL3.GL_ARRAY_BUFFER,vboBuffer[0]);
        glContext.glBufferData(GL3.GL_ARRAY_BUFFER,vboDataRaw.length * Float.SIZE, vboData,GL3.GL_STATIC_DRAW);

        //glContext.glVertexAttribPointer(vboBuffer,);
    }

    public static void setup( GL3 glContext, int width, int height ) {
        glContext.glViewport(0, 0, width, height);

        float aspect = (float) width / height;
        projectionMatrix = perspective(50, aspect, 0.1f, 100.0f);
    }

    public static void render( GL3 glContext, int width, int height ) {
        glContext.glClear( GL.GL_COLOR_BUFFER_BIT );
    }


    private static float[] perspective(
            float fovy, float aspect, float zNear, float zFar)
    {
        float radians = (float)Math.toRadians(fovy / 2);
        float deltaZ = zFar - zNear;
        float sine = (float)Math.sin(radians);
        if ((deltaZ == 0) || (sine == 0) || (aspect == 0))
        {
            return identity();
        }
        float cotangent = (float)Math.cos(radians) / sine;
        float m[] = identity();
        m[0*4+0] = cotangent / aspect;
        m[1*4+1] = cotangent;
        m[2*4+2] = -(zFar + zNear) / deltaZ;
        m[2*4+3] = -1;
        m[3*4+2] = -2 * zNear * zFar / deltaZ;
        m[3*4+3] = 0;
        return m;
    }

    /**
     * Creates an identity matrix
     *
     * @return An identity matrix
     */
    private static float[] identity()
    {
        float m[] = new float[16];
        Arrays.fill(m, 0);
        m[0] = m[5] = m[10] = m[15] = 1.0f;
        return m;
    }
}