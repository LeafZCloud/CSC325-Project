package edu.farmingdale.demo1.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class StatBar extends VBox {

    public StatBar(String label, double value) {

        setSpacing(6);
        setMaxWidth(300);

        String displayValue;
        if (label.equalsIgnoreCase("Population")) {
            displayValue = String.format("%.1fB", value);
        } else {
            displayValue = Math.round(value) + "%";
        }

        // Header: Label (left) and Value (right)
        HBox header = new HBox();
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-text-fill:#94a3b8; -fx-font-size:13px;");

        Label valueLabel = new Label(displayValue);
        valueLabel.setStyle("-fx-text-fill:#ef4444; -fx-font-size:13px; -fx-font-weight:bold;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleLabel, spacer, valueLabel);

        // Bar
        Rectangle background = new Rectangle(300, 8);
        background.setArcHeight(8);
        background.setArcWidth(8);
        background.setFill(Color.web("#1e293b"));

        double percentage;
        if (label.equalsIgnoreCase("Population")) {
            // Assume 10B is 100% for the bar
            percentage = Math.max(0, Math.min(value * 10, 100));
        } else {
            percentage = Math.max(0, Math.min(value, 100));
        }

        double width = percentage * 3.0; // 300px max

        Rectangle fill = new Rectangle(width, 8);
        fill.setArcHeight(8);
        fill.setArcWidth(8);

        // Color logic based on value/severity
        String barColor = "#3b82f6"; // Blue default
        if (label.equalsIgnoreCase("Stress") || label.equalsIgnoreCase("Exposure to Events")) {
            if (percentage > 75) barColor = "#ef4444"; // Red
            else if (percentage > 45) barColor = "#f59e0b"; // Orange/Yellow
        } else if (label.equalsIgnoreCase("Economic Health")) {
            if (percentage < 35) barColor = "#ef4444"; // Red
            else if (percentage < 60) barColor = "#f59e0b"; // Orange/Yellow
            else barColor = "#22c55e"; // Green
        } else if (label.equalsIgnoreCase("Population")) {
            barColor = "#ef4444"; // Red as in screenshot
        }

        fill.setFill(Color.web(barColor));

        StackPane bar = new StackPane(background, fill);
        bar.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(header, bar);
    }
}
