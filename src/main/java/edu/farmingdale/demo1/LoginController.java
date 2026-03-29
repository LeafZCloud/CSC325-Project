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

public class LoginController extends StartController
{

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
    public void initialize()
    {
        // Bind background image to StackPane size (fullscreen)
        bgImage.fitWidthProperty().bind(rootPane.widthProperty());
        bgImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

    // <--- This is the method your Login button calls
    @FXML
    private void handleLogin()
    {
        String email = emailField.getText();
        String password = passwordField.getText();

        String inputResponse = service.loginInputValidation(email, password);

        if (inputResponse == null) {
            if (service.login(email, password)) {
                startForController(emailField);
                return;
            }
            statusLabel.setText("Something went wrong, during the startup");
        } else {
            statusLabel.setText(inputResponse);
        }

    }

        @FXML
        private void handleCreateAccount() {
        statusLabel.setText("Create Account clicked!");
    }
}