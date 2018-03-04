package onelog.downloader;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

import static onelog.Util.*;

/**
 * Downloader
 */
public abstract class Downloader {

    public String line;

    public void validate() {
        StringBuilder errors = new StringBuilder();
        iterateFields(this, (f, val) -> {
            if (val == null) {
                errors.append(" - Field " + f.getName() + " was not initialized.\n");
            }
        });
        if (errors.length() > 0)
            throw new RuntimeException("Validation errors ocurred in " + getClass().getSimpleName() +
                                       ":\n" + errors.substring(0, errors.length() - 1));
    }

    public abstract void download(String root) throws Exception;

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(getClass().getSimpleName());
        ret.append("[");
        iterateFields(this, (f, val) -> {
            ret.append(f.getName() + "=" + val);
            ret.append(", ");
        });
        return ret.substring(0, ret.length() - 2) + "]";
    }

}
