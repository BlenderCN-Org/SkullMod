package skullMod.lvlEdit.gui.rightPane;

/**
 * JTable for images
 */
public class SceneJTable extends HashMapJTable{
    protected SceneJTable() { super(); }


    protected boolean isValidValue(String key, String oldValue, String newValue, int row, int col) {
        return false;
    }
    protected void initHashMapValues() {
        hashMap.put("Scenename", "name");
    }
    protected void invalidValueFound(String key, String oldValue, String newValue, int row, int col) {

    }
}
