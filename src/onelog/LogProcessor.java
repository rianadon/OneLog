package onelog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import onelog.BunyanLogger.Param;
import onelog.ConfigurationParser.Config;
import onelog.downloader.Downloader;
import onelog.gui.UserNotificationApp;
import onelog.gui.UserNotificationStage;

/**
 * LogProcessor
 */
public class LogProcessor {

    private static Config config = ConfigurationParser.getConfiguration();
    private static int progressMax = config.downloaders.size() + 2;
    private static DateFormat dateFormat = new SimpleDateFormat(config.logConfig.time_format);

    private UsbDevice device;
    private UserNotificationStage stage;

    private boolean errored;
    private Handler handler;

    private static Logger logger = Logger.getLogger(LogProcessor.class.getName());

    public LogProcessor(UsbDevice device) {
        this.device = device;
    }

    private void addLogHandler(String path) throws IOException {
        Logger rootLogger = Logger.getLogger("onelog");
        rootLogger.setLevel(Level.ALL);
        for (Handler h : rootLogger.getHandlers()) {
            if (h instanceof FileHandler) rootLogger.removeHandler(h);
        }
        handler = new FileHandler(path, true);
        handler.setLevel(Level.ALL);
        handler.setFormatter(new BunyanLogger());
        rootLogger.addHandler(handler);
    }

    public void go() {
        stage = UserNotificationApp.createWindow();
        stage.setProgress(1, progressMax);
        try {
            Path folderPath = makeFolder();
            addLogHandler(folderPath.resolve(config.logConfig.app_log).toString());

            stage.setProgress(2, progressMax);
            downloadLogs(folderPath);
		} catch (IOException e) {
			e.printStackTrace();
        } finally {
            if (handler != null) handler.close();
            if (errored) {
                stage.setText("Oops!");
                stage.stopAnim();
            } else {
                stage.hide();
            }
        }
    }

    private Path makeFolder() throws IOException {
        String folderName = dateFormat.format(new Date());
        Path folderPath = Paths.get(device.path, config.logConfig.dir, folderName);
        Files.createDirectories(folderPath);
        return folderPath;
    }

    private void downloadLogs(Path folderPath) {
        for (int i = 0; i < config.downloaders.size(); i++) {
            Downloader d = config.downloaders.get(i);
            logger.log(Level.FINE, d.line, new Param("downloader", d));
            stage.setText(d.line);
            try {
                d.download(folderPath.toString());
                logger.fine("Downloader successfully finished");
            } catch (Exception e) {
                handleError("[" + i + "] " + e.toString(), e);
            }
            stage.setProgress(i+3, progressMax);
        }
    }

    private void handleError(String error, Exception e) {
        logger.log(Level.WARNING, error, e);
        System.out.println(error);
        stage.addError(error);
        errored = true;
    }

}
