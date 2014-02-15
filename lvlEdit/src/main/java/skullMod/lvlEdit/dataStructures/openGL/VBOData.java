package skullMod.lvlEdit.dataStructures.openGL;


import javax.media.opengl.GL3;

import static javax.media.opengl.GL3.*;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 * Data required for setting up a vbo
 * glBufferData, glEnableVertexAttribArray and glVertexAttribPointer
 */
public class VBOData {
    public final Buffer data;
    public final List<VertexAttribute> attributes;
    public final IBOData iboData;
    public final int sizeInBytes;
    public final int nOfElements;

    public final int glDrawType;

    private int vbo;
    private int ibo;

    public VBOData(Buffer data, List<VertexAttribute> attributes, IBOData iboData, int sizeInBytes, int nOfElements, int glDrawType){
        this.data = data;
        this.attributes = attributes;
        this.iboData = iboData;
        this.sizeInBytes = sizeInBytes;
        this.nOfElements = nOfElements;
        this.glDrawType = glDrawType;
    }

    public static class VertexAttribute{
        public final int index,stride,offset,gl_type,nOfElements;
        public final boolean normalize;

        public VertexAttribute(int index, int stride, int offset, int gl_type, int nOfElements){
            this(index,stride,offset,gl_type,nOfElements,false);
        }

        //index is location
        public VertexAttribute(int index, int stride, int offset, int gl_type, int nOfElements, boolean normalize){
            if(stride < 0){ throw new IllegalArgumentException("Stride is less than 0"); }
            if(offset < 0){ throw new IllegalArgumentException("Offset is less than 0"); }

            if(!checkGL_type(gl_type)){ throw new IllegalArgumentException("Invalid type"); }

            this.index = index;
            this.stride = stride;
            this.offset = offset;
            this.gl_type = gl_type;
            this.nOfElements = nOfElements;
            this.normalize = normalize;
        }
    }

    public int getVBO(){ return vbo; }
    public int getIBO(){ return ibo; }
    public boolean hasIBO(){
        if(ibo == -1){
            return false;
        }else{
            return true;
        }
    }

    /**
     * @param gl3 OpenGL context
     * @return
     */
    public void setup(GL3 gl3){
        {   //Setup vbo
            IntBuffer buffer = IntBuffer.allocate(1);
            gl3.glGenBuffers(1, buffer);
            vbo = buffer.get(0);
            gl3.glBindBuffer(GL_ARRAY_BUFFER, vbo);
            gl3.glBufferData(GL_ARRAY_BUFFER, sizeInBytes, data, GL_STATIC_DRAW);
        }

        if(iboData == null){
            ibo = -1;
        }else{
            IntBuffer buffer = IntBuffer.allocate(1);
            gl3.glGenBuffers(1, buffer);

            ibo = buffer.get(0);
            gl3.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            gl3.glBufferData(GL_ELEMENT_ARRAY_BUFFER, iboData.sizeInBytes, iboData.data, GL_STATIC_DRAW);
        }

        for(VertexAttribute attribute : attributes){
            gl3.glEnableVertexAttribArray(attribute.index);
            gl3.glVertexAttribPointer(attribute.index, attribute.nOfElements, attribute.gl_type, attribute.normalize, attribute.stride, attribute.offset);
        }
    }

    private static boolean checkGL_type(int gl_type){
        switch(gl_type){
            case GL_FLOAT:
            case GL_BYTE:
            case GL_UNSIGNED_BYTE:
            case GL_SHORT:
            case GL_UNSIGNED_SHORT:
            case GL_INT:
            case GL_UNSIGNED_INT:
            case GL_HALF_FLOAT:
                return true;
            default:
                return false;
        }
    }
}
