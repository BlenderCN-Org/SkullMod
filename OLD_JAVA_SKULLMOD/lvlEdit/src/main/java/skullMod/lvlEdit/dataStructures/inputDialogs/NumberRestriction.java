package skullMod.lvlEdit.dataStructures.inputDialogs;

public class NumberRestriction implements InputRestriction<Number>{
    private final double biggerThan;

    public NumberRestriction(double biggerThan){
        this.biggerThan = biggerThan;
    }
    public boolean isValid(Number object) {
        return object.doubleValue() > biggerThan;
    }
}
