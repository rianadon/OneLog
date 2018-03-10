package onelog.gui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class UserNotificationApp extends Application {

    private static CountDownLatch latch = new CountDownLatch(1);


    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);

        latch.countDown();
    }

    public static UserNotificationStage makeSecond() throws Exception {
        UserNotificationStage secondStage = new UserNotificationStage();

        // Display the stage contents
        secondStage.show();

        // Position the stage
        Platform.runLater(() -> {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            secondStage.setPosition((bounds.getWidth() - secondStage.getWidth()) / 2, 0);
        });

        return secondStage;
    }

    public static UserNotificationStage createWindow() {
        CountDownLatch stageLatch = new CountDownLatch(1);
        AtomicReference<UserNotificationStage> stage = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                stage.set(makeSecond());
                stageLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
			stageLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not wait for stage to be created");
		}
        return stage.get();
    }

    public static void doLaunch() {
        (new Thread(() -> launch())).start();
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
