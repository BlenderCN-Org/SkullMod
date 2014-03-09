package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.SGM.Triangle;
import skullMod.lvlEdit.dataStructures.SGM.Vertex;

public class VertexData{
    public final int DEFAULT_STRIDE = 36;
    public final int BONE_STRIDE = 44;

    public int stride;

    boolean hasBones;
    public Vertex[] vertexData;
    public int bytesPerEntry;
    public short[] iboData;

    /**
     * Default triangle
     * TODO change to modern vertex data
     */
    public VertexData(){
        this.vertexData = new Vertex[1];
        vertexData[0] = new Vertex();
        this.iboData = new short[]{0, 1, 2};
        this.bytesPerEntry = 3*4;
        this.hasBones = false;

        stride = DEFAULT_STRIDE;
    }

    public VertexData(Vertex[] vertices, Triangle[] triangles, boolean hasBones){
        this.hasBones = hasBones;
        if(hasBones){
            stride = BONE_STRIDE;
        }else{
            stride = DEFAULT_STRIDE;
        }




        //vertexData = new float[nOfVertices*3];
        vertexData = vertices;



        int nOfTriangles = triangles.length * 3;
        iboData = new short[nOfTriangles];

        int currentTriangle = 0;
        for(Triangle triangle : triangles){
            iboData[currentTriangle * 3] = triangle.vertexIndex1;
            iboData[currentTriangle * 3 + 1] = triangle.vertexIndex2;
            iboData[currentTriangle * 3 + 2] = triangle.vertexIndex3;
            currentTriangle++;
        }
    }

    public String toString(){
        return "Vertices: " + (vertexData.length/3)  + " Triangles: " + (iboData.length/3);
    }
}
