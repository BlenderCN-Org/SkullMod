package skullMod.lvlEdit.dataStructures.LVL;

//Used this instead of subclassing because there should never be another light type (please don't implement spot lights, thanks)
//Or I'm lazy, not sure...

public class LVL_Light {
    public static final String identifier = "Light";

    public enum LightType{
        //Ambient: ONE ambient light, hahaha
        //Directional: UPTO FOUR directional lights, hahaha (don't use more than 2)
        //Point: UPTO EIGHT point lights, hahaha (performance hogs, use max. 4, rest is reserved for effects)

        AMBIENT("Amb", 1),        //RGB
        DIRECTIONAL("Dir", 4),    //RGB XYZ
        POINT("Pt", 8);           //RGB XYZ "Radius in pixels(at default screen res of 1280x720)" nevercull

        public final String abbrevation;
        public final int suggestedMaxAmount;

        LightType(String abbrevation, int suggestedMaxAmount){
            this.abbrevation = abbrevation;
            this.suggestedMaxAmount = suggestedMaxAmount;
        }
    }

    public LightType type;

    public int r,g,b;

    /* X+ is right, Y+ is up, Z+ is towards camera */
    public int x,y,z; //Vector when directonal, position coordinate when point

    public int pointLightRadiusInPx = 1000; //Only for point light
    public boolean neverCull = false; //Only for point light


    //Create ambient light
    public LVL_Light(int r, int g, int b){
        this.type = LightType.AMBIENT;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public LVL_Light(int r, int g, int b, int x, int y, int z){
        this.type = LightType.DIRECTIONAL;
        this.r = r;
        this.g = g;
        this.b = b;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public LVL_Light(int r, int g, int b, int x, int y, int z, int pointLightRadiusInPx, boolean neverCull){
        this.type = LightType.POINT;
        this.r = r;
        this.g = g;
        this.b = b;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pointLightRadiusInPx = pointLightRadiusInPx;
        this.neverCull = neverCull;
    }
}
