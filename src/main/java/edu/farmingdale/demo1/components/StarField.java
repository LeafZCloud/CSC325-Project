package edu.farmingdale.demo1.components;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarField extends Pane {

    private static class Star {
        Circle circle;
        double speed;
    }

    private final List<Star> stars = new ArrayList<>();
    private final Random random = new Random();

    public StarField(int starCount) {

        setStyle("-fx-background-color: #05050f;");

        widthProperty().addListener((obs, oldVal, newVal) -> resetStars());
        heightProperty().addListener((obs, oldVal, newVal) -> resetStars());

        for (int i = 0; i < starCount; i++) {

            Star star = new Star();
            star.circle = new Circle();
            star.circle.setFill(Color.WHITE);

            star.speed = random.nextDouble() * 0.4 + 0.05;

            stars.add(star);
            getChildren().add(star.circle);
        }

        new AnimationTimer() {

            @Override
            public void handle(long now) {

                double height = getHeight();
                double width = getWidth();

                for (Star star : stars) {

                    star.circle.setCenterY(
                            star.circle.getCenterY() + star.speed
                    );

                    if (star.circle.getCenterY() > height) {

                        star.circle.setCenterY(0);
                        star.circle.setCenterX(random.nextDouble() * width);
                    }
                }
            }

        }.start();
    }

    private void resetStars() {

        double width = getWidth();
        double height = getHeight();

        if (width == 0 || height == 0) return;

        for (Star star : stars) {

            star.circle.setRadius(random.nextDouble() * 1.5 + 0.3);
            star.circle.setCenterX(random.nextDouble() * width);
            star.circle.setCenterY(random.nextDouble() * height);
        }
    }
}