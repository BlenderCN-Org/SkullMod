package skullMod.lvlEdit.dataStructures.openGL;


import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import skullMod.lvlEdit.dataStructures.CentralDataObject;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

public class OpenGL_Frame extends GLCanvas {
    public final OpenGL_Listener listener;
    public static OpenGL_Frame getNewFrame(){
        OpenGL_Listener listener = new OpenGL_Listener();


        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glp);
        OpenGL_Frame result = new OpenGL_Frame(glCapabilities, listener);

        result.addGLEventListener(listener);
        result.addKeyListener(listener);
        result.addMouseListener(listener);
        result.addMouseMotionListener(listener);

        FPSAnimator animator = new FPSAnimator(result, 30);
        animator.start();
        //FIXME stop animator

        return result;
    }

    private OpenGL_Frame(GLCapabilities glCapabilities, OpenGL_Listener listener){
        super(glCapabilities);
        this.listener = listener;
    }
}
