package edu.farmingdale.demo1.views;

import edu.farmingdale.demo1.components.StarField;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
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

        StarField stars = new StarField(1000);

        content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        getChildren().addAll(stars, content);

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

        Label title = new Label("Create Your Planet");
        title.setStyle("-fx-text-fill:white; -fx-font-size:24;");

        content.getChildren().add(title);

        switch (step) {
            case 1 -> renderNameStep();
            case 2 -> renderTypeStep();
            case 3 -> renderContinentStep();
            case 4 -> renderMoonStep();
        }
    }

    private void renderNameStep() {

        Label question = new Label("What is your planet called?");
        question.setStyle("-fx-text-fill:white;");

        TextField nameField = new TextField();
        nameField.setMaxWidth(300);

        Button next = new Button("Next");
        next.setStyle("-fx-background-color:#06b6d4; -fx-text-fill:white;");

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

        Label question = new Label("What type of world is it?");
        question.setStyle("-fx-text-fill:white;");

        HBox types = new HBox(15);
        types.setAlignment(Pos.CENTER);

        Button terran = createTypeButton("🌍 Terran", "terran");
        Button arid = createTypeButton("🏜 Arid", "arid");
        Button oceanic = createTypeButton("🌊 Oceanic", "oceanic");
        Button volcanic = createTypeButton("🌋 Volcanic", "volcanic");

        types.getChildren().addAll(terran, arid, oceanic, volcanic);

        Button back = new Button("Back");
        Button next = new Button("Next");

        back.setOnAction(e -> {
            step--;
            renderStep();
        });

        next.setOnAction(e -> {
            step++;
            renderStep();
        });

        HBox nav = new HBox(10, back, next);
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

        Label question = new Label("How many continents?");
        question.setStyle("-fx-text-fill:white;");

        Spinner<Integer> spinner = new Spinner<>(2, 8, continents);

        Button back = new Button("Back");
        Button next = new Button("Next");

        back.setOnAction(e -> {
            step--;
            renderStep();
        });

        next.setOnAction(e -> {

            continents = spinner.getValue();

            step++;
            renderStep();
        });

        HBox nav = new HBox(10, back, next);
        nav.setAlignment(Pos.CENTER);

        content.getChildren().addAll(question, spinner, nav);
    }

    private void renderMoonStep() {

        Label question = new Label("How many moons orbit your planet?");
        question.setStyle("-fx-text-fill:white;");

        Spinner<Integer> spinner = new Spinner<>(0, 3, moons);

        Button back = new Button("Back");
        Button start = new Button("Begin Simulation");

        back.setOnAction(e -> {
            step--;
            renderStep();
        });

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

        HBox nav = new HBox(10, back, start);
        nav.setAlignment(Pos.CENTER);

        content.getChildren().addAll(question, spinner, nav);
    }
}