package onelog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import onelog.downloader.Downloader;
import onelog.gui.UserNotificationApp;
import onelog.gui.UserNotificationStage;

/**
 * LogProcessor
 */
public class LogProcessor {

    private static List<Downloader> downloaders = ConfigurationParser.getConfiguration();
    private static int progressMax = downloaders.size() + 2;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH;mm;ss");

    private UsbDevice device;
    private UserNotificationStage stage;

    public LogProcessor(UsbDevice device) {
        this.device = device;
    }

    public void go() {
        stage = UserNotificationApp.createWindow();
        stage.setProgress(1, progressMax);
        try {
            Path folderPath = makeFolder();
            stage.setProgress(2, progressMax);
            downloadLogs(folderPath);
		} catch (IOException e) {
			e.printStackTrace();
        } finally {
            stage.hide();
        }
    }

    private Path makeFolder() throws IOException {
        String folderName = dateFormat.format(new Date());
        Path folderPath = Paths.get(device.path, folderName);
        Files.createDirectory(folderPath);
        return folderPath;
    }

    private void downloadLogs(Path folderPath) {
        for (int i = 0; i < downloaders.size(); i++) {
            Downloader d = downloaders.get(i);
            System.out.println(d.line);
            stage.setText(d.line);
            try {
                d.download(folderPath.toString());
            } catch (Exception e) {
                System.out.println(e.toString());
            }
            stage.setProgress(i+3, progressMax);
        }
    }

}
