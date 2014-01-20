package skullMod.sprConv.dataStructures.SPR;

public class StringNode {
    private final String string;
    private final Object parent;
    private int index;
    private final String prefix;

    public StringNode(Object parent, String prefix, String string, int index){
        this.prefix = prefix;
        this.string = string;
        this.parent = parent;
        this.index = index;
    }
    public String getString(){
        return string;
    }


    public String toString(){
        return prefix + ": " + string;
    }
    public Object getParent(){
        return parent;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
