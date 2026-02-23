package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("player") && password.equals("1234")) {
            statusLabel.setText("Login successful!");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
                Scene startScene = new Scene(loader.load(), 600, 400);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(startScene);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }
}

