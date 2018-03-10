package onelog.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape3D;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * UserNotificationStage
 */
public class UserNotificationStage {
    public static final double SCENE_WIDTH = 650;
    public static final double SCENE_HEIGHT = 100;
    public static final String STAGE_TITLE = "OneLog";
    public static final String FONT_NAME = "Consolas";
    public static final double TEXT_MARGIN = 10;
    public static final double PBAR_HEIGHT = 8;
    public static final double ERROR_HEIGHT = 150;

    private Stage stage;

    private Label statusText;
    private Rectangle progress;
    private Label errors;

    Timeline ringTimeline;

    public UserNotificationStage() throws Exception {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);

        Group group = (Group) FXMLLoader.load(getClass().getResource("/layout.fxml"));
        statusText = (Label) group.lookup("#statusText");
        progress = (Rectangle) group.lookup("#progress");
        errors = (Label) group.lookup("#errors");

        PerspectiveCamera camera = new PerspectiveCamera(false);
        SubScene subscene = (SubScene) group.lookup("#subscene");
        subscene.setCamera(camera);
        animateRing((Shape3D) subscene.getRoot().lookup("#torus"));

        Button close = (Button) group.lookup("#close");
        close.setOnMouseClicked(e -> stage.close());

        // Group group = new Group(statusText, animation());
        Scene scene = new Scene(group, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(Color.BLACK);

        progress = new Rectangle(0, SCENE_HEIGHT - PBAR_HEIGHT, 150, PBAR_HEIGHT);
        progress.setFill(Color.web("#997722"));
        group.getChildren().add(progress);

        stage.setTitle(STAGE_TITLE);
        stage.setScene(scene);
    }

    private void animateRing(Shape3D torus) {
        torus.setRotationAxis(new Point3D(0, 1, 0));

        ringTimeline = new Timeline();
        ringTimeline.setCycleCount(Timeline.INDEFINITE);
        KeyValue kv1 = new KeyValue(torus.rotationAxisProperty(), new Point3D(1, 0, 0), new Point3DInterpolator());
        KeyValue kv2 = new KeyValue(torus.rotateProperty(), 360);
        KeyValue kv3 = new KeyValue(torus.rotationAxisProperty(), new Point3D(0, 1, 0), new Point3DInterpolator());
        KeyFrame kf1 = new KeyFrame(Duration.millis(1000), kv1);
        KeyFrame kf2 = new KeyFrame(Duration.millis(2000), kv2, kv3);
        ringTimeline.getKeyFrames().addAll(kf1, kf2);
        ringTimeline.play();
    }

    public void show() { Platform.runLater(stage::show); }
    public void hide() { Platform.runLater(stage::hide); }
    public double getWidth() { return stage.getWidth(); }
    public double getHeight() { return stage.getHeight(); }
    public void stopAnim() { Platform.runLater(ringTimeline::stop); }

    public void setPosition(double x, double y) {
        stage.setX(x);
        stage.setY(y);
    }

    public void setText(String text) {
        Platform.runLater(() -> {
            statusText.setFont(Font.font(FONT_NAME, 30));
            statusText.setTextFill(Color.web("#CCCCAA"));
            statusText.setText(text);
        });
    }

    public void setProgress(double prog, double max) {
        Platform.runLater(() -> {
            progress.setWidth(prog / max * SCENE_WIDTH);
        });
    }

    public void animateToErrors() {
        WritableValue<Double> writableHeight = new WritableValue<Double>() {
            @Override
            public Double getValue() { return stage.getHeight(); }

            @Override
            public void setValue(Double value) { stage.setHeight(value); }
        };

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        KeyValue kv = new KeyValue(writableHeight, SCENE_HEIGHT + ERROR_HEIGHT, Interpolator.EASE_OUT);
        KeyFrame kf = new KeyFrame(Duration.millis(800), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    public void addError(String error) {
        Platform.runLater(() -> {
            boolean firstTime = errors.getText().length() == 0;
            errors.setText(errors.getText() + error + "\n");
            if (firstTime) animateToErrors();
        });
    }

    private static class Point3DInterpolator extends Interpolator {
		@Override
		protected double curve(double t) {
			return t;
        }
        @Override
        public Object interpolate(Object startValue, Object endValue, double fraction) {
            Point3D sv = (Point3D) startValue;
            Point3D ev = (Point3D) endValue;
            return new Point3D(
                interpolate(sv.getX(), ev.getX(), fraction),
                interpolate(sv.getY(), ev.getY(), fraction),
                interpolate(sv.getZ(), ev.getZ(), fraction)
            );
        }
    }
}
