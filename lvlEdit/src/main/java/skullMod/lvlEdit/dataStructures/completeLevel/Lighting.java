package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafAdapter;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class Lighting extends NodeAdapter{
    private final AmbientLight ambientLight;
    private final DirectionalLight[] directionalLights;
    private final PointLight[] pointLights;

    public Lighting(TreeNode parent){
        super(parent);
        this.ambientLight = new AmbientLight(255,255,255);
        this.directionalLights = new DirectionalLight[4];
        this.pointLights = new PointLight[4];
    }

    public Lighting(TreeNode parent, AmbientLight ambientLight, DirectionalLight[] directionalLights, PointLight[] pointLights){
         super(parent);
        //TODO verify
        this.ambientLight = ambientLight;
        this.directionalLights = directionalLights;
        this.pointLights = pointLights;
    }

    public class AmbientLight extends LeafAdapter{
        //RGB can be BIGGER than 256, be smart don't go overboard
        private int r,g,b;

        public AmbientLight(int r, int g, int b){
            super(Lighting.this);
            setColor(r,g,b);
        }

        public void setColor(int r, int g, int b){
            //TODO VERFIY
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public String toString(){
            return "Ambient light";
        }
    }

    public class DirectionalLight extends LeafAdapter {
        private int r,g,b;
        private float x,y,z; //NORMALIZED

        public DirectionalLight(int r, int g, int b, float x, float y, float z){
            super(Lighting.this);
            //TODO verify
            this.r = r;
            this.g = g;
            this.b = b;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public String toString(){
            return "Directional light";
        }
    }

    public class PointLight extends LeafAdapter{
        private int r,g,b;
        private float x,y,z; //absolute coordiantes
        private int lightRadiusInPx; //BE AWARE: this is for HD ready resolution only
        private boolean neverCull;

        public PointLight(int r, int g, int b, float x, float y, float z, int lightRadiusInPx, boolean neverCull){
            super(Lighting.this);
            //TODO verify
            this.r = r;
            this.g = g;
            this.b = b;
            this.x = x;
            this.y = y;
            this.z = z;
            this.lightRadiusInPx = lightRadiusInPx;
            this.neverCull = neverCull;
        }

        public String toString(){
            return "Point light";
        }
    }

    public TreeNode getChildAt(int childIndex) {
        int nOfDirectionalLights = getNumberOfNonNullElements(directionalLights);

        if(childIndex == 0){ return ambientLight; }
        if(nOfDirectionalLights > 0 && childIndex < nOfDirectionalLights+1){
            return directionalLights[childIndex-1];
        }else{
            return pointLights[childIndex-1 + nOfDirectionalLights];
        }
    }

    public int getChildCount() {
        int nOfDirectionalLights = getNumberOfNonNullElements(directionalLights);
        int nOfPointLights = getNumberOfNonNullElements(pointLights);

        return 1 + nOfDirectionalLights + nOfPointLights; //Ambient + directional + point
    }

    public int getIndex(TreeNode node) {
        if(node instanceof AmbientLight){ return 0; }
        if(node instanceof DirectionalLight){
            return getElementPosition(directionalLights, node)+1;
        }
        if(node instanceof PointLight){
            return getElementPosition(pointLights, node)+1+getNumberOfNonNullElements(directionalLights);
        }
        return -1;
    }

    public Enumeration children() {
        ArrayList<TreeNode> list = new ArrayList<>();
        list.add(ambientLight);
        for(DirectionalLight directionalLight : directionalLights){
            if(directionalLight != null){ list.add(directionalLight); }
        }
        for(PointLight pointLight : pointLights){
            if(pointLight != null){ list.add(pointLight); }
        }
        return Collections.enumeration(list);
    }
    //TODO find a better way
    private static int getNumberOfNonNullElements(Object[] c){
        int i = 0;
        for(Object o : c){
            if(o != null){ i++; }
        }
        return i;
    }
    //TODO find a better way
    private static int getElementPosition(Object[] a, Object b){
        for(int i = 0;i < a.length;i++){
            if(a[i] == b){ return i; }
        }
        return -1;
    }

    public String toString(){
        return "Lights";
    }
}
