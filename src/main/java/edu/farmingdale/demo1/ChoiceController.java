package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ChoiceController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView bgImage;

    @FXML
    public void initialize() {
        bgImage.fitWidthProperty().bind(rootPane.widthProperty());
        bgImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

    // This method runs when the user clicks the "Login" button
    @FXML
    private void handleLoginChoice() {
        try {
            // load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}