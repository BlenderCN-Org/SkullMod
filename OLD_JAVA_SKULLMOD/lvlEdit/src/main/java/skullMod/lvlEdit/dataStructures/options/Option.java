package skullMod.lvlEdit.dataStructures.options;

import java.util.HashMap;

/**
 * Avoid unnecessary generics and class casting while having control over the model values
 */
public class Option {
    public enum OptionType{ INT, FLOAT, STRING }

    //Used for things like ALPHA_NUMERIC or POSITIVE
    public static final int INT_TRUE = 1;
    public static final int INT_FALSE = 0;

    public enum StringRestrictionOptions{ LONGER_THAN, SHORTER_THAN, FIXED_LENGTH, ALPHA_NUMERIC, ALPHA_NUMERIC_WITH_SPACES, NOT_EMPTY }
    public enum NumericRestrictionOptions{ BIGGER_THAN, SMALLER_THAN, BIGGER_OR_EQUAL_THAN, SMALLER_OR_EQUAL_THAN, POSITIVE, NEGATIVE }

    public final OptionType type;

    private final HashMap<StringRestrictionOptions, Integer> stringRestrictions;
    private final HashMap<NumericRestrictionOptions, Integer>  numericRestrictions;
    private String content;


    /**
     * Make a string option
     * @param content Starting content, is validated as well
     * @param restrictions String restrictions, may be null
     */
    public Option(String content, HashMap<StringRestrictionOptions, Integer> restrictions){
        //Check incoming data
        if(content == null){ throw new IllegalArgumentException("content must not be null"); }
        if(!areValidStringConstrictions(restrictions)){ throw new IllegalArgumentException("Invalid string constriction found"); }

        //Set class members
        this.type = OptionType.STRING;
        this.stringRestrictions = restrictions;
        this.numericRestrictions = null;

        //With all class members in place the initial content is checked for validity
        if(!isValid(content)){ throw new IllegalArgumentException("Initial content must be valid"); }
        this.content = content;
    }

    public synchronized boolean setContent(String content){ return writeValue(content); }
    public String getContent(){ return content; }

    private static boolean areValidStringConstrictions(HashMap<StringRestrictionOptions, Integer> strConstr) {
        final boolean longer_than, shorter_than, fixed_length, alpha_numeric, alpha_numeric_with_spaces, not_empty;
        if(strConstr.containsKey(StringRestrictionOptions.LONGER_THAN)){ longer_than = true; }else{ longer_than = false; }
        if(strConstr.containsKey(StringRestrictionOptions.SHORTER_THAN)){ shorter_than = true; }else{ shorter_than = false; }
        if(strConstr.containsKey(StringRestrictionOptions.FIXED_LENGTH)){ fixed_length = true; }else{ fixed_length = false; }
        if(strConstr.containsKey(StringRestrictionOptions.ALPHA_NUMERIC)){ alpha_numeric = true; }else{ alpha_numeric = false; }
        if(strConstr.containsKey(StringRestrictionOptions.ALPHA_NUMERIC_WITH_SPACES)){ alpha_numeric_with_spaces = true; }else{ alpha_numeric_with_spaces = false; }
        if(strConstr.containsKey(StringRestrictionOptions.NOT_EMPTY)){ not_empty = true; }else{ not_empty = false; }

        //This combination is not allowed
        if(fixed_length & (longer_than | shorter_than)){ return false; }
        //Check for invalid length
        if(longer_than & (strConstr.get(StringRestrictionOptions.LONGER_THAN) < 0)){ return false; }
        if(shorter_than & (strConstr.get(StringRestrictionOptions.SHORTER_THAN) <= 1)){ return false; }

        //TODO more checks for unlikely stuff
        return true;
    }

    private static boolean areValidNumericConstrictions(HashMap<NumericRestrictionOptions, Integer> numericConstrictions){
        //TODO
        return false;
    }

    private boolean writeValue(String content){
        if(isValid(content) == false){ return false; }



        return true;
    }

    public boolean isValid(String content){
    return false;
    }
}
