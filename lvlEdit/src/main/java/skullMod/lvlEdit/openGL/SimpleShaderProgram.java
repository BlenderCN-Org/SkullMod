package skullMod.lvlEdit.openGL;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import java.io.File;
import java.util.HashMap;

import static skullMod.lvlEdit.openGL.Mini_GLUT.*;

//TODO enhancements make the shaders reusable for multiple programs if it makes sense
public class SimpleShaderProgram {
    public final String name;
    public final String vertexSourceCode, fragmentShaderSourceCode;
    public final ShaderAttributes attributes;
    public final ShaderUniforms uniforms;

    public final int shaderProgramID;

    private boolean deleted = false;

    public SimpleShaderProgram(String name, String vertexShaderSourceCode, String fragmentShaderSourceCode, String[] attributes, String[] uniforms, GL gl){
        //TODO check for null or invalid stuff

        this.name = name;
        this.vertexSourceCode = vertexShaderSourceCode;
        this.fragmentShaderSourceCode = fragmentShaderSourceCode;

        GL3 gl3 = gl.getGL3();

        //CREATE PROGRAMS
        this.shaderProgramID = gl3.glCreateProgram();
        int vertexShaderID = gl3.glCreateShader(GL3.GL_VERTEX_SHADER);
        int fragmentShaderID = gl3.glCreateShader(GL3.GL_FRAGMENT_SHADER);

        //TODO is this complexity required?
        //SOURCE FOR SHADERS
        String[] vertexShaderSourceCodeArray = new String[1];
        vertexShaderSourceCodeArray[0] = vertexShaderSourceCode;
        int[] vertexShaderSourceCodeLineLength = new int[1];
        vertexShaderSourceCodeLineLength[0] = vertexShaderSourceCode.length();

        String[] fragmentShaderSourceCodeArray = new String[1];
        fragmentShaderSourceCodeArray[0] = fragmentShaderSourceCode;
        int[] fragmentShaderSourceCodeLineLength = new int[1];
        fragmentShaderSourceCodeLineLength[0] = fragmentShaderSourceCode.length();

        //ADD SOURCE
        gl3.glShaderSource(vertexShaderID,1,vertexShaderSourceCodeArray,vertexShaderSourceCodeLineLength, 0);
        gl3.glShaderSource(fragmentShaderID,1,fragmentShaderSourceCodeArray,fragmentShaderSourceCodeLineLength, 0);

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

        //LINK SHADERS
        gl3.glLinkProgram(shaderProgramID);
        gl3.glValidateProgram(shaderProgramID);

        //CHECK LINKING STATUS FOR ERRORS
        String linkerErrorLog = checkGLShaderLinkError(gl3, shaderProgramID);
        if(linkerErrorLog == null){ throw new OGLException(linkerErrorLog); }

        //Not required for normal operations. Just for debugging.
        this.uniforms = new ShaderUniforms(gl3, shaderProgramID, uniforms);
        this.attributes = new ShaderAttributes(gl3, shaderProgramID, attributes);

        //Shader objects are not required except if they are reused for another program
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

    //TODO redo those classes, or remove them altogether
    public static class ShaderAttributes{
        public final HashMap<String, Integer> attributes;
        public ShaderAttributes(GL3 gl, int shaderProgramID, String[] shaderAttributes){
            this.attributes = new HashMap<>();
            for(int i = 0;i < shaderAttributes.length;i++){
                attributes.put(shaderAttributes[i], gl.glGetAttribLocation(shaderProgramID, shaderAttributes[i]));
            }
        }
    }
    public static class ShaderUniforms{
        private final HashMap<String,Integer> uniforms;
        public ShaderUniforms(GL3 gl, int shaderProgramID, String[] shaderUniforms){
            uniforms = new HashMap<>();
            for(int i  = 0;i < shaderUniforms.length;i++){
                uniforms.put(shaderUniforms[i], gl.glGetUniformLocation(shaderProgramID,shaderUniforms[i]));
            }
        }
    }

    public String toString(){
        return name;
    }

    public String getInfos(){
        String info = "";
        info += "Shadername: " + name + " with id " + shaderProgramID + File.separator;

        for(String key : attributes.attributes.keySet().toArray(new String[0])){
            info += "Attribute: " + key + " with location " + attributes.attributes.get(key) + "\n";
        }

        for(String key : uniforms.uniforms.keySet().toArray(new String[0])){
            info += "Uniform: " + key + " with location " + uniforms.uniforms.get(key) + "\n";
        }
        return info;
    }
}
