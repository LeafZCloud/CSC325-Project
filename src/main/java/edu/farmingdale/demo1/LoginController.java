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
    private StackPane rootPane;       // StackPane in FXML

    @FXML
    private ImageView bgImage;        // Background image

    @FXML
    private TextField usernameField;  // Username input

    @FXML
    private PasswordField passwordField; // Password input

    @FXML
    private Label statusLabel;        // Status label

    // This method is called when FXML is loaded
    @FXML
    public void initialize() {
        // Bind background image to StackPane size (fullscreen)
        bgImage.fitWidthProperty().bind(rootPane.widthProperty());
        bgImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

    // <--- This is the method your Login button calls
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple login check (replace with real auth later)
        if (username.equals("player") && password.equals("1234")) {
            statusLabel.setText("Login successful!");

            try {
                // Load start screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
                Scene startScene = new Scene(loader.load());

                // Get current window (stage) and set new scene
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(startScene);
                stage.setFullScreen(true);  // optional
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