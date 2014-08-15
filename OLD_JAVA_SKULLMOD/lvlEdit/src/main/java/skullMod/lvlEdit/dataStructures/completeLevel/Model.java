package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.Mat4;
import skullMod.lvlEdit.dataStructures.SGA.SGA_File;
import skullMod.lvlEdit.dataStructures.SGI.SGI_Element;
import skullMod.lvlEdit.dataStructures.SGM.SGM_File;
import skullMod.lvlEdit.dataStructures.SGS.Bone;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafContentNode;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.*;

public class Model extends NodeAdapter{
    public LeafContentNode<String> modelName;
    public LeafContentNode<String> fileName;
    public LeafContentNode<String> textureName;

    public LeafContentNode<Mat4> transformationMatrix;

    public LeafContentNode<ArrayList<Bone>> bones;

    //TODO unknown 2 bytes see SGI_Element

    public LeafContentNode<VertexData> modelData;


    public Model(TreeNode parent){
        super(parent);

        //TODO add other requirements
        this.modelName = new LeafContentNode<>(this, "Name", "defaultElement");
        this.fileName = new LeafContentNode<>(this, "File", "defaultName");
        this.textureName = new LeafContentNode<>(this, "Texture name", "defaultTexture");

        float[] tempMatrix = {1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};
        this.transformationMatrix = new LeafContentNode<>(this, "Transformation matrix", new Mat4(tempMatrix));

        this.modelData = new LeafContentNode<>(this, "Data", new VertexData());
    }

    public Model(TreeNode parent, SGI_Element modelMetadata, SGM_File model) {
        super(parent);

        this.modelName = new LeafContentNode<>(this, "Name", modelMetadata.elementName);
        this.fileName = new LeafContentNode<>(this, "File",modelMetadata.modelFileName);
        this.textureName = new LeafContentNode<>(this, "Texture name", model.textureName);

        this.transformationMatrix = new LeafContentNode<>(this, "Transformation matrix", modelMetadata.transformationMatrix);

        //TODO add model attributes
        modelData = new LeafContentNode<>(this,"Data",new VertexData(model.vertices,model.triangles,false));
    }


    public int getChildCount() {
        return 5;
    }

    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>(getChildCount());
        list.add(modelName);
        list.add(fileName);
        list.add(textureName);
        list.add(modelData);
        list.add(transformationMatrix);
        return Collections.enumeration(list);
    }

    public String toString(){
        return modelName.getContent();
    }

    public String getTextureFileName(){ return textureName.getContent(); }
}