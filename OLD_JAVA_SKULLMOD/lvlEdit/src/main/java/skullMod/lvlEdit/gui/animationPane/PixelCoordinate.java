package skullMod.lvlEdit.gui.animationPane;

import java.io.Serializable;

public class PixelCoordinate implements Serializable{
    private int x,y;

    public PixelCoordinate(int x, int y){
        setX(x);
        setY(y);
    }

    public PixelCoordinate(PixelCoordinate pc){
        setCoordinates(pc);
    }

    public void setCoordinates(PixelCoordinate pc){
        if(pc == null){ throw new IllegalArgumentException("Given pc is null"); }
        setCoordinates(pc.getX(), pc.getY());
    }


    public void setCoordinates(int x, int y){
        setX(x);
        setY(y);
    }

    public int getX(){ return x; }
    public int getY(){ return y; }

    public void setX(int x){
        if(x < 0){ throw new IllegalArgumentException("Given x coordinate is below 0"); }
        this.x = x;
    }

    public void setY(int y){
        if(y < 0){ throw new IllegalArgumentException("Given y coordinate is below 0"); }
        this.y = y;
    }

    public String toString(){
        return "x: " + x + " y: " + y;
    }
}
