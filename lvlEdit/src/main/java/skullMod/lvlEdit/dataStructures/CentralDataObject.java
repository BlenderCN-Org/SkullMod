package skullMod.lvlEdit.dataStructures;

import skullMod.lvlEdit.dataStructures.completeLevel.Level;
import skullMod.lvlEdit.gui.DDS_Panel;
import skullMod.lvlEdit.gui.modeChange.ModeChanger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;


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
    public static JPanel scenePanel;


    /**
     * All root nodes for the different JTrees
     */
    public final static DefaultMutableTreeNode sceneRoot = new DefaultMutableTreeNode("sceneRoot");
    public final static DefaultMutableTreeNode modelRoot = new DefaultMutableTreeNode("modelRoot");
    public final static DefaultMutableTreeNode animationRoot = new DefaultMutableTreeNode("animationRoot");
    public final static DefaultMutableTreeNode shapeRoot = new DefaultMutableTreeNode("shapeRoot");

    public final static JTree level = new JTree(new Level());

    //Are the JTrees required too?, yeah
    public final static JTree sceneTree = new JTree(sceneRoot);
    public final static JTree modelTree = new JTree(modelRoot);
    public final static JTree animationTree = new JTree(animationRoot);
    public final static JTree shapeTree = new JTree(shapeRoot);


    public final static DDS_Panel imageView = new DDS_Panel();


}
