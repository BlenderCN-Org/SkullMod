package skullMod.lvlEdit.dataStructures;

import java.io.Serializable;

public class SGI_Element implements Serializable {

    public String elementName;
    public String shapeName;

    public char[] unknown; //66 bytes

    public SGI_Animation[] animations;
}
