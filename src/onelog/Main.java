package onelog;

import onelog.ConfigurationParser.Config;
import onelog.gui.UserNotificationApp;

/**
 * Main
 */
public class Main {

    private static Config config = ConfigurationParser.getConfiguration();

    public static void main(String[] args) {
        UserNotificationApp.doLaunch();
        BackgroundProcess proc = new BackgroundProcess(config.logConfig.volume, (device) -> {
            new LogProcessor(device).go();
        });
        proc.mainLoop();
    }
}
