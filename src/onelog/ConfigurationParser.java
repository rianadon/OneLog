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

    private static Config config = null;

    public static class LogConfig {
        public String volume;
        public String dir;
        public String time_format;
        public String app_log;
        public String combined_log;
        public String line;

        @Override
        public String toString() {
            return configToString(this);
        }
    }

    public static class Config {
        List<Downloader> downloaders;
        LogConfig logConfig;

        public Config(List<Downloader> down, LogConfig lc) {
            downloaders = down;
            logConfig = lc;
        }

        @Override
        public String toString() {
            return configToString(this);
        }
    }

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
            validateFields(down);
            down.getParserConstructor(); // Make sure this runs correctly
            return down;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Downloader class with name " + tp + " not found");
        }
    }

    private static LogConfig logConfigFor(String name, Toml config) {
        String line = unquotify(name);
        LogConfig lc = config.to(LogConfig.class);
        lc.line = line;
        validateFields(lc);
        return lc;
    }

    public static Config getConfiguration() {
        if (config != null) return config;

        Toml toml = new Toml().read(LOG_FILE);
        boolean errors = false;
        List<Downloader> downloaders = new ArrayList<>();
        LogConfig logConfig = null;

        for (String name : toml.toMap().keySet()) {
            try {
                Downloader d = downloaderFor(name, toml.getTable(name));
                if (d == null)
                    logConfig = logConfigFor(name, toml.getTable(name));
                else
                    downloaders.add(d);
            } catch (RuntimeException e) {
                errors = true;
                System.out.println("Error: " + e.getMessage());
            }
        }

        if (errors) throw new RuntimeException("Errors exist in configuration.");

        downloaders.sort(Comparator.comparingInt((Downloader d) -> parseNumber(d.line))
          .thenComparing(Comparator.comparing((Downloader d) -> d.line)));
        return config = new Config(downloaders, logConfig);
    }
}
