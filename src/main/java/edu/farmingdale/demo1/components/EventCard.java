package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes.GameEventDef;
import javafx.scene.control.Button;

public class EventCard extends Button {

    public EventCard(GameEventDef event) {

        setText(event.emoji + " " + event.name + "\n" + event.description);
        setWrapText(true);

        setStyle("""
            -fx-background-color:#1e293b;
            -fx-text-fill:white;
            -fx-border-color:#334155;
            -fx-border-radius:8;
            -fx-background-radius:8;
            -fx-padding:12;
            -fx-font-size:12px;
            -fx-alignment:top-left;
        """);

        setPrefSize(180, 92);
    }
}
