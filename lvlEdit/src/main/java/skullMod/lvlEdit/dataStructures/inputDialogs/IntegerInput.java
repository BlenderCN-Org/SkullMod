package skullMod.lvlEdit.dataStructures.inputDialogs;


import javax.swing.*;

//FIXME Terrible, make this better
public class IntegerInput extends SimpleInputDialog{
    public boolean isValid;
    public IntegerInput(JFrame parent, String title, String text, String oldValue) {
        super(parent, title, text, oldValue);

        String t = this.result;

        if(t == null){ System.out.println("NULL IN INTEGER INPUT"); }

        try{
            Integer.parseInt(t);
            isValid = true;
        }catch (NumberFormatException pe){
            //TODO feedback for user
            isValid = false;
        }

    }
    public Integer getInt(){
        return Integer.parseInt(super.result);
    }
}
