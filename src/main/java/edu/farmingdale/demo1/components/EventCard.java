package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes.GameEventDef;
import javafx.scene.control.Button;

public class EventCard extends Button {

    public EventCard(GameEventDef event) {

        setText(event.emoji + " " + event.name);

        setStyle("""
            -fx-background-color:#1e293b;
            -fx-text-fill:white;
            -fx-border-color:#334155;
            -fx-border-radius:8;
            -fx-background-radius:8;
        """);

        setPrefSize(120, 80);
    }
}