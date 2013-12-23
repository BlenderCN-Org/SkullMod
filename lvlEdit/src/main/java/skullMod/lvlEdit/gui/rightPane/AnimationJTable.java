package skullMod.lvlEdit.gui.rightPane;

/**
 * JTable for animations
 */
public class AnimationJTable extends HashMapJTable{
    protected AnimationJTable() { super(); }


    protected boolean isValidValue(String key, String oldValue, String newValue, int row, int col) {
        return false;
    }
    protected void initHashMapValues() {
        hashMap.put("Animationname", "name");
    }
    protected void invalidValueFound(String key, String oldValue, String newValue, int row, int col) {

    }
}
