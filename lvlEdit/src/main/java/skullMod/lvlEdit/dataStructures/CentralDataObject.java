package skullMod.lvlEdit.dataStructures;

import skullMod.lvlEdit.dataStructures.completeLevel.Level;
import skullMod.lvlEdit.dataStructures.completeLevel.SkullmodJTree;
import skullMod.lvlEdit.gui.AnimationPanel;
import skullMod.lvlEdit.gui.DDS_Panel;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

/**
 * Contains all data for this application
 * All attributes (should) have synchronized getters and setters to prevent multithreading issues
 *
 * This might not be the correct Java way but it works
 */
public final class CentralDataObject {
    private CentralDataObject(){} //Prevent object creation, this object has static methods / objects only

    /**
     * Attach itemListener for mode change, use static import on enum
     * import static skullMod.lvlEdit.gui.modeChange.ModeChanger.Modes.*;
     */
    public static DDS_Panel ddsPanel = new DDS_Panel();
    public static JScrollPane animationPanel = new JScrollPane(new AnimationPanel());
    public static GLCanvas scenePanel;

    public static final JTree level = new SkullmodJTree(new Level());
}
