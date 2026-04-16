package edu.farmingdale.demo1.components;

import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;
import edu.farmingdale.demo1.simulation.GameTypes.Region;
import edu.farmingdale.demo1.simulation.WorldMapModel;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
    private static final double PLANET_CENTER_X = 400;
    private static final double PLANET_CENTER_Y = 400;
    private static final double PLANET_RADIUS = 340;
    private static final double HORIZONTAL_MARGIN = 48;
    private static final double TOP_MARGIN = 36;
    private static final double BOTTOM_MARGIN = 120;
    private static final double WORLD_VERTICAL_BIAS = 0.43;

    public WorldMapView(List<Region> regions, PlanetConfig config, Set<String> flashingRegions, String lastEventId) {

        setPrefSize(1500, 1500);
        Group worldGroup = new Group();

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
        Circle planet = new Circle(PLANET_CENTER_X, PLANET_CENTER_Y, PLANET_RADIUS);
        Image planetWater = new Image (getClass().getResource("/images/waterTexture2.JPG").toExternalForm());
        ImagePattern planetWaterPattern = new ImagePattern(planetWater);
            // (this is a plain color used in testing) planet.setFill(Color.web("#1c6087"));
            // (this is a water texture used in testing) planet.setFill(planetWaterPattern);
        planet.setFill(planetWaterPattern);
        planet.setStroke(Color.BLACK);
        planet.setStrokeWidth(8);
        worldGroup.getChildren().add(planet);

        addMoons(worldGroup, config);

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

            worldGroup.getChildren().add(poly);

            // Region label
            double[] centroid = WorldMapModel.getCentroid(pts);

            Text label = new Text(region.name);
            label.setFill(Color.web("#ffffff"));
            label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 18));
            label.setStroke(Color.rgb(0, 0, 0, 0.95));
            label.setStrokeType(StrokeType.OUTSIDE);
            label.setStrokeWidth(1.6);

            DropShadow labelShadow = new DropShadow();
            labelShadow.setRadius(10);
            labelShadow.setOffsetX(1.5);
            labelShadow.setOffsetY(1.5);
            labelShadow.setColor(Color.rgb(0, 0, 0, 0.85));
            label.setEffect(labelShadow);

            double textWidth = label.getLayoutBounds().getWidth();
            double textHeight = label.getLayoutBounds().getHeight();

            label.setX(centroid[0] - (textWidth / 2.0));
            label.setY(centroid[1] + (textHeight / 4.0));

            worldGroup.getChildren().add(label);
        }

        getChildren().add(worldGroup);

        widthProperty().addListener((obs, oldWidth, newWidth) -> centerWorld(worldGroup));
        heightProperty().addListener((obs, oldHeight, newHeight) -> centerWorld(worldGroup));
        layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> centerWorld(worldGroup));

        centerWorld(worldGroup);
    }

    private void addMoons(Group worldGroup, PlanetConfig config) {
        int moonCount = Math.max(0, Math.min(3, config != null ? config.moons : 0));

        double[][] moonPositions = {
                {170, 120, 42},
                {655, 155, 34},
                {710, 620, 26}
        };

        for (int i = 0; i < moonCount; i++) {
            double[] moon = moonPositions[i];

            Circle orbit = new Circle(PLANET_CENTER_X, PLANET_CENTER_Y, distanceFromPlanetCenter(moon[0], moon[1]));
            orbit.setFill(Color.TRANSPARENT);
            orbit.setStroke(Color.rgb(148, 163, 184, 0.18));
            orbit.getStrokeDashArray().addAll(8.0, 12.0);
            orbit.setStrokeWidth(1.5);

            Circle moonBody = new Circle(moon[0], moon[1], moon[2]);
            moonBody.setFill(Color.web("#d8dee9"));
            moonBody.setStroke(Color.web("#94a3b8"));
            moonBody.setStrokeWidth(2);

            Circle craterA = new Circle(moon[0] - (moon[2] * 0.22), moon[1] - (moon[2] * 0.12), moon[2] * 0.18);
            craterA.setFill(Color.rgb(148, 163, 184, 0.35));

            Circle craterB = new Circle(moon[0] + (moon[2] * 0.18), moon[1] + (moon[2] * 0.16), moon[2] * 0.12);
            craterB.setFill(Color.rgb(148, 163, 184, 0.28));

            worldGroup.getChildren().addAll(orbit, moonBody, craterA, craterB);
        }
    }

    private void centerWorld(Group worldGroup) {
        double availableWidth = getWidth();
        double availableHeight = getHeight();
        double worldWidth = worldGroup.getLayoutBounds().getWidth();
        double worldHeight = worldGroup.getLayoutBounds().getHeight();

        if (availableWidth <= 0 || availableHeight <= 0 || worldWidth <= 0 || worldHeight <= 0) {
            return;
        }

        double usableWidth = Math.max(1, availableWidth - (HORIZONTAL_MARGIN * 2));
        double usableHeight = Math.max(1, availableHeight - TOP_MARGIN - BOTTOM_MARGIN);

        double scaleX = (usableWidth * 0.82) / worldWidth;
        double scaleY = (usableHeight * 0.82) / worldHeight;
        double scale = Math.min(1.0, Math.min(scaleX, scaleY));

        worldGroup.setScaleX(scale);
        worldGroup.setScaleY(scale);

        double worldCenterX = worldGroup.getLayoutBounds().getMinX() + (worldWidth / 2.0);
        double worldCenterY = worldGroup.getLayoutBounds().getMinY() + (worldHeight / 2.0);
        double targetCenterX = availableWidth / 2.0;
        double targetCenterY = TOP_MARGIN + (usableHeight * WORLD_VERTICAL_BIAS);

        worldGroup.setLayoutX(targetCenterX - (worldCenterX * scale));
        worldGroup.setLayoutY(targetCenterY - (worldCenterY * scale));
    }

    private double distanceFromPlanetCenter(double x, double y) {
        double dx = x - PLANET_CENTER_X;
        double dy = y - PLANET_CENTER_Y;
        return Math.sqrt((dx * dx) + (dy * dy));
    }
}
