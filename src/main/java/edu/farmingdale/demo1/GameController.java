package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// --- IMPORT PLANET SIM ---
import edu.farmingdale.demo1.views.PlanetCreationView;
import edu.farmingdale.demo1.views.SimulationView;
import edu.farmingdale.demo1.views.SummaryView;

import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;
import edu.farmingdale.demo1.simulation.GameTypes.GameState;

public class GameController {

    @FXML
    private StackPane rootPane;

    // --------------------------------
    // START GAME (YOUR SIMULATION)
    // --------------------------------
    @FXML
    public void initialize() {

        // Start planet simulation immediately
        handlePlanetSim();
    }

    // -----------------------------
    // PLANET CREATION SCREEN
    // -----------------------------
    private void handlePlanetSim() {

        PlanetCreationView view = new PlanetCreationView();

        view.setOnSimulationStart(() -> {

            PlanetConfig config = view.getCreatedConfig();

            showSimulation(config);
        });

        rootPane.getChildren().setAll(view);
    }

    // -----------------------------
    // RUN SIMULATION
    // -----------------------------
    private void showSimulation(PlanetConfig config) {

        SimulationView view = new SimulationView(config);

        view.setOnSimulationEnd(() -> {

            GameState state = view.getState();

            showSummary(state);
        });

        rootPane.getChildren().setAll(view);
    }

    // -----------------------------
    // SUMMARY SCREEN
    // -----------------------------
    private void showSummary(GameState state) {

        SummaryView view = new SummaryView(state);

        view.setOnRestart(() -> {
            handlePlanetSim();
        });

        rootPane.getChildren().setAll(view);
    }

    // -----------------------------
    // BACK TO START MENU
    // -----------------------------
    @FXML
    private void handleBackToStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}