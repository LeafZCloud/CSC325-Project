package edu.farmingdale.demo1.views;

import edu.farmingdale.demo1.Database.DatabaseController;
import edu.farmingdale.demo1.Database.FirebaseAuthService;
import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PlanetCreationView extends StackPane {

    private int step = 1;

    private String planetName = "";
    private String selectedType = null;
    private int continents = 4;
    private int moons = 1;

    private VBox content;

    private Runnable onSimulationStart;
    private PlanetConfig createdConfig;

    private FirebaseAuthService authService;
    private DatabaseController databaseController;
    private GameTypes.GameState loadedState;

    public GameTypes.GameState getLoadedState() {
        return loadedState;
    }

    public PlanetCreationView(FirebaseAuthService authService, DatabaseController databaseController) {

        this.authService = authService;
        this.databaseController = databaseController;

        // updated image background to match the game's interface
        ImageView background = new ImageView(
                new Image(getClass().getResource("/images/Background.png").toExternalForm())
        );
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(widthProperty());
        background.fitHeightProperty().bind(heightProperty());

        content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        getChildren().addAll(background, content);

        renderStep();
    }

    public PlanetConfig getCreatedConfig() {
        return createdConfig;
    }

    public void setOnSimulationStart(Runnable r) {
        this.onSimulationStart = r;
    }

    private void renderStep() {

        content.getChildren().clear();

        content.getChildren().add(createStepImage("CreateYourPlanet.png", 620));

        switch (step) {
            case 1 -> renderNameStep();
            case 2 -> renderTypeStep();
            case 3 -> renderContinentStep();
            case 4 -> renderMoonStep();
        }
    }

    private void renderNameStep() {
        ImageView question = createStepImage("PlanetName.png", 500);
        TextField nameField = new TextField();
        nameField.setMaxWidth(300);
        Label errorLabel = createErrorLabel();
        nameField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                errorLabel.setText("");
            }
        });

        Button next = createNextButton();
        next.setOnAction(e -> {
            planetName = nameField.getText().trim();
            if (planetName.isEmpty()) {
                errorLabel.setText("Please enter a planet name.");
                return;
            }
            step++;
            renderStep();
        });

        HBox saveSlots = new HBox(20);
        saveSlots.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 3; i++) {
            saveSlots.getChildren().add(buildSlotCard(i));
        }

        content.getChildren().addAll(question, nameField, errorLabel, next, saveSlots);
    }

    private Button createNextButton() {
        return createImageButton("Next.png", 180);
    }

    private VBox buildSlotCard(int slotNumber) {
        GameTypes.GameState state = databaseController.loadGameState(
                authService.getSaveIdToken(),
                authService.getSaveLocalIdToken(),
                slotNumber
        );

        boolean hasData = state != null;
        String planetName = hasData ? state.planet.name : "Empty";

        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(140);
        card.setPrefHeight(80);
        card.setPadding(new javafx.geometry.Insets(12));
        card.setStyle(hasData ? """
        -fx-background-color:#1e3a5f;
        -fx-background-radius:12;
        -fx-border-color:#38bdf8;
        -fx-border-radius:12;
        -fx-cursor:hand;
    """ : """
        -fx-background-color:#1a1a2e;
        -fx-background-radius:12;
        -fx-border-color:#334155;
        -fx-border-radius:12;
    """);

        Label slotLabel = new Label("Slot " + slotNumber);
        slotLabel.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:11px; -fx-font-weight:bold;");

        Label nameLabel = new Label(planetName);
        nameLabel.setStyle(hasData
                ? "-fx-text-fill:white; -fx-font-size:13px; -fx-font-weight:bold;"
                : "-fx-text-fill:#475569; -fx-font-size:12px;");

        card.getChildren().addAll(slotLabel, nameLabel);

        if (hasData) {
            GameTypes.GameState finalState = state;
            card.setOnMouseClicked(e -> {
                createdConfig = finalState.planet;
                loadedState = finalState;
                if (onSimulationStart != null) onSimulationStart.run();
            });

            card.setOnMouseEntered(e -> card.setStyle("""
            -fx-background-color:#1e4a7f;
            -fx-background-radius:12;
            -fx-border-color:#7dd3fc;
            -fx-border-radius:12;
            -fx-cursor:hand;
        """));

            card.setOnMouseExited(e -> card.setStyle("""
            -fx-background-color:#1e3a5f;
            -fx-background-radius:12;
            -fx-border-color:#38bdf8;
            -fx-border-radius:12;
            -fx-cursor:hand;
        """));
        }

        return card;
    }

    private Button loadSaveGame1() { return new Button(); }
    private Button loadSaveGame2() { return new Button(); }
    private Button loadSaveGame3() { return new Button(); }

    private void renderTypeStep() {

        ImageView question = createStepImage("TypeOfWorld.png", 520);

        HBox types = new HBox(15);
        types.setAlignment(Pos.CENTER);

        Button terran = createTypeButton("🌍 Terran", "terran");
        Button arid = createTypeButton("🏜 Arid", "arid");
        Button oceanic = createTypeButton("🌊 Oceanic", "oceanic");
        Button volcanic = createTypeButton("🌋 Volcanic", "volcanic");

        types.getChildren().addAll(terran, arid, oceanic, volcanic);

        Label errorLabel = createErrorLabel();

        Button next = createNextButton();

        next.setOnAction(e -> {
            if (selectedType == null) {
                errorLabel.setText("Please select a planet type.");
                return;
            }
            step++;
            renderStep();
        });

        HBox nav = new HBox(10, next);
        nav.setAlignment(Pos.CENTER);

        content.getChildren().addAll(question, types, errorLabel, nav);
    }

    private Button createTypeButton(String text, String type) {

        Button btn = new Button(text);

        btn.setStyle("""
            -fx-background-color:#06b6d4;
            -fx-text-fill:white;
            -fx-padding:10 20;
            -fx-background-radius:8;
        """);

        btn.setOnAction(e -> {

            selectedType = type;

            clearStepErrors();

            for (Node node : ((HBox) btn.getParent()).getChildren()) {
                node.setStyle("""
                    -fx-background-color:#06b6d4;
                    -fx-text-fill:white;
                    -fx-padding:10 20;
                    -fx-background-radius:8;
                """);
            }

            btn.setStyle("""
                -fx-background-color:#16a34a;
                -fx-text-fill:white;
                -fx-padding:10 20;
                -fx-background-radius:8;
            """);
        });

        return btn;
    }

    private void renderContinentStep() {

        ImageView question = createStepImage("HowManyContinents.png", 560);

        GridPane choices = new GridPane();
        choices.setHgap(16);
        choices.setVgap(16);
        choices.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 8; i++) {
            int value = i;
            Button choice = createImageButton(value + ".png", 110);
            choice.setOnAction(e -> {
                continents = value;
                step++;
                renderStep();
            });
            choices.add(choice, (i - 1) % 4, (i - 1) / 4);
        }

        content.getChildren().addAll(question, choices);
    }

    private void renderMoonStep() {

        ImageView question = createStepImage("HowManyMoons.png", 520);

        HBox choices = new HBox(16);
        choices.setAlignment(Pos.CENTER);

        for (int i = 0; i <= 3; i++) {
            int value = i;
            Button choice = createMoonChoiceButton(value + ".png");
            choice.setOnAction(e -> {
                moons = value;

                createdConfig = new PlanetConfig(
                        planetName,
                        continents,
                        selectedType != null ? selectedType : "terran",
                        moons
                );

                if (onSimulationStart != null) {
                    onSimulationStart.run();
                }
            });
            choices.getChildren().add(choice);
        }

        content.getChildren().addAll(question, choices);
    }



    private Button createStartButton() {
        return createImageButton("StartButton.png", 220);
    }

    private ImageView createStepImage(String imageName, double fitWidth) {
        ImageView imageView = new ImageView(
                new Image(getClass().getResource("/images/" + imageName).toExternalForm())
        );
        imageView.setFitWidth(fitWidth);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private Button createImageButton(String imageName, double fitWidth) {
        ImageView imageView = createStepImage(imageName, fitWidth);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        return button;
    }

    // Error for Planet Creation pages
    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#dc2626"));
        errorLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return errorLabel;
    }

    private void clearStepErrors() {
        for (Node node : content.getChildren()) {
            if (node instanceof Label label) {
                label.setText("");
            }
        }
    }

    private Button createMoonChoiceButton(String imageName) {
        ImageView imageView = createStepImage(imageName, 110);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");
        return button;
    }
}
