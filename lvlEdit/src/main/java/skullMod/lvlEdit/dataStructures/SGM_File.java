package skullMod.lvlEdit.dataStructures;

import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Skullgirls model file (.sgm.msb)
 *
 * Javas signed data types are a bother and need additional security checks TODO implement them
 */
public class SGM_File {
    public String fileFormatRevision;
    public String textureName;
    public float unknown1[]; //13 entries
    public String dataFormat;

    public long bytesPerPolygon;
    public long nOfPolygons;
    public long nOfTriangles;
    public long nOfJoints;

    public byte polygonData[]; //Length = nOfPolygons * bytesPerPolygon
    public short triangleData[]; //Length = nofTriangles*3
    public float maybeBoundingBox[]; //Length = 6 floats = 6*4 = 24 bytes

    public String jointNames[]; //nOfJoints Strings
    public byte jointProperties[]; //nOfJoints*16

    public SGM_File(DataInputStream dis) throws IOException {
        fileFormatRevision = Utility.readLongPascalString(dis);
        textureName = Utility.readLongPascalString(dis);
        unknown1 = Utility.readFloatArray(dis,new float[13]); //13 entries
        dataFormat = Utility.readLongPascalString(dis);

        bytesPerPolygon = dis.readLong();
        nOfPolygons = dis.readLong();
        nOfTriangles = dis.readLong();
        nOfJoints = dis.readLong();

        polygonData = Utility.readByteArray(dis,new byte[(int) (nOfPolygons*bytesPerPolygon)]);
        triangleData = Utility.readShortArray(dis,new short[(int) (nOfTriangles*3)]);
        maybeBoundingBox = Utility.readFloatArray(dis,new float[6]); //Length = 6 floats = 6*4 = 24 bytes

        jointNames = Utility.readLongPascalStringArray(dis,new String[(int) nOfJoints]);
        jointProperties = Utility.readByteArray(dis,new byte[(int) (nOfJoints*16)]);
    }

    public SGM_File(String fileFormatRevision, String textureName, float unknown1[], String dataFormat,
                    long bytesPerPolygon, long nOfPolygons, long nOfTriangles, long nOfJoints,
                    byte polygonData[], short triangleData[], float maybeBoundingBox[],
                    String jointNames[], byte jointProperties[]){

        //TODO check incoming data

        this.fileFormatRevision = fileFormatRevision;
        this.textureName = textureName;
        this.unknown1 = unknown1;
        this.dataFormat = dataFormat;

        this.bytesPerPolygon = bytesPerPolygon;
        this.nOfPolygons = nOfPolygons;
        this.nOfTriangles = nOfTriangles;
        this.nOfJoints = nOfJoints;

        this.polygonData = polygonData;
        this.triangleData = triangleData;
        this.maybeBoundingBox = maybeBoundingBox;
        this.jointNames = jointNames;
        this.jointProperties = jointProperties;
    }

    public String toString(){
        return "Fileformat revision: " + fileFormatRevision +
                "\nTexture name: " + textureName +
                "\nData format: " + dataFormat +
                "\nBytes per polygon: " + bytesPerPolygon +
                "\nNumber of polygons: " + nOfPolygons +
                "\nNumber of triangles: " + nOfTriangles +
                "\nNumber of joints: " + nOfJoints;
    }

    //TODO check additional methods and data (like relative texture etc)
}
