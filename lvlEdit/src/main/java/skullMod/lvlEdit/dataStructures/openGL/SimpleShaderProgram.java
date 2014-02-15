package skullMod.lvlEdit.dataStructures.openGL;

import skullMod.lvlEdit.openGL.Mini_GLUT;
import skullMod.lvlEdit.openGL.OGLException;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import static skullMod.lvlEdit.openGL.Mini_GLUT.*;

//TODO enhancements make the shaders reusable for multiple programs if it makes sense
public class SimpleShaderProgram {
    public final String name;
    public final String vertexSourceCode, fragmentShaderSourceCode;

    public final int shaderProgramID;

    private boolean deleted = false;

    private final int DEFAULT_FRAGMENT_OUTPUT_INDEX = 0;

    public SimpleShaderProgram(String name, String vertexShaderSourceCode, String fragmentShaderSourceCode, GL gl){
        //TODO check for null or invalid stuff

        this.name = name;
        this.vertexSourceCode = vertexShaderSourceCode;
        this.fragmentShaderSourceCode = fragmentShaderSourceCode;

        GL3 gl3 = gl.getGL3();

        //CREATE PROGRAMS
        this.shaderProgramID = gl3.glCreateProgram();
        int vertexShaderID = gl3.glCreateShader(GL3.GL_VERTEX_SHADER);
        int fragmentShaderID = gl3.glCreateShader(GL3.GL_FRAGMENT_SHADER);

        //ADD SOURCE FOR SHADERS
        gl3.glShaderSource(vertexShaderID,1, new String[] { vertexShaderSourceCode }, null);
        gl3.glShaderSource(fragmentShaderID,1, new String[] { fragmentShaderSourceCode } , null);

        //COMPILE AND CHECK
        gl3.glCompileShader(vertexShaderID);
        String vertexShaderErrorLog = checkGLShaderCompileError(gl,vertexShaderID);
        if(vertexShaderErrorLog != null){ throw new OGLException(vertexShaderErrorLog); }

        gl3.glCompileShader(fragmentShaderID);
        String fragmentShaderErrorLog = checkGLShaderCompileError(gl, fragmentShaderID);
        if(fragmentShaderErrorLog != null){ throw new OGLException(fragmentShaderErrorLog); }

        //ATTACH SHADERS
        gl3.glAttachShader(shaderProgramID, vertexShaderID);
        gl3.glAttachShader(shaderProgramID, fragmentShaderID);

        //TODO make this a param?
        //gl3.glBindFragDataLocation(shaderProgramID, DEFAULT_FRAGMENT_OUTPUT_INDEX, "outputF");

        //LINK SHADERS
        gl3.glLinkProgram(shaderProgramID);
        gl3.glValidateProgram(shaderProgramID);

        //CHECK LINKING STATUS FOR ERRORS
        String linkerErrorLog = checkGLShaderLinkError(gl3, shaderProgramID);
        if(linkerErrorLog != null){ throw new OGLException(linkerErrorLog); }


        //Shader objects are not required except if they are reused for another program so they can be removed from the shader
        gl3.glDetachShader(shaderProgramID, vertexShaderID);
        gl3.glDetachShader(shaderProgramID, fragmentShaderID);

        //Delete shaders, they are only deleted when they are NOT attached to ANY program
        gl3.glDeleteShader(vertexShaderID);
        gl3.glDeleteShader(fragmentShaderID);

    }

    public void enableShader(GL3 gl, boolean enable){
        if(deleted){ throw new IllegalStateException("Shader is deleted"); }
        if(enable){
            gl.glUseProgram(shaderProgramID);
        }else{
            gl.glUseProgram(0);
        }
    }

    public void deleteShader(GL3 gl){
        if(deleted){ throw new IllegalStateException("Shader already deleted"); }
        gl.getGL3().glDeleteProgram(shaderProgramID);
        checkGlError(gl);
    }

    public String toString(){
        return name;
    }

    public String getInfos(GL3 gl3){
        String info = "";
        info += "Shadername: " + name + "\n";
        info += Mini_GLUT.getAttributesInfo(gl3, shaderProgramID);
        info += Mini_GLUT.getUniformsInfo(gl3, shaderProgramID);
        return info;
    }
}
