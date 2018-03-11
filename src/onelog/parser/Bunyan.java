package onelog.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import onelog.BunyanLogger.Param;
import org.json.JSONObject;

/**
 * BunyanParser
 */
public class Bunyan extends Parser {

    private static final Map<Integer, Level> LEVELS = new HashMap<>();
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final Collection<String> BUILTINS = new HashSet<>();

    static {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        LEVELS.put(50, Level.SEVERE);
        LEVELS.put(40, Level.WARNING);
        LEVELS.put(30, Level.INFO);
        LEVELS.put(20, Level.FINE);
        LEVELS.put(10, Level.FINER);

        BUILTINS.add("v");
        BUILTINS.add("level");
        BUILTINS.add("name");
        BUILTINS.add("time");
        BUILTINS.add("msg");
        BUILTINS.add("err");
        // NOTE: hostname and pid are technically builtins, but because they have no equivalent in
        // the Java logging library, they are omitted so they end up in the record parameters.
    }

    private BufferedReader reader;

    public Bunyan(Path file) throws Exception {
        super(file);
        reader = new BufferedReader(new FileReader(file.toString()));
    }

    public LogRecord nextRecord() throws Exception {
        String line = reader.readLine();
        if (line == null) return null;

        JSONObject message = new JSONObject(line);
        if (message.getInt("v") != 0) {
            throw new RuntimeException("Log version 0 only supported");
        }
        LogRecord record = new LogRecord(
            LEVELS.get(message.getInt("level")),
            message.getString("msg")
        );
        if (message.has("name")) record.setSourceClassName(message.getString("name"));
        if (message.has("method")) record.setSourceMethodName(message.getString("method"));
        long time = formatter.parse(message.getString("time")).toInstant().toEpochMilli();
        record.setMillis(time);

        List<Param> parameters = new ArrayList<>();
        for (String key : message.keySet()) {
            if (!BUILTINS.contains(key)) {
                parameters.add(new Param(key, message.get(key)));
            }
        }

        record.setParameters(parameters.toArray());

        return record;
    }

}
