package edu.farmingdale.demo1.views;

import edu.farmingdale.demo1.simulation.GameTypes.GameState;
import edu.farmingdale.demo1.simulation.SummaryModel;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SummaryView extends VBox {

    private Runnable onRestart;

    public SummaryView(GameState state) {

        setAlignment(Pos.CENTER);
        setSpacing(20);

        SummaryModel.Outcome outcome = SummaryModel.getOutcome(state);

        Label emoji = new Label(outcome.emoji);
        emoji.setStyle("-fx-font-size:48px;");

        Label title = new Label(outcome.title);
        title.setStyle("-fx-font-size:20px; -fx-text-fill:white;");

        Label desc = new Label(outcome.description);
        desc.setStyle("-fx-text-fill:white;");

        Label events = new Label("Events triggered: " + state.eventLog.size());
        events.setStyle("-fx-text-fill:white;");

        Button restart = new Button("Create New Planet");

        restart.setOnAction(e -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });

        getChildren().addAll(
                emoji,
                title,
                desc,
                events,
                restart
        );
    }

    public void setOnRestart(Runnable r) {
        this.onRestart = r;
    }
}