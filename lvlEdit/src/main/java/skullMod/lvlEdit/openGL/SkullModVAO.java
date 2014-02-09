package skullMod.lvlEdit.openGL;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Limitations
 *
 * Only ONE attribute buffer (interleaved)
 * One shader per VAO that can not be used again for other vaos
 * Normally two shaders would be enough for the entire scene (one for stuff without bones, one with them)
 */

/** Vertex Array Object only for the specific use cases for this tool */
public class SkullModVAO {
    private final int NO_BUFFER = 0;

    public final int vaoID;
    public VBO vbo;
    public IBO ibo;

    public final SimpleShaderProgram shaderProgram;

    public final boolean useBones;

    private boolean deleted = false;

    public SkullModVAO(GL gl, boolean useBones, FloatBuffer vboData, ShortBuffer iboData){
        this.useBones = useBones;

        GL3 gl3 = gl.getGL3();

        { //Create Shader
            String vertexShaderSource = "attribute vec3 pos;\n" +
                    "uniform mat4 p;\n" +
                    "void main(){\n" +
                    " gl_Position = p * vec4(pos, 1.0);\n" +
                    "}";
            String fragmentShaderSource = "#version 150\n out vec4 fragmentColor;" +
                    "void main(){\n" +
                    " fragmentColor = vec4(1.0,0.0,0.0,0.5);\n" +
                    "}";



            String[] attributes = new String[1];
            attributes[0] = "pos";

            String[] uniforms = new String[1];
            uniforms[0] = "p";

            shaderProgram = new SimpleShaderProgram("SAMPLE SHADER", vertexShaderSource, fragmentShaderSource,attributes, uniforms,gl);
        }


        { //Generate VAO
            IntBuffer vaos = IntBuffer.allocate(1);
            gl3.glGenVertexArrays(1,vaos);

            this.vaoID = vaos.array()[0];
        }

        { //Generate VBO and IBO, only one gen buffers is required but for better readability this is done using two calls
            IntBuffer vbos = IntBuffer.allocate(1);
            IntBuffer ibos = IntBuffer.allocate(1);
            gl3.glGenBuffers(1,vbos);
            gl3.glGenBuffers(1,ibos);
            this.vbo = new VBO(vbos.array()[0]);
            this.ibo = new IBO(ibos.array()[0]);
        }

        { //Bind and fill VBO
            gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER,vbo.id);
            //Get data from outside source (the parameters of this constructor)
            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, vboData.array().length * Float.SIZE, vboData, GL.GL_STATIC_DRAW);
        }

        { //Bind and fill IBO
            gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER,ibo.id);
            gl3.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, iboData.array().length * Short.SIZE, iboData, GL.GL_STATIC_DRAW);
        }

        //Bind VAO, all states until unbinding the vao are "recorded" (vbo attributes as well) //TODO are they?
        //(the states are saved, no actual replication is done when binding it again)
        //Things that are not bound (non extensive list): shaders
        //TODO is binding the buffers again (see above binding) required?
        gl3.glBindVertexArray(vaoID);

        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER,vbo.id);
        //TODO oh why, this seems chaotic how to bind directly to the attribute instead of 0
        gl3.glEnableVertexAttribArray(0);
        gl3.glVertexAttribPointer(0,3,GL3.GL_FLOAT,false,Float.SIZE*3,0);

        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo.id);

        //Unbind VAO, VBO, IBO TODO is the VBO and IBO unbinding necessary?
        gl3.glBindVertexArray(NO_BUFFER);
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, NO_BUFFER);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, NO_BUFFER);
    }

    public void delete(GL gl){
        GL3 gl3 = gl.getGL3();

        //TODO make VBOs and IBOs deleteable with their own deleted flags

        //Unbind all buffers (only unbound buffers can be deleted without them lingering around) //TODO is this correct?
        gl3.glBindVertexArray(NO_BUFFER);
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, NO_BUFFER);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, NO_BUFFER);

        { //Delete VBO
            IntBuffer vboIDs = IntBuffer.allocate(1);
            vboIDs.put(vbo.id);
            gl3.glDeleteBuffers(1,vboIDs);
        }

        { //Delete IBO
            IntBuffer iboIDs = IntBuffer.allocate(1);
            iboIDs.put(ibo.id);
            gl3.glDeleteBuffers(1,iboIDs);
        }

        this.deleted = true;
    }

    public String toString(){
        return "VAO id: " + vaoID + " VBO id: " + vbo.id + " IBO id: " + ibo.id + " Deleted?: " + deleted;
    }
}
