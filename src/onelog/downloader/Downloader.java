package onelog.downloader;

import static onelog.Util.*;

/**
 * Downloader
 */
public abstract class Downloader {

    public String line;

    public abstract void download(String root) throws Exception;

    @Override
    public String toString() {
        return configToString(this);
    }

}
