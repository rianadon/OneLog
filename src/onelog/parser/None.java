package onelog.parser;

import java.nio.file.Path;
import java.util.logging.LogRecord;

/**
 * None
 */
public class None extends Parser {

    public None(Path file) throws Exception {
        super(file);
    }

    public LogRecord nextRecord() throws Exception {
        return null;
    }

}
