package skullMod.lvlEdit.gui.rightPane;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A table with an HashMap data model, allows only two columns,
 * only the right one is editable
 */
public class HashMapJTable extends JTable{
    private HashMap<String, Object> hashMap;

    public HashMapJTable(){ this(null); }


    public HashMapJTable(HashMap<String, Object> hashMap){

        if(hashMap == null){
            this.hashMap = new HashMap<>();
        }else{
            this.hashMap = hashMap;
        }

        this.setModel(new HashMapTableModel());

        //TODO check if this is necessary
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //TODO explain what this does


    }

    //TODO explain what this does in simple terms
    public void setPreferredScrollableViewportSize(){
        //TODO ignored, see the getter for this attribute
    }

    public Dimension getPreferredScrollableViewportSize(){
        return getPreferredSize();
    }

    private class HashMapTableModel implements TableModel {
        public String getColumnName(int i){
            if(i < 0 || i > 1){ throw new IllegalArgumentException("Out of table bounds"); }
            if(i == 0){
                return "Name";
            }else{
                return "Value";
            }
        }

        //FIXME don't cheat the alignment use the proper way
        public Class<?> getColumnClass(int i) {
            if(i < 0 || i > 1) { throw new IllegalArgumentException("Out of table bounds"); }
            if(i == 0){
                return Integer.class; //This cheats the JTable into aligning the content on the right side
            }
            return String.class; //This is aligned on the left
        }

        public boolean isCellEditable(int row, int col) {
            if(col == 1){
                return true;
            }else{
                return false;
            }
        }

        public int getRowCount() {
            return hashMap.size();
        }
        public int getColumnCount() {
            return 2;
        }
        public Object getValueAt(int row, int column) {
            if(row > getRowCount() || column > getColumnCount() || row < 0 || column < 0){
                throw new IllegalArgumentException("Out of table bounds");
            }

            Set<Map.Entry<String,Object>> entries= hashMap.entrySet();


            int i = 0;
            for(Map.Entry<String,Object> entry : entries){

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

        public void setValueAt(Object input, int row, int col) {
            if(col != 1){ throw new IllegalArgumentException("Out of table bounds"); }
            hashMap.put(getValueAt(row, 0).toString(),input.toString());

            System.out.println(input.toString() + " " + row + " " + col);
        }
        public void addTableModelListener(TableModelListener tableModelListener) {
        }
        public void removeTableModelListener(TableModelListener tableModelListener) {
        }


    }
}
