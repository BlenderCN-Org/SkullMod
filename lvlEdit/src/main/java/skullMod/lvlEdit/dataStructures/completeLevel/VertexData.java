package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.SGM.Triangle;
import skullMod.lvlEdit.dataStructures.SGM.Vertex;

public class VertexData{
    public VertexData(Vertex[] vertices, Triangle[] triangles, boolean hasBones){
        this.hasBones = hasBones;

        int nOfVertices = vertices.length;

        vertexData = new float[nOfVertices*3];

        int currentVertex = 0;
        for(Vertex vertex : vertices){
            vertexData[currentVertex * 3] = vertex.position.xPos;
            vertexData[currentVertex * 3 + 1] = vertex.position.yPos;
            vertexData[currentVertex * 3 + 2] = vertex.position.zPos;
            currentVertex++;
        }

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

    boolean hasBones;
    float[] vertexData;
    short[] iboData;
    public String toString(){
        return "Vertices: " + (vertexData.length/3)  + " Triangles: " + (iboData.length/3);
    }
}
