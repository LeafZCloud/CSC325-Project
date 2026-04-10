package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;
import edu.farmingdale.demo1.simulation.GameTypes.Region;
import edu.farmingdale.demo1.simulation.WorldMapModel;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Set;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.util.Duration;

import javafx.scene.effect.DropShadow;


public class WorldMapView extends Pane {

    public WorldMapView(List<Region> regions, PlanetConfig config, Set<String> flashingRegions, String lastEventId) {

        setPrefSize(1500, 1500);

        // Stars
        List<WorldMapModel.Star> stars = WorldMapModel.generateStars(42);

        for (WorldMapModel.Star s : stars) {
            Circle star = new Circle(s.x, s.y, s.radius);
            star.setFill(Color.WHITE);

            // base opacity
            star.setOpacity(s.opacity);

            Timeline twinkle = new Timeline(
                    new KeyFrame(Duration.seconds(0), e -> {
                        star.setOpacity(s.opacity);
                    }),
                    new KeyFrame(Duration.seconds(.2), e -> {
                        star.setOpacity(s.opacity * (0.5 + Math.random()));
                    })
            );

            twinkle.setCycleCount(Animation.INDEFINITE);
            twinkle.setAutoReverse(true);
            twinkle.play();

            getChildren().add(star);
        }

        // Planet base
        Circle planet = new Circle(400, 400, 340);
        Image planetWater = new Image (getClass().getResource("/images/waterTexture2.JPG").toExternalForm());
        ImagePattern planetWaterPattern = new ImagePattern(planetWater);
            // (this is a plain color used in testing) planet.setFill(Color.web("#1c6087"));
            // (this is a water texture used in testing) planet.setFill(planetWaterPattern);
        planet.setFill(planetWaterPattern);
        planet.setStroke(Color.BLACK);
        planet.setStrokeWidth(8);
        getChildren().add(planet);

        // Continents / Regions
        for (Region region : regions) {

            String pts = GameTypes.CONTINENT_POLYGONS[region.polygonIndex];

            Polygon poly = new Polygon();

            String[] pairs = pts.split(" ");

            for (String pair : pairs) {
                String[] xy = pair.split(",");
                poly.getPoints().addAll(
                        Double.parseDouble(xy[0]),
                        Double.parseDouble(xy[1])
                );
            }

            //loading the images
            Image forestNormImage = new Image (getClass().getResource("/images/forestBiomeImages/forestBiomeNormal.PNG").toExternalForm());
            Image forestBlizzImage = new Image (getClass().getResource("/images/forestBiomeImages/blizzardEventForest.PNG").toExternalForm());
            Image forestMeteorImage = new Image (getClass().getResource("/images/forestBiomeImages/meteorEventForest.PNG").toExternalForm());

            //creating the image patterns
            ImagePattern forestNormPattern = new ImagePattern(forestNormImage);
            ImagePattern forestBlizzPattern = new ImagePattern(forestBlizzImage);
            ImagePattern forestMeteorPattern = new ImagePattern(forestMeteorImage);

            //application of forestNorm
            poly.setFill(forestNormPattern);
            // (used in testing dark green vs. black) poly.setStroke(Color.web("#123808"));
            poly.setStroke(Color.BLACK);
            poly.setStrokeWidth(2);

            //shadow for the continents
            DropShadow shadow = new DropShadow();
            shadow.setRadius(18);
            shadow.setOffsetX(5);
            shadow.setOffsetY(5);
            shadow.setColor(Color.rgb(0, 0, 0, 0.6));

            poly.setEffect(shadow);


            Color color = Color.web(GameTypes.regionHealthColor(region));

            if ("ice_age".equals(lastEventId)) {
                poly.setFill(forestBlizzPattern);
                poly.setStroke(Color.WHITE);
            } else if (flashingRegions.contains(region.id)) {
                color = Color.RED;
            }

            if ("meteor".equals(lastEventId)) {
                poly.setFill(forestMeteorPattern);
                poly.setStroke(Color.RED);
            } else if (flashingRegions.contains(region.id)) {
                color = Color.RED;
            }

            poly.setOpacity(0.85);

            getChildren().add(poly);

            // Region label
            double[] centroid = WorldMapModel.getCentroid(pts);

            Text label = new Text(centroid[0], centroid[1], region.name);
            label.setFill(Color.WHITE);
            label.setStyle("-fx-font-size: 8px;");

            getChildren().add(label);
        }
    }
}