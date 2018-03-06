package onelog;

import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UserNotification extends Application {

    private static CountDownLatch latch = new CountDownLatch(1);

    private static boolean isLaunched = false;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // Platform.setImplicitExit(false);

        primaryStage = stage;
        latch.countDown();
    }

    public static void makeSecond() {
        Stage secondStage = new Stage();
        secondStage.initStyle(StageStyle.UNDECORATED);
        secondStage.setAlwaysOnTop(true);

        //creating a Text object
        Text t = new Text(50, 55, "OneLog initializing...");
        t.setFont(Font.font("Consolas", 40));
        t.setFill(Color.web("#FFCC22"));

        //creating a Group object
        Group group = new Group(t);

        //Creating a Scene by passing the group object, height and width
        Scene scene = new Scene(group, 600, 100);

        //setting color to the scene
        scene.setFill(Color.BLACK);

        System.out.println(scene.getHeight());
        Rectangle r = new Rectangle(0, scene.getHeight() - 8, 150, 8);
        r.setFill(Color.web("#997722"));
        group.getChildren().add(r);

        //Setting the title to Stage.
        secondStage.setTitle("OneLog");

        //Adding the scene to Stage
        secondStage.setScene(scene);

        //Displaying the contents of the stage
        secondStage.show();

        //Setting the position of the stage
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        secondStage.setX((bounds.getWidth() - secondStage.getX()) / 2);
        secondStage.setY(0);
    }

    public static void go() {
        Platform.runLater(UserNotification::makeSecond);
    }

    public static void doLaunch() {
        (new Thread(() -> {
            launch();
        })).start();
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
}
