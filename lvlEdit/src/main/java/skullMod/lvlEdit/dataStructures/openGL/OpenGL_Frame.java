package skullMod.lvlEdit.dataStructures.openGL;


import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;
import skullMod.lvlEdit.dataStructures.CentralDataObject;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class OpenGL_Frame extends GLCanvas {
    public final OpenGL_Listener listener;

    private final FPSAnimator animator;

    public static OpenGL_Frame getNewPanel(JFrame frame){
        OpenGL_Listener listener = new OpenGL_Listener();


        GLProfile glp = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glp);
        OpenGL_Frame result = new OpenGL_Frame(glCapabilities, listener);

        result.addGLEventListener(listener);
        result.addKeyListener(listener);
        result.addMouseListener(listener);
        result.addMouseMotionListener(listener);

        result.startAnimator();

        frame.addWindowListener(new StopAnimatorWindowListener(result.getAnimator()));

        return result;
    }

    private static class StopAnimatorWindowListener extends WindowAdapter{
        private final FPSAnimator animator;
        public StopAnimatorWindowListener(FPSAnimator animator){
            this.animator = animator;
        }

        public void windowClosing(WindowEvent e){
            this.animator.stop();
        }
    }


    public void startAnimator(){
        this.animator.start();
    }

    public void stopAnimator(){
        this.animator.stop();
    }

    public FPSAnimator getAnimator(){
        return animator;
    }

    public void setRefreshRate(boolean high){
        if(high){
            this.animator.setFPS(1);
        }else{
            this.animator.setFPS(30);
        }
    }

    private OpenGL_Frame(GLCapabilities glCapabilities, OpenGL_Listener listener){
        super(glCapabilities);
        this.listener = listener;

        this.animator = new FPSAnimator(this, 30);
    }
}
