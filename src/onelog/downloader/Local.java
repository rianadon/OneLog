package onelog.downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Local
 */
public class Local extends Downloader {

    private static Logger logger = Logger.getLogger(Local.class.getName());
    private List<String> files;

	@Override
	public void download(String root) throws IOException {
        for (String f : files) {
            Path from = Paths.get(f);
            Path to = Paths.get(root, new File(f).getName());
            logger.fine("Copying from " + from + " to " + to);
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public List<Path> exports(String root) {
        return files.stream().map(f ->
            Paths.get(root, new File(f).getName())
        ).collect(Collectors.toList());
    }

}
