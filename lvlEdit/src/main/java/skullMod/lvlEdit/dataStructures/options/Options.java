package skullMod.lvlEdit.dataStructures.options;

import java.util.EventListener;

public final class Options {


    /*Only for factory use*/
    protected Options(){}

    public void setOption(String option, String value){

    }

    public interface OptionsChangedListener extends EventListener {
        public void fireOptionChanged(String option);
        public void fireOptionsChanged(Options opt); //The new options object, the old one is not updated after this is called
    }


}
