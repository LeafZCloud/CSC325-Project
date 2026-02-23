package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.Stage;


public class StartController {

    @FXML
    private void handleStartGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
            Scene gameScene = new Scene(loader.load(), 800, 600);

            // Get the current stage from the event source
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(gameScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
