package onelog.downloader;

import static onelog.Util.*;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.List;

import onelog.parser.Parser;

/**
 * Downloaders are responsible from retrieving log files from wherever they may be stored. They have
 * two important methods: download and exports. The LogProcesser will first call download to
 * retrieve all the log files, then use the exports method to determine which files to run through
 * the attached parser.
 *
 * Note that the two public fields line and parser are populated by the LogProcessor after
 * instantiation. Thus, you do not have to write a constructor to handle them (in fact all
 * Downloaders must have a default constructor), but these fields will also be null within the
 * constructor. Thus it is recommended you do all setup work in the download method.
 */
public abstract class Downloader {

    public String line;
    public String parser;

    /**
     * Downloads all the configured files to the given root directory.
     * @param root  The directory to which the files will be downloaded
     */
    public abstract void download(String root) throws Exception;

    /**
     * Returns a list of absolute paths to the files that were downloaded by the download function.
     *
     * Since this always run after the download function, you can use the former to generate this
     * list and return it via this function.
     *
     * @param root  The same directory passed to the download method, indicating the root directory
     * to which all files were downloaded
     */
    public abstract List<Path> exports(String root);

    /**
     * Returns the constructor for the Parser which will parse all files downloaded by this
     * Downloader.
     */
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
