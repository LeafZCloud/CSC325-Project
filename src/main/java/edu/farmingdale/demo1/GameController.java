package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import java.util.Random;

public class GameController {

    @FXML
    private Pane gamePane;

    private Random random = new Random();
    private Polygon northAmerica;  // Keep a reference

    @FXML
    public void initialize() {
        drawNorthAmerica();
    }

    private void drawNorthAmerica() {
        northAmerica = new Polygon(
                200, 100, // upper-left
                300, 80,  // top
                400, 120, // upper-right
                450, 200, // middle-right
                350, 300, // bottom-right
                200, 250, // bottom-left
                180, 150  // middle-left
        );
        northAmerica.setFill(Color.GREEN);
        northAmerica.setStroke(Color.DARKGREEN);
        northAmerica.setStrokeWidth(2);

        gamePane.getChildren().add(northAmerica);
    }

    @FXML
    private void handleSnowEffect() {
        // Turn the continent white
        if (northAmerica != null) {
            northAmerica.setFill(Color.WHITE);
        }

        // Create snow animation
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {
            Circle snowflake = new Circle(random.nextInt((int) gamePane.getWidth()), 0, 5, Color.WHITE);
            gamePane.getChildren().add(snowflake);

            // Falling animation
            Timeline fall = new Timeline(new KeyFrame(Duration.millis(50), e -> {
                snowflake.setLayoutY(snowflake.getLayoutY() + 5);
            }));
            fall.setCycleCount(100);
            fall.play();
        }));
        timeline.setCycleCount(50);
        timeline.play();
    }
}
