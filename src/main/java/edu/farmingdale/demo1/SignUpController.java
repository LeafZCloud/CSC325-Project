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

        // Simple signUp
        boolean makeAnAccount = service.signUp(email, password, username);
        if (makeAnAccount){
            startForController(emailField);
        }
        else{
            statusLabel.setText("This user already exists.");
        }
    }



    @FXML
    private void handleCreateAccount() {
        statusLabel.setText("Create Account clicked!");
    }
}