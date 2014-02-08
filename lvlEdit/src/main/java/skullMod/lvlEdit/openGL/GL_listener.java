package skullMod.lvlEdit.openGL;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class GL_listener implements GLEventListener{
    public void init(GLAutoDrawable glAutoDrawable) {
        GL3 g = glAutoDrawable.getGL().getGL3();
        g.glClearColor(0.0f,1.0f,0.0f,0.0f);
        g.glEnable(GL3.GL_DEPTH_TEST); //TODO is this required?
    }
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }
    public void display(GLAutoDrawable glAutoDrawable) {
        OneTriangle.render(glAutoDrawable.getGL().getGL3(),glAutoDrawable.getWidth(),glAutoDrawable.getHeight());
    }
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        OneTriangle.setup(glAutoDrawable.getGL().getGL3(), width, height);
    }
}
