package skullMod.lvlEdit.dataStructures;

import skullMod.lvlEdit.gui.modeChange.ModeChanger;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;


/**
 * Contains all data for this application
 * All attributes have synchronized getters and setters to prevent multithreading issues
 *
 * This might not be the correct Java way but it works
 */
public final class CentralDataObject {
    private CentralDataObject(){} //Prevent object creation, this object has static methods / objects only

    /**
     * Attach itemListener for mode change, use static import on enum
     * import static skullMod.lvlEdit.gui.modeChange.ModeChanger.Modes.*;
     */
    public static final ModeChanger modeList = new ModeChanger();


    public static JScrollPane modelPanel;
    public static JScrollPane animationPanel;
    public static GLCanvas scenePanel;

}
