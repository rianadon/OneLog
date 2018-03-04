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

}
