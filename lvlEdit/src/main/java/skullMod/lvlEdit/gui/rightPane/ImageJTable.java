package skullMod.lvlEdit.gui.rightPane;

/**
 * JTable for images
 */
public class ImageJTable extends HashMapJTable{
    protected ImageJTable() { super(); }


    protected boolean isValidValue(String key, String oldValue, String newValue, int row, int col) {
        //TODO use constant strings
        if(key.equals("Name") && newValue.matches("[\\w]+.msb.sgm")){
            return true;
        }else{
            return false;
        }
    }
    protected void initHashMapValues() {
        hashMap.put("Name", "");
        hashMap.put("Image Width","");
        hashMap.put("Image Height", "");
    }
    protected void invalidValueFound(String key, String oldValue, String newValue, int row, int col) {

    }
}
