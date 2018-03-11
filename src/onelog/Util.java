package onelog;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * Util
 */
public class Util {

    private static String[] NUMBERS = {"zero", "one", "two", "three", "four", "five", "six",
                                       "seven", "nine", "ten", "eleven", "twelve", "thirteen",
                                       "fourteen", "fifteen", "sixteen", "seventeen", "eighteen",
                                       "nineteen", "twenty"};

    public static String unquotify(String s) {
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static int parseNumber(String nString) {
        String number = nString.substring(0, nString.indexOf(" ")).toLowerCase();
        int index = Arrays.asList(NUMBERS).indexOf(number);
        if (index == -1) throw new RuntimeException("Line \"" + nString + "\" does not start with a number.");
        return index;
    }

    public static void iterateFields(Object obj, BiConsumer<Field, Object> it) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                it.accept(f, f.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void validateFields(Object obj) {
        StringBuilder errors = new StringBuilder();
        iterateFields(obj, (f, val) -> {
            if (val == null) {
                errors.append(" - Field " + f.getName() + " was not initialized.\n");
            }
        });
        if (errors.length() > 0)
            throw new RuntimeException("Validation errors ocurred in " + obj.getClass().getSimpleName() +
                                       ":\n" + errors.substring(0, errors.length() - 1));
    }

    public static String configToString(Object obj) {
        StringBuilder ret = new StringBuilder(obj.getClass().getSimpleName());
        ret.append("[");
        iterateFields(obj, (f, val) -> {
            ret.append(f.getName() + "=" + val);
            ret.append(", ");
        });
        return ret.substring(0, ret.length() - 2) + "]";
    }

}
