package edu.farmingdale.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Random;

public class GameController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Pane gamePane;

    private Random random = new Random();
    private Polygon northAmerica;

    @FXML
    public void initialize() {

        // Make gamePane resize with window
        gamePane.prefWidthProperty().bind(rootPane.widthProperty());
        gamePane.prefHeightProperty().bind(rootPane.heightProperty());

        drawNorthAmerica();
    }

    private void drawNorthAmerica() {

        northAmerica = new Polygon(
                200, 100,
                300, 80,
                400, 120,
                450, 200,
                350, 300,
                200, 250,
                180, 150
        );

        northAmerica.setFill(Color.GREEN);
        northAmerica.setStroke(Color.DARKGREEN);
        northAmerica.setStrokeWidth(2);

        gamePane.getChildren().add(northAmerica);
    }

    @FXML
    private void handleSnowEffect() {

        if (northAmerica != null) {
            northAmerica.setFill(Color.WHITE);
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), event -> {

            Circle snowflake = new Circle(
                    random.nextInt((int) gamePane.getWidth()),
                    0,
                    5,
                    Color.WHITE
            );

            gamePane.getChildren().add(snowflake);

            Timeline fall = new Timeline(new KeyFrame(Duration.millis(50), e ->
                    snowflake.setLayoutY(snowflake.getLayoutY() + 5)
            ));

            fall.setCycleCount(100);
            fall.play();

        }));

        timeline.setCycleCount(50);
        timeline.play();
    }

    @FXML
    private void handleBackToStart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/start.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}