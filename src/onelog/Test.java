package onelog;

import javafx.application.Platform;
import onelog.gui.UserNotificationApp;
import onelog.gui.UserNotificationStage;

/**
 * Test
 */
public class Test {

    public static void main(String[] args) throws Exception {
        UserNotificationApp.doLaunch();
        UserNotificationStage stage = UserNotificationApp.createWindow();
        stage.setText("Three Logs for the Camera streams of the raspis");
        Platform.runLater(stage::animateToErrors);
        // Thread.sleep(5000);
        // UserNotification.go();
    }
}
