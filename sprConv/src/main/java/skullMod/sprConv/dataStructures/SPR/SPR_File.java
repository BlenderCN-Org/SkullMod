package skullMod.sprConv.dataStructures.SPR;


import skullMod.sprConv.utility.Utility;

import java.io.*;

public class SPR_File implements Serializable{
    public static final String knownFileFormatRevision = "2.0";

    public String fileFormatRevision;
    public String spriteSceneName; //TODO better idea for naming

    public int unknown1; //This is int for sure (see the character select sprites)

    public String dataFormatString;

    //Unsigned
    public long bytesPerEntry;
    public long nOfEntries;
    public long nOfFrames;
    public long nOfAnimations;
    public long blockWidth;  //in pixels
    public long blockHeight; //in pixels

    public SPR_Entry[] entries;
    public SPR_Frame[] frames;
    public SPR_Animation[] animations;

    public SPR_File(DataInputStream dis) throws IOException{
        fileFormatRevision = Utility.readLongPascalString(dis);

        if(!fileFormatRevision.equals(knownFileFormatRevision)){
            throw new IllegalArgumentException("File format revision does not match, stopped reading");
        }

        spriteSceneName = Utility.readLongPascalString(dis);
        unknown1 = dis.readInt();

        dataFormatString = Utility.readLongPascalString(dis);

        //Unsigned
        bytesPerEntry = dis.readLong();
        nOfEntries = dis.readLong();
        nOfFrames = dis.readLong();
        nOfAnimations = dis.readLong();
        blockWidth = dis.readLong();
        blockHeight = dis.readLong();

        //Init arrays for the following reads
        entries = new SPR_Entry[(int)nOfEntries];
        frames = new SPR_Frame[(int)nOfFrames];
        animations = new SPR_Animation[(int) nOfAnimations];

        for(int i = 0;i < nOfEntries;i++){
            entries[i] = new SPR_Entry(dis);
        }
        for(int i = 0;i < nOfFrames;i++){
            frames[i] = new SPR_Frame(dis);
        }
        for(int i = 0;i < nOfAnimations;i++){
            animations[i] = new SPR_Animation(dis);
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
        dos.writeLong(nOfAnimations);
        dos.writeLong(blockWidth);
        dos.writeLong(blockHeight);

        for(SPR_Entry entry : entries){
            entry.writeToStream(dos);
        }
        for(SPR_Frame frame : frames){
            frame.writeToStream(dos);
        }
        for(SPR_Animation animation : animations){
            animation.writeToStream(dos);
        }
    }
}
