package skullMod.lvlEdit.gui.rightPane;

/**
 * JTable for shapes
 */
public class ShapeJTable extends HashMapJTable{
    protected ShapeJTable() { super(); }


    protected boolean isValidValue(String key, String oldValue, String newValue, int row, int col) {
        return false;
    }
    protected void initHashMapValues() {
        hashMap.put("Shapename", "name");
    }
    protected void invalidValueFound(String key, String oldValue, String newValue, int row, int col) {

    }
}
