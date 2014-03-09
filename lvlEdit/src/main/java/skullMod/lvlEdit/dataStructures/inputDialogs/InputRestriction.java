package skullMod.lvlEdit.dataStructures.inputDialogs;

//TODO implement restrictions to the simple input dialog (do more important stuff first)
public interface InputRestriction<T> {
    public boolean isValid(T object);
}
