package edu.farmingdale.demo1.views;

import edu.farmingdale.demo1.simulation.GameTypes.GameState;
import edu.farmingdale.demo1.simulation.SummaryModel;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SummaryView extends StackPane {

    private Runnable onRestart;

    public SummaryView(GameState state) {

        ImageView background = new ImageView(
                new Image(getClass().getResource("/images/EndPlanetBG.png").toExternalForm())
        );
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(widthProperty());
        background.fitHeightProperty().bind(heightProperty());

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(700);

        SummaryModel.Outcome outcome = SummaryModel.getOutcome(state);

        Label emoji = new Label(outcome.emoji);
        emoji.setStyle("-fx-font-size:48px;");

        Label title = new Label(outcome.title);
        title.setStyle("-fx-font-size:28px; -fx-text-fill:#f8fafc; -fx-font-weight:bold;");

        Label desc = new Label(outcome.description);
        desc.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:16px;");

        Label events = new Label("Events triggered: " + state.eventLog.size());
        events.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:14px;");

        ImageView restartImage = new ImageView(
                new Image(getClass().getResource("/images/NewPlanetButton.png").toExternalForm())
        );
        restartImage.setFitWidth(320);
        restartImage.setPreserveRatio(true);

        Button restart = new Button();
        restart.setGraphic(restartImage);
        restart.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        restart.setOnAction(e -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });

        content.getChildren().addAll(
                emoji,
                title,
                desc,
                events,
                restart
        );

        getChildren().addAll(background, content);
    }

    public void setOnRestart(Runnable r) {
        this.onRestart = r;
    }
}
