package skullMod.lvlEdit.utility;

import java.lang.reflect.Array;
import java.util.Enumeration;

final public class ArrayEnumerationFactory {
    static public Enumeration makeEnumeration(final Object obj) {
        Class type = obj.getClass();
        if (!type.isArray()) {
            throw new IllegalArgumentException(obj.getClass().toString());
        } else {
            return (new Enumeration() {
                int size = Array.getLength(obj);

                int cursor;

                public boolean hasMoreElements() {
                    return (cursor < size);
                }

                public Object nextElement() {
                    return Array.get(obj, cursor++);
                }
            });
        }
    }
}