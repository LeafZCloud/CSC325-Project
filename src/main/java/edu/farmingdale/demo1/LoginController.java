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
import edu.farmingdale.demo1.simulation.GameTypes.GameState;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;
import edu.farmingdale.demo1.views.PlanetCreationView;
import edu.farmingdale.demo1.views.SimulationView;
import edu.farmingdale.demo1.views.SummaryView;

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

        // Simple login check (replace with real auth later)
        if (service.login(email, password)) {
            statusLabel.setText("Login successful!");
            goToGame();
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    @FXML
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
    private void handleTempEnterGame() {
        statusLabel.setText("Temporary bypass enabled.");
        goToGame();
    }

    private void goToGame() {
        try {
            StackPane gameRoot = new StackPane();
            showPlanetCreation(gameRoot);
            Scene startScene = new Scene(gameRoot, 1000, 700);
            String stylesheet = getClass().getResource("/styles/dark-theme.css").toExternalForm();
            startScene.getStylesheets().add(stylesheet);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(startScene);
            stage.setFullScreen(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Game load failed: " + e.getClass().getSimpleName());
        }
    }

    private void showPlanetCreation(StackPane gameRoot) {
        PlanetCreationView view = new PlanetCreationView();
        view.setOnSimulationStart(() -> {
            PlanetConfig config = view.getCreatedConfig();
            showSimulation(gameRoot, config);
        });
        gameRoot.getChildren().setAll(view);
    }

    private void showSimulation(StackPane gameRoot, PlanetConfig config) {
        SimulationView view = new SimulationView(config);
        view.setOnSimulationEnd(() -> {
            GameState state = view.getState();
            showSummary(gameRoot, state);
        });
        gameRoot.getChildren().setAll(view);
    }

    private void showSummary(StackPane gameRoot, GameState state) {
        SummaryView view = new SummaryView(state);
        view.setOnRestart(() -> showPlanetCreation(gameRoot));
        gameRoot.getChildren().setAll(view);
    }

    @FXML
    private void handleCreateAccount() {
        statusLabel.setText("Create Account clicked!");
    }
}
