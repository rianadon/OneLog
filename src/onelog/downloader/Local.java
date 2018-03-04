package onelog.downloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Local
 */
public class Local extends Downloader {

    private List<String> files;

	@Override
	public void download(String root) throws IOException {
        for (String f : files) {
            Files.copy(Paths.get(f), Paths.get(root, new File(f).getName()), StandardCopyOption.REPLACE_EXISTING);
        }
	}

}
