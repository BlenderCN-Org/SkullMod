package skullMod.lvlEdit.dataStructures;

import skullMod.lvlEdit.gui.modeChange.ModeChanger;


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
    public static ModeChanger modeList = new ModeChanger();

}
