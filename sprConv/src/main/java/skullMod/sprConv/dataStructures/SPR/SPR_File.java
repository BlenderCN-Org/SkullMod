package skullMod.sprConv.dataStructures.SPR;


import java.io.Serializable;

public class SPR_File implements Serializable{
    public static final String knownFileFormatRevision = "2.0";

    String fileFormatRevision;
    String spriteSceneName; //TODO better idea for naming

    float unknown1;

    String dataFormatString;

    //Unsigned
    long bytesPerEntry;
    long nOfEntries;
    long nOfFrames;
    long nOfSprites;
    long blockWidth;  //in pixels
    long blockHeight; //in pixels

    SPR_Entry[] entries;
    SPR_Frame[] frames;
    SPR_SpriteInfo[] spriteInfos;
}
