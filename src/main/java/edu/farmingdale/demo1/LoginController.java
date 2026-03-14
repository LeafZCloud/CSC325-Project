package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView bgImage;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {

        // Make background fill window
        bgImage.fitWidthProperty().bind(rootPane.widthProperty());
        bgImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

    // Login button
    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        // Temporary test login
        if (username.equals("player") && password.equals("1234")) {

            statusLabel.setText("Login successful!");

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
                Scene gameScene = new Scene(loader.load());

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(gameScene);
                stage.setFullScreen(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleCreateAccount() {
        statusLabel.setText("Create Account clicked!");
    }
}