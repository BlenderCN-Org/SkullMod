package skullMod.lvlEdit.dataStructures.options;

public final class OptionsFactory {
    private Options opt;

    private OptionsFactory(){}

    /**
     * Allow only one options object
     * @return Options object
     */
    public Options getOptions(){
        if(this.opt == null){  this.opt = new Options(); }
        return this.opt;
    }

    
}
