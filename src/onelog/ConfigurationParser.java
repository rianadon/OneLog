package onelog;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.moandjiezana.toml.Toml;
import onelog.downloader.Downloader;

import static onelog.Util.*;

/**
 * ConfigurationParser
 */
public class ConfigurationParser {

    public static final File LOG_FILE = new File("logs.toml");

    private static Downloader downloaderFor(String name, Toml config) {
        String line = unquotify(name);
        int n = parseNumber(line);
        if (n == 1) return null;

        String tp = config.getString("type");
        try {
            Class<?> c = Class.forName("onelog.downloader." + tp);
            Downloader down = (Downloader) config.to(c);
            down.line = line;
            down.validate();
            return down;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Downloader class with name " + tp + " not found");
        }
    }

    public static List<Downloader> getConfiguration() {
        Toml toml = new Toml().read(LOG_FILE);
        boolean errors = false;
        List<Downloader> downloaders = new ArrayList<>();

        for (String name : toml.toMap().keySet()) {
            try {
                Downloader d = downloaderFor(name, toml.getTable(name));
                if (d == null) continue;
                downloaders.add(d);
            } catch (RuntimeException e) {
                errors = true;
                System.out.println("Error: " + e.getMessage());
            }
        }

        if (errors) throw new RuntimeException("Errors exist in configuration.");

        downloaders.sort(Comparator.comparingInt((d) -> parseNumber(d.line)));
        return downloaders;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        List<Downloader> downloaders = getConfiguration();
        for (Downloader d : downloaders) {
            System.out.println(d.line);
            try {
                d.download("E:/logs/");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
