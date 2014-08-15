package skullMod.lvlEdit.dataStructures.SGM;

import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.utility.Utility;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

public class SGM_File implements Serializable{
    public String fileFormatRevision;
    public String textureName;
    public float[] fixedUnknown; //ALWAYS 52 bytes (13 * 4 byte) , a 3x4 matrix and *something* else would fit in there, why?
    public String dataFormatString;

    public long attributeLengthPerVertex;
    public long nOfVertices;
    public long nOfTriangles;
    public long nOfJoints;

    //Data for VBO (Vertex Buffer Object)
    public Vertex[] vertices;
    //Data for IBO (Index Buffer Object)
    public Triangle[] triangles;

    //The following part is guesswork, might be xyz pos + rotation or xyz pos*2 for something like a bounding box
    //The only thing certain is that they are floats, maybe
    public float xPos;
    public float yPos;
    public float zPos;

    public float xRot;
    public float yRot;
    public float zRot;

    public String[] jointNames;
    public Mat4[] jointMatrix;


    public SGM_File(DataInputStream dis) throws IOException {
        fileFormatRevision = Utility.readLongPascalString(dis);
        textureName = Utility.readLongPascalString(dis);

        byte[] fixedUknown = new byte[52];
        dis.read(fixedUknown);

        dataFormatString = Utility.readLongPascalString(dis);

        attributeLengthPerVertex = dis.readLong();
        nOfVertices = dis.readLong();
        nOfTriangles = dis.readLong();
        nOfJoints = dis.readLong();

        vertices = new Vertex[(int) nOfVertices];

        for(int i = 0;i < nOfVertices;i++){
            vertices[i] = new Vertex(dis, attributeLengthPerVertex, dataFormatString);
        }

        triangles = new Triangle[(int) nOfTriangles];

        for(int i = 0;i < nOfTriangles;i++){
            triangles[i] = new Triangle(dis);
        }

        xPos = dis.readFloat();
        yPos  = dis.readFloat();
        zPos = dis.readFloat();

        xRot = dis.readFloat();
        yRot  = dis.readFloat();
        zRot = dis.readFloat();

        jointNames = new String[(int) nOfJoints];

        for(int i = 0;i < nOfJoints;i++){
            jointNames[i] = Utility.readLongPascalString(dis);
        }

        jointMatrix = new Mat4[(int) nOfJoints];

        for(int i = 0;i < nOfJoints;i++){
            jointMatrix[i] = new Mat4(dis);
        }
    }

    //TODO add write to stream
}
