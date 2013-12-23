package skullMod.lvlEdit.gui.modeChange;

import javax.swing.*;

public class ModeChanger extends JComboBox<String>{
    public enum Modes{
        SCENE, MODEL, ANIMATION, SHAPE;

        public final String name;

        Modes(){
            name = this.name();
        }
        public String toString(){ return this.name(); }
    }

    public ModeChanger(){

        for(Modes mode : Modes.values()){
            this.addItem(mode.name());
        }

        this.setSelectedIndex(0);
    }
}
