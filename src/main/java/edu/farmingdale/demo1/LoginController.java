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
    private TextField emailField;  // Username input

    @FXML
    private PasswordField passwordField; // Password input

    @FXML
    private Label statusLabel;        // Status label

    FirebaseAuthService service = new FirebaseAuthService();
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
        String email = emailField.getText();
        String password = passwordField.getText();

        // TEMPORARY BYPASS LOGIN, can't sign up rn
        if (email.equalsIgnoreCase("Test") && password.equals("1234")) {
            statusLabel.setText("Login successful!");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
                Scene startScene = new Scene(loader.load());

                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(startScene);
                stage.setFullScreen(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return; // stops Firebase login from running
        }

        System.out.println("Login button clicked");

        // Simple login check (replace with real auth later)
        if (service.login(email, password)) {
            statusLabel.setText("Login successful!");

            try {
                // Load game screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
                Scene startScene = new Scene(loader.load());

                // Get current window (stage) and set new scene
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(startScene);
                stage.setFullScreen(true);  // optional
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML // This method sets up the back button in the sign up screen. Takes you back to login or sign up choice area
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginOrSignUp.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateAccount() {
        statusLabel.setText("Create Account clicked!");
    }
}