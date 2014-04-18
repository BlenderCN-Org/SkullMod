package skullMod.lvlEdit.openGL;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Limitations
 *
 * Only ONE attribute buffer (interleaved)
 * One shader per VAO that can not be used again for other vaos
 * Normally two shaders would be enough for the entire scene (one for stuff without bones, one with them)
 */

/** Vertex Array Object only for the specific use cases for this tool */
public class Object3D {
    private final int NO_BUFFER = 0;

    public final int vaoID;
    public VBO vbo;
    public IBO ibo;

    public final boolean useBones;

    private boolean deleted = false;

    //TODO change vboColorData to iboData after triangle
    public Object3D(GL gl, boolean useBones, FloatBuffer vboData, FloatBuffer vboColorData, int shaderProgramID){
        this.useBones = useBones;

        GL3 gl3 = gl.getGL3();

        { //Generate VAO
            IntBuffer vaos = IntBuffer.allocate(1);
            gl3.glGenVertexArrays(1,vaos);

            this.vaoID = vaos.array()[0];
        }

        //Bind VAO, all states until unbinding the vao are "recorded" (vbo attributes as well) //TODO are they?
        //(the states are saved, no actual replication is done when binding it again)
        //Things that are not bound (non extensive list): shaders
        gl3.glBindVertexArray(this.vaoID);

        { //Generate VBO and IBO, only one gen buffers is required but for better readability this is done using two calls
            IntBuffer vbos = IntBuffer.allocate(1);
            IntBuffer ibos = IntBuffer.allocate(1);
            gl3.glGenBuffers(1,vbos);
            gl3.glGenBuffers(1,ibos);
            this.vbo = new VBO(vbos.array()[0]);
            this.ibo = new IBO(ibos.array()[0]);
        }

        //TODO remove
        int vertexLoc = gl3.glGetAttribLocation(shaderProgramID, "position");
        int colorLoc = gl3.glGetAttribLocation(shaderProgramID, "color");



        { //Bind and fill VBO
            gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo.id);
            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, 12 * Float.SIZE / 8, vboData, GL.GL_STATIC_DRAW);

            //TODO fixed code


            gl3.glEnableVertexAttribArray(vertexLoc);
            gl3.glVertexAttribPointer(vertexLoc, 4, GL3.GL_FLOAT, false,0,0);





            gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, ibo.id);
            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, 12 * Float.SIZE / 8, vboColorData, GL3.GL_STATIC_DRAW);

            gl3.glEnableVertexAttribArray(colorLoc);
            gl3.glVertexAttribPointer(colorLoc, 4, GL3.GL_FLOAT, false, 0,0);

        }

        /*
        { //Bind and fill IBO
            gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo.id);
            gl3.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, iboData.array().length * Float.SIZE, iboData, GL.GL_STATIC_DRAW);

            //TODO fixed code

        }
        */





        //Unbind VAO, VBO, IBO TODO is the VBO and IBO unbinding necessary?
        /*
        gl3.glBindVertexArray(NO_BUFFER);
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, NO_BUFFER);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, NO_BUFFER);
        */
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
