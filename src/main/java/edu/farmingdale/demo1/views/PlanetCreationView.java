package edu.farmingdale.demo1.views;

import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PlanetCreationView extends StackPane {

    private int step = 1;

    private String planetName = "";
    private String selectedType = null;
    private int continents = 4;
    private int moons = 1;

    private VBox content;

    private Runnable onSimulationStart;
    private PlanetConfig createdConfig;

    public PlanetCreationView() {

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

        Button next = createNextButton();

        next.setOnAction(e -> {

            planetName = nameField.getText();

            if (planetName.isEmpty()) {
                planetName = "Unnamed";
            }

            step++;
            renderStep();
        });

        content.getChildren().addAll(question, nameField, next);
    }

    private void renderTypeStep() {

        ImageView question = createStepImage("TypeOfWorld.png", 520);

        HBox types = new HBox(15);
        types.setAlignment(Pos.CENTER);

        Button terran = createTypeButton("🌍 Terran", "terran");
        Button arid = createTypeButton("🏜 Arid", "arid");
        Button oceanic = createTypeButton("🌊 Oceanic", "oceanic");
        Button volcanic = createTypeButton("🌋 Volcanic", "volcanic");

        types.getChildren().addAll(terran, arid, oceanic, volcanic);

        Button next = createNextButton();

        next.setOnAction(e -> {
            step++;
            renderStep();
        });

        HBox nav = new HBox(10, next);
        nav.setAlignment(Pos.CENTER);

        content.getChildren().addAll(question, types, nav);
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

        Spinner<Integer> spinner = new Spinner<>(0, 3, moons);

        Button start = createStartButton();

        start.setOnAction(e -> {

            moons = spinner.getValue();

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

        HBox nav = new HBox(10, start);
        nav.setAlignment(Pos.CENTER);

        content.getChildren().addAll(question, spinner, nav);
    }

    // Next Button UI Update
    private Button createNextButton() {
        return createImageButton("Next.png", 180);
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
}
