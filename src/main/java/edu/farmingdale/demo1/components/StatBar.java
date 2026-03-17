package edu.farmingdale.demo1.components;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class StatBar extends VBox {

    public StatBar(String label, double value) {

        setSpacing(4);

        Label title = new Label(label + ": " + Math.round(value) + "%");

        Rectangle background = new Rectangle(200, 10);
        background.setFill(Color.web("#1e293b"));

        double width = Math.max(0, Math.min(value, 100)) * 2;

        Rectangle fill = new Rectangle(width, 10);
        fill.setFill(Color.web("#00d4ff"));

        StackPane bar = new StackPane(background, fill);

        getChildren().addAll(title, bar);
    }
}
