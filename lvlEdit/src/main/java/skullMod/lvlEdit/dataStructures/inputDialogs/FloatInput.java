package skullMod.lvlEdit.dataStructures.inputDialogs;

import javax.swing.*;

//FIXME Terrible, make this better
public class FloatInput extends SimpleInputDialog{
    public boolean isValid;

    public FloatInput(JFrame parent, String title, String text){
        super(parent, title, text);

        String t = this.result;

        if(t == null){ System.out.println("NULL IN INTEGER INPUT"); }

        try{
            Float.parseFloat(t);
            isValid = true;
        }catch (NumberFormatException pe){
            JOptionPane.showMessageDialog(parent, "ERROR");
            isValid = false;
        }
    }
    public Float getFloat(){
        return Float.parseFloat(super.result);
    }
}
