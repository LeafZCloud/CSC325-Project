package edu.farmingdale.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));

        Scene scene = new Scene(loader.load(), 1000, 700);

        // max's CSS
        scene.getStylesheets().add(
                getClass().getResource("/styles/dark-theme.css").toExternalForm()
        );

        stage.setTitle("Planet Sim");
        stage.setScene(scene);
        stage.setFullScreen(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
