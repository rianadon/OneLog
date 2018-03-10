package onelog.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
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

    private Stage stage;

    private Label statusText;
    private Rectangle progress;

    public UserNotificationStage() {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setAlwaysOnTop(true);

        statusText = new Label("OneLog initializing...");
        statusText.setTranslateX(100 + TEXT_MARGIN);
        statusText.setTranslateY(0);
        statusText.setAlignment(Pos.CENTER);
        statusText.setPrefHeight(SCENE_HEIGHT - PBAR_HEIGHT);
        statusText.setFont(Font.font(FONT_NAME, 40));
        statusText.setTextFill(Color.web("#FFCC22"));
        statusText.setMaxWidth(SCENE_WIDTH - SCENE_HEIGHT - TEXT_MARGIN*2);
        statusText.setWrapText(true);
        statusText.setStyle("-fx-line-spacing: -0.2em;");

        Group group = new Group(statusText, animation());
        Scene scene = new Scene(group, SCENE_WIDTH, SCENE_HEIGHT);
        scene.setFill(Color.BLACK);

        progress = new Rectangle(0, SCENE_HEIGHT - PBAR_HEIGHT, 150, PBAR_HEIGHT);
        progress.setFill(Color.web("#997722"));
        group.getChildren().add(progress);

        stage.setTitle(STAGE_TITLE);
        stage.setScene(scene);
    }

    private Node animation() {
        Shape3D b = new TorusMesh(20, 3);
        b.setTranslateX(SCENE_HEIGHT / 2);
        b.setTranslateY(SCENE_HEIGHT / 2 - 6);
        b.setRotationAxis(new Point3D(0, 1, 0));

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.web("#FFDD66"));
        material.setSpecularColor(Color.web("#FFEE99"));
        material.setSpecularPower(0.3);
        b.setMaterial(material);

        PointLight light = new PointLight(Color.color(0.8, 0.8, 0.8));
        light.setTranslateX(200);
        light.setTranslateY(0);
        light.setTranslateZ(-400);
        AmbientLight ambientLight = new AmbientLight(Color.color(0.2, 0.2, 0.2));
        SubScene s2 = new SubScene(new Group(b, ambientLight, light), SCENE_HEIGHT, SCENE_HEIGHT);
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(0);
        camera.setTranslateZ(0);
        s2.setCamera(camera);
        s2.setTranslateX(0);
        s2.setTranslateY(0);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyValue kv1 = new KeyValue(b.rotationAxisProperty(), new Point3D(1, 0, 0), new Point3DInterpolator());
        KeyValue kv2 = new KeyValue(b.rotateProperty(), 360);
        KeyValue kv3 = new KeyValue(b.rotationAxisProperty(), new Point3D(0, 1, 0), new Point3DInterpolator());
        KeyFrame kf1 = new KeyFrame(Duration.millis(1000), kv1);
        KeyFrame kf2 = new KeyFrame(Duration.millis(2000), kv2, kv3);
        timeline.getKeyFrames().addAll(kf1, kf2);
        timeline.play();

        return s2;
    }

    public void show() { Platform.runLater(stage::show); }
    public void hide() { Platform.runLater(stage::hide); }
    public double getWidth() { return stage.getWidth(); }
    public double getHeight() { return stage.getHeight(); }

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
