package skullMod.lvlEdit.dataStructures.SGA;

import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

public class SGA_File implements Serializable{
    public static final String SGA_KNOWN_VERISON = "3.0";

    public String fileFormatRevision;
    public float unknown; //FIXME missing
    public float animationLength; //In seconds

    public LinkedList<UnknownElement> unknownElements = new LinkedList<>();
    public LinkedList<UV_Track> uvTracks = new LinkedList<>();
    public SGA_File(DataInputStream dis) throws IOException {
        fileFormatRevision = Utility.readLongPascalString(dis);
        if(fileFormatRevision.equals(SGA_KNOWN_VERISON)){
            unknown = dis.readFloat();
            long nOfElements = dis.readLong();
            long nOfUVTracks = dis.readLong();
            animationLength = dis.readFloat();

            for(int i=0;i < nOfElements;i++){
                unknownElements.add(new UnknownElement(dis));
            }


            for(int i=0;i < nOfUVTracks;i++){
                uvTracks.add(new UV_Track(dis));
            }
        }else{
            throw new IllegalArgumentException("Given SGA file contains unknown version number");
        }
    }

    public void writeToStream(DataOutputStream dos) throws IOException{
        Utility.writeLongPascalString(dos, SGA_KNOWN_VERISON);

        dos.writeFloat(unknown);
        dos.writeLong(unknownElements.size());
        dos.writeLong(uvTracks.size());
        dos.writeFloat(animationLength);

        for(UnknownElement element : unknownElements){
            element.writeToStream(dos);
        }
        for(UV_Track uvTrack : uvTracks){
            uvTrack.writeToStream(dos);
        }
    }
}
