package onelog.downloader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.Paths.*;

import com.esotericsoftware.wildcard.Paths;

/**
 * A downloader for files stored on the local computer.
 */
public class Local extends Downloader {

    private static Logger logger = Logger.getLogger(Local.class.getName());
    private List<String> files;

    private List<Paths> expandedPaths() {
        return files.stream()
            .map(this::pathsFor)
            .collect(Collectors.toList());
    }

    private Paths pathsFor(String f) {
        String[] split = f.split("[/]{2,}");
        if (split.length == 1) {
            return new Paths().addFile(f);
        } else if (split.length == 2) {
            return new Paths().glob(split[0], split[1]);
        } else if (split.length == 3) {
            Paths all = new Paths().glob(split[0], split[1]);
            Integer keep = Integer.parseInt(split[2]);
            TreeSet<String> under = new TreeSet<>();
            for (String file : all) {
                if (under.size() < keep) under.add(file);
                else if (under.first().compareTo(file) < 0) {
                    under.pollFirst();
                    under.add(file);
                }
            }
            Iterator<String> it = all.iterator();
            while (it.hasNext()) {
                if (!under.contains(it.next())) it.remove();
            }
            return all;
        } else {
            throw new RuntimeException("Path notation " + split + " is invalid (more than three //)");
        }
    }

	@Override
	public void download(String root) throws IOException {
        for (Paths from : expandedPaths()) {
            Paths to = from.copyTo(root);
            logger.fine("Copied from " + from + " to " + to);
        }
    }

    @Override
    public List<Path> exports(String root) {
        List<Path> exp = new ArrayList<>();
        for (Paths from : expandedPaths()) {
            for (String relative : from.getRelativePaths()) {
                exp.add(get(root, relative));
            }
        }
        return exp;
    }

}
