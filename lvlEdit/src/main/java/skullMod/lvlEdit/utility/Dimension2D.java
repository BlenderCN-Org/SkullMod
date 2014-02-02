package skullMod.lvlEdit.utility;

public class Dimension2D<T extends Number> {
    private T x,y;

    public Dimension2D(T x, T y){
        setX(x);
        setY(y);
    }

    public T getX(){
        return x;
    }

    public T getY(){
        return y;
    }

    public void setX(T x){
        this.x = x;
    }

    public void setY(T y){
        this.y = y;
    }
}
