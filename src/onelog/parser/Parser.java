package onelog.parser;

import java.nio.file.Path;
import java.util.logging.LogRecord;

/**
 * Parser
 */
public abstract class Parser {

    private String fname;

    public Parser(Path file) {
        fname = file.toString();
    }

    public abstract LogRecord nextRecord() throws Exception;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[file=" + fname + "]";
    }
}
