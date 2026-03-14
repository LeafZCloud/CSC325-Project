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
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.Random;

public class GameController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Pane gamePane;

    private Random random = new Random();
    private Polygon northAmerica;

    private Image normalForest;
    private Image snowForest;

    @FXML
    public void initialize() {

        // Make gamePane resize with window (fullscreen safe)
        gamePane.prefWidthProperty().bind(rootPane.widthProperty());
        gamePane.prefHeightProperty().bind(rootPane.heightProperty());

        drawNorthAmerica();

        normalForest = new Image(getClass().getResource("/images/forestBiomeNormal.png").toExternalForm());
        snowForest = new Image(getClass().getResource("/images/forestBiomeSnow.png").toExternalForm());

        northAmerica.setFill(new ImagePattern(normalForest));
    }

    private void drawNorthAmerica() {

        northAmerica = new Polygon(
                400, 200,
                600, 160,
                800, 240,
                900, 400,
                700, 600,
                400, 500,
                360, 300
        );

        northAmerica.setFill(Color.GREEN);

        gamePane.getChildren().add(northAmerica);
    }

    @FXML
    private void handleSnowEffect() {

        if (northAmerica != null) {
            northAmerica.setFill(new ImagePattern(snowForest));
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