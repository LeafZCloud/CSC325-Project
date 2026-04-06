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

public class SignUpController extends StartController{

    @FXML
    private StackPane rootPane;       // StackPane in FXML

    @FXML
    private ImageView bgImage;        // Background image

    @FXML
    private TextField emailField;  // Username input

    @FXML
    private PasswordField passwordField; // Password input

    @FXML
    private Label statusLabel;

    @FXML
    private TextField usernameField;
    // Status label

    FirebaseAuthService service = new FirebaseAuthService();
    // This method is called when FXML is loaded
    @FXML
    public void initialize() {
        // Bind background image to StackPane size (fullscreen)
        bgImage.fitWidthProperty().bind(rootPane.widthProperty());
        bgImage.fitHeightProperty().bind(rootPane.heightProperty());
    }

    // <--- This is the method your Signup button calls
    @FXML
    private void handleSignUp() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String username = usernameField.getText();

        String inputResponse = service.signUpInputValidation(email, password, username);

        if (inputResponse == null) {
            if (service.signUp(email, password, username)) {
                startForController(emailField);
                return;
            }
            statusLabel.setText("Something went wrong, during the startup");
        } else {
            statusLabel.setText(inputResponse);
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