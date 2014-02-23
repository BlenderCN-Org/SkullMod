package skullMod.lvlEdit.dataStructures.completeLevel;

import skullMod.lvlEdit.dataStructures.LVL.LVL_File;
import skullMod.lvlEdit.dataStructures.LVL.LVL_Light;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafAdapter;
import skullMod.lvlEdit.dataStructures.jTreeNodes.LeafContentNode;
import skullMod.lvlEdit.dataStructures.jTreeNodes.NodeAdapter;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import static skullMod.lvlEdit.dataStructures.LVL.LVL_Light.LightType.AMBIENT;

public class Lighting extends NodeAdapter{
    public final LeafContentNode<AmbientLight> ambientLight;
    //FIXME make proper nodes or a good selector
    public final LeafContentNode<DirectionalLight[]> directionalLights;
    public final LeafContentNode<PointLight[]> pointLights;

    public Lighting(TreeNode parent){
        super(parent);
        this.ambientLight = new LeafContentNode<>(this,"Ambient light", new AmbientLight(255,255,255));
        //TODO arrays are no leafs
        this.directionalLights = new LeafContentNode<>(this,"Directional lights", new DirectionalLight[4]);
        this.pointLights = new LeafContentNode<>(this,"Point lights", new PointLight[4]);
    }

    public Lighting(TreeNode parent, LVL_File lvl){
        super(parent);

        AmbientLight ambientLight = null;
        ArrayList<DirectionalLight> directionalLights = new ArrayList<>();
        ArrayList<PointLight> pointLights = new ArrayList<>();

        for(LVL_Light light : lvl.lights){
            //TODO is float for rgb a good choice in LVL_Light?
            switch(light.type){
                case AMBIENT:
                    ambientLight = new AmbientLight((int)light.r, (int)light.g, (int)light.b);
                    break;
                case DIRECTIONAL:
                    directionalLights.add(new DirectionalLight((int) light.r, (int) light.g, (int) light.b, light.x, light.y, light.z));
                    break;
                case POINT:
                    pointLights.add(new PointLight((int) light.r, (int) light.g, (int) light.b, light.x, light.y, light.z, light.pointLightRadiusInPx, light.neverCull ));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown enum"); //TODO is this necessary
            }
        }

        this.ambientLight = new LeafContentNode<>(this,"Ambient light", ambientLight);
        this.directionalLights = new LeafContentNode<>(this,"Directional lights", directionalLights.toArray(new DirectionalLight[0]));
        this.pointLights = new LeafContentNode<>(this,"Point lights", pointLights.toArray(new PointLight[0]));
    }

    public Lighting(TreeNode parent, AmbientLight ambientLight, DirectionalLight[] directionalLights, PointLight[] pointLights){
         super(parent);
        //TODO verify
        this.ambientLight = new LeafContentNode<>(this,"Ambient light", ambientLight);
        this.directionalLights = new LeafContentNode<>(this,"Directional lights", directionalLights);
        this.pointLights = new LeafContentNode<>(this,"Point lights", pointLights);
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

        public int getR(){ return r; }
        public int getG(){ return g; }
        public int getB(){ return b; }

        public String toStringRGB(){
            return r + " " + g + " " + b;
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

        public String toStringRGBXYZ() {
            return r + " " + g + " " + b + " " + x  + " " + y + " " + z;
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

        public String toStringData() {
            if(neverCull){
                return r + " " + g + " " + b + " " + x  + " " + y + " " + z + " " + lightRadiusInPx + " NeverCull";
            }else{
                return r + " " + g + " " + b + " " + x  + " " + y + " " + z + " " + lightRadiusInPx;
            }
        }
    }


    public int getChildCount() {
        int nOfDirectionalLights = getNumberOfNonNullElements(directionalLights.getContent());
        int nOfPointLights = getNumberOfNonNullElements(pointLights.getContent());

        return 1 + nOfDirectionalLights + nOfPointLights; //Ambient + directional + point
    }

    public Enumeration<TreeNode> children() {
        ArrayList<TreeNode> list = new ArrayList<>();
        list.add(ambientLight);
        for(DirectionalLight directionalLight : directionalLights.getContent()){
            if(directionalLight != null){ list.add(directionalLight); }
        }
        for(PointLight pointLight : pointLights.getContent()){
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

    public String toString(){
        return "Lights";
    }
}
