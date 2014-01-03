package skullMod.sprConv.dataStructures.SPR;


import skullMod.sprConv.utility.Utility;

import java.io.*;

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

    public SPR_File(DataInputStream dis) throws IOException{
        fileFormatRevision = Utility.readLongPascalString(dis);
        spriteSceneName = Utility.readLongPascalString(dis);
        unknown1 = dis.readFloat();

        dataFormatString = Utility.readLongPascalString(dis);

        //Unsigned
        bytesPerEntry = dis.readLong();
        nOfEntries = dis.readLong();
        nOfFrames = dis.readLong();
        nOfSprites = dis.readLong();
        blockWidth = dis.readLong();
        blockHeight = dis.readLong();

        //Init arrays for the following reads
        entries = new SPR_Entry[(int)nOfEntries];
        frames = new SPR_Frame[(int)nOfFrames];
        spriteInfos = new SPR_SpriteInfo[(int)nOfSprites];

        for(int i = 0;i < nOfEntries;i++){
            entries[i] = new SPR_Entry(dis);
        }
        for(int i = 0;i < nOfFrames;i++){
            frames[i] = new SPR_Frame(dis);
        }
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, fileFormatRevision);
        Utility.writeLongPascalString(dos, spriteSceneName);
        dos.writeFloat(unknown1);

        Utility.writeLongPascalString(dos, dataFormatString);

        dos.writeLong(bytesPerEntry);
        dos.writeLong(nOfEntries);
        dos.writeLong(nOfFrames);
        dos.writeLong(nOfSprites);
        dos.writeLong(blockWidth);
        dos.writeLong(blockHeight);

        for(SPR_Entry entry : entries){
            entry.writeToStream(dos);
        }
        for(SPR_Frame frame : frames){
            frame.writeToStream(dos);
        }
        for(SPR_SpriteInfo spriteInfo : spriteInfos){
            spriteInfo.writeToStream(dos);
        }
    }
}
