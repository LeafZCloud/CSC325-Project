package edu.farmingdale.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(loader.load(), visualBounds.getWidth(), visualBounds.getHeight());

        // max's CSS
        scene.getStylesheets().add(
                getClass().getResource("/styles/dark-theme.css").toExternalForm()
        );

        stage.setTitle("Planet Sim");
        stage.setScene(scene);
        fitStageToScreen(stage);
        stage.show();
    }

    public static void fitStageToScreen(Stage stage) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.setX(visualBounds.getMinX());
        stage.setY(visualBounds.getMinY());
        stage.setWidth(visualBounds.getWidth());
        stage.setHeight(visualBounds.getHeight());
    }

    public static void main(String[] args) {
        launch();
    }
}
