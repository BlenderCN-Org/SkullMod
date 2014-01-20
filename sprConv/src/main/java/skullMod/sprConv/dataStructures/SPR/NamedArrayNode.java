package skullMod.sprConv.dataStructures.SPR;

public class NamedArrayNode {
    public final String name;
    private Object[] array;
    public NamedArrayNode(String name, Object[] array){
        this.name = name;
        this.array = array;
    }
    public Object getChild(int index){
        return array[index];
    }

    public int getChildCount(){
        return array.length;
    }
    public String toString(){
        return name;
    }

    public int findChildIndex(Object child) {
        for(int i = 0;i < array.length;i++){
            if(array[i].equals(child)){ return i; }
        }
        throw new IllegalArgumentException("Given child is not in this element");
    }
}
