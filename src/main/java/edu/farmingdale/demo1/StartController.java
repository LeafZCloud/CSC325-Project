package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class StartController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView bgImage;

    @FXML
    public void initialize() {
        // Make background fill the screen
        bgImage.fitWidthProperty().bind(rootPane.widthProperty());
        bgImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

    public void startForController(TextField email)
    {
        try {
            // Load game screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Scene startScene = new Scene(loader.load());

            // Get current window (stage) and set new scene
            Stage stage = (Stage)email.getScene().getWindow();
            stage.setScene(startScene);
            stage.setFullScreen(true);  // optional
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStartGame() { // Actually starts the login or sign up screen now
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginOrSignUp.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(false);   // keeps fullscreen

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewGame() {
        System.out.println("New Game clicked!");
    }

    @FXML
    private void handleSettings() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText(null);
        alert.setContentText("Settings menu will be implemented here.");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}