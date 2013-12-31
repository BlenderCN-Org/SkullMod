package skullMod.lvlEdit.dataStructures.SGM;

import java.io.Serializable;

public class SGM_File implements Serializable{
    public String file_format_revision;
    public String textureName;
    public float[] fixedUnknown; //ALWAYS 52 bytes (13 * 4 byte) , a 3x4 matrix and *something* else would fit in there, why?
    public String attributeString;

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
    public JointProperty[] jointProperties;
}
