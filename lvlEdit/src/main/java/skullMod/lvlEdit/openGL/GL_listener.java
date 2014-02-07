package skullMod.lvlEdit.openGL;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

public class GL_listener implements GLEventListener{
    private final GLCanvas canvas;

    public GL_listener(GLCanvas canvas) {
        this.canvas = canvas;
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 g = glAutoDrawable.getGL().getGL3();

    }
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }
    public void display(GLAutoDrawable glAutoDrawable) {
    }
public void reshape(GLAutoDrawable glAutoDrawable, int i, int i2, int i3, int i4) {
    }
}
