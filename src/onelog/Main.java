package onelog;

import onelog.gui.UserNotificationApp;

/**
 * Main
 */
public class Main {
    public static void main(String[] args) {
        UserNotificationApp.doLaunch();
        BackgroundProcess proc = new BackgroundProcess("PATRIOT", (device) -> {
            new LogProcessor(device).go();
        });
        proc.mainLoop();
    }
}
