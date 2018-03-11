package onelog.downloader;

import static onelog.Util.*;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.List;

import onelog.parser.Parser;

/**
 * Downloader
 */
public abstract class Downloader {

    public String line;
    public String parser;

    public abstract void download(String root) throws Exception;
    public abstract List<Path> exports(String root);

    @SuppressWarnings("unchecked")
    public Constructor<Parser> getParserConstructor() {
        try {
            Class<Parser> pclass = (Class<Parser>) Class.forName("onelog.parser." + parser);
            return pclass.getDeclaredConstructor(Path.class);
		} catch (Exception e) {
            System.out.println(e);
			throw new RuntimeException("Parser class with name " + parser + " not found (" + e.toString() + ")");
		}
    }

    @Override
    public String toString() {
        return configToString(this);
    }

}
