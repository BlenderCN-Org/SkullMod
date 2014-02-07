package skullMod.lvlEdit.openGL;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: netbook
 * Date: 2/7/14
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestFrame extends JFrame{
    //FIXME currently a GL3 context is requested, find a "softer" way to get the desired context

    public TestFrame(){
        super("OpenGL test");
        initGL();
    }

    private void initGL() {
        GLProfile glprofile = GLProfile.get(GLProfile.GL3);  //TODO too hard, use softer way
        GLCapabilities glcapabilities = new GLCapabilities( glprofile );
        GLCanvas canvas = new GLCanvas( glcapabilities );
        canvas.setSize(300,300);


        canvas.addGLEventListener(new GL_listener(canvas));
    }
}
