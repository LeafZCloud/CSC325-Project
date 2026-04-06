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

        setStyle("-fx-background-color:#0f172a;");
        setAlignment(Pos.CENTER);
        setSpacing(20);

        SummaryModel.Outcome outcome = SummaryModel.getOutcome(state);

        Label emoji = new Label(outcome.emoji);
        emoji.setStyle("-fx-font-size:48px;");

        Label title = new Label(outcome.title);
        title.setStyle("-fx-font-size:28px; -fx-text-fill:#f8fafc; -fx-font-weight:bold;");

        Label desc = new Label(outcome.description);
        desc.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:16px;");

        Label events = new Label("Events triggered: " + state.eventLog.size());
        events.setStyle("-fx-text-fill:#cbd5e1; -fx-font-size:14px;");

        Button restart = new Button("Create New Planet");
        restart.setStyle("""
            -fx-background-color:#1e293b;
            -fx-text-fill:white;
            -fx-border-color:#334155;
            -fx-border-radius:8;
            -fx-background-radius:8;
            -fx-padding: 10 20;
            -fx-font-size: 14px;
        """);

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