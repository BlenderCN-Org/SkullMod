package skullMod.lvlEdit.gui.dds_info;

import java.io.Serializable;

/**
 * A displayed rectangle, contains a name and boundaries
 * Boundaries are automatically calculate
 * The first coordinate contains the min values for x and y, the second the max values for x and y
 * The internal Object never changes
 */
public class InfoRectangle implements Serializable{
    /** Coordinates for the rectangle*/
    private final PixelCoordinate pc1,pc2;
    private String name;

    public InfoRectangle(PixelCoordinate pc1, PixelCoordinate pc2, String name){
        this.pc1 = new PixelCoordinate(0,0);
        this.pc2 = new PixelCoordinate(0,0);

        setCoordinates(pc1,pc2);
        setName(name);
    }

    public InfoRectangle(PixelCoordinate pc1, PixelCoordinate pc2) {
        this(pc1,pc2,"");
    }

    /** Copy values from given object into this object */
    public InfoRectangle(InfoRectangle other){
        this.pc1 = other.pc1;
        this.pc2 = other.pc2;
        this.name = other.name;
    }



    public PixelCoordinate getPoint1(){ return pc1; }
    public PixelCoordinate getPoint2(){ return pc2; }

    public int getWidth(){ return pc2.getX() - pc1.getX(); }
    public int getHeight(){ return pc2.getY() - pc1.getY(); }
    public String getName(){ return name;}

    public void setCoordinates(PixelCoordinate pc1, PixelCoordinate pc2){
        if(pc1 == null || pc2 == null){ throw new IllegalArgumentException("one of the coordinates is null"); }
        setCoordinates(pc1.getX(),pc1.getY(),pc2.getX(),pc2.getY());
    }

    /** Enforce order (see class doc) */
    public void setCoordinates(int x1, int y1, int x2, int y2){
        if(x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0){ throw new IllegalArgumentException("One of the coordinates is below 0"); }
        this.pc1.setCoordinates(Math.min(x1,x2), Math.min(y1,y2));
        this.pc2.setCoordinates(Math.max(x1,x2), Math.max(y1,y2));
    }

    public void setName(String name){
        if(name == null){ throw new IllegalArgumentException("Given name is null"); }
        this.name = name;
    }

    public String toString(){ return "Name: " + getName() + " First Point: " + getPoint1()+ " Second Point: " + getPoint2();}
}
