package skullMod.lvlEdit.gui.rightPane;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.*;

/**
 * A table with a HashMap data model, allows only two columns, only the right column is editable
 *
 * The validation of fields is done using the method isValidValue(...)
 * The initalisation of fields is done using the method initHashMapValues(...)
 * If the validation failed the invalidValueFound(...) method is called
 */
public abstract class HashMapJTable extends JTable{
    /** The internal HashMap */
    protected final HashMap<String, String> hashMap;

    /**
     * Constructor
     */
    protected HashMapJTable(){
        this.hashMap = new HashMap<>();
        this.initHashMapValues();

        this.setModel(new HashMapTableModel());

        //TODO check if this is necessary
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    //TODO explain what this does in simple terms
    public void setPreferredScrollableViewportSize(){
        //TODO ignored, see the getter for this attribute, throw exception?
    }

    /** TODO Explain */
    public Dimension getPreferredScrollableViewportSize(){ return getPreferredSize(); }

    /** Internal class for TableMOdel */
    private class HashMapTableModel implements TableModel {
        public String getColumnName(int i){
            if(i < 0 || i > 1){ throw new IllegalArgumentException("Out of table bounds"); }
            if(i == 0){
                return "Name";
            }else{
                return "Value";
            }
        }


        //FIXME don't cheat the alignment use the proper way, whatever that might be
        public Class<?> getColumnClass(int i) {
            if(i < 0 || i > 1) { throw new IllegalArgumentException("Out of table bounds"); }
            if(i == 0){
                return Integer.class; //This cheats the JTable into aligning the content on the right side
            }
            return String.class; //This is aligned on the left
        }

        /**
         * Only the "Value" column is editable
         * @param row The row
         * @param col The column
         * @return Whether the cell is editable
         */
        public boolean isCellEditable(int row, int col) {
            if(col == 1){
                return true;
            }else{
                return false;
            }
        }

        /**
         * Row count
         * @return row count
         */
        public int getRowCount() { return hashMap.size(); }

        /**
         * Column count, fixed to 2 (one for key, one for value)
         * @return Always 2
         */
        public int getColumnCount() { return 2; }

        /**
         * Value at table position
         * @param row value at this row
         * @param column value at this column
         * @return The value, always a String
         */
        public Object getValueAt(int row, int column) {
            if(row > getRowCount() || column > getColumnCount() || row < 0 || column < 0){
                throw new IllegalArgumentException("Out of table bounds");
            }

            Set<Map.Entry<String,String>> entries= hashMap.entrySet();

            int i = 0;
            for(Map.Entry<String,String> entry : entries){
                if(i == row){
                    if(column == 0){
                        return entry.getKey();
                    }else{
                        return entry.getValue();
                    }
                }
                i++;
            }
            throw new IllegalArgumentException("Out of table bounds");

        }

        /**
         * Set value at given position, only the "value" row is editable
         * @param input Object to be written, toString() is called on it for the actual value
         * @param row The row in which to write
         * @param col The column in which to write
         */
        public void setValueAt(Object input, int row, int col) {
            if(col != 1){ throw new IllegalArgumentException("Out of table bounds"); }

            if(isValidValue(getValueAt(row, 0).toString(), getValueAt(row, 1).toString(), input.toString(),row,col)){
                hashMap.put(getValueAt(row, 0).toString(),input.toString());
            }else{
                invalidValueFound(getValueAt(row, 0).toString(), getValueAt(row, 1).toString(), input.toString(),row,col);
            }
        }

        //TODO listener methods, add them
        public void addTableModelListener(TableModelListener tableModelListener) {
        }
        public void removeTableModelListener(TableModelListener tableModelListener) {
        }
    }

    /** Valid value  */
    protected abstract boolean isValidValue(String key, String oldValue, String newValue, int row, int col);

    /** init the HashMap values using the setter */
    protected abstract void initHashMapValues();

    /** what to do when an invalid value found */
    protected abstract void invalidValueFound(String key, String oldValue, String newValue, int row, int col);
}
