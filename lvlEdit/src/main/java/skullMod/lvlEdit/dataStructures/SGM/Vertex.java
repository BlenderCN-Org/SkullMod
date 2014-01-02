package skullMod.lvlEdit.dataStructures.SGM;

import java.io.Serializable;

public class Vertex implements Serializable{
    public Position position;
    public Normals normals;
    public UV uv;
    public Color c; //uchar4 * 4 = RGBA (or BGRA, have to see what direct x prefers)

    //The following part is optional, it will exist if any joints are referenced.
    //Weights used for vertex skinning: http://tech-artists.org/wiki/Vertex_Skinning
    //More info: http://http.developer.nvidia.com/CgTutorial/cg_tutorial_chapter06.html
    //These are set to null if they are not needed, FIXME add fitting method to add them later on
    //The bone matrix array should be supplied programatically
    //TODO GUESS: other joint stuff is supplied within attached sgs file
    public BlendIndices blendIndices;
    public BlendWeights blendWeights;
}
