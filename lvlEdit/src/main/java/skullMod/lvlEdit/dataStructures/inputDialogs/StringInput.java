package skullMod.lvlEdit.dataStructures.inputDialogs;

import javax.swing.JFrame;

public class StringInput extends SimpleInputDialog{

    public boolean isValid = false;
    public StringInput(JFrame parent, String title, String text) {
        super(parent, title, text);

        String t = this.result;

        //TODO add regex for ASCII + _
        if(t != null){ isValid = true;  }

    }
    public String getString(){
        return super.result;
    }
}
