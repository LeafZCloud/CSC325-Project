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
    private static final double BOTTOM_MARGIN = 36;
    private static final double WORLD_FILL = 0.9;
    private static final double MAX_WORLD_SCALE = 1.08;

    private static Image load(String path) {
        return new Image(WorldMapView.class.getResource(path).toExternalForm());
    }

    private static final Image PLANET_WATER = load("/images/waterTexture2.JPG");

    private static final Image FOREST_NORMAL = load("/images/forestBiomeImages/forestBiomeNormal.PNG");
    private static final Image FOREST_BLIZZ = load("/images/forestBiomeImages/blizzardEventForest.PNG");
    private static final Image FOREST_METEOR = load("/images/forestBiomeImages/meteorEventForest.PNG");
    private static final Image FOREST_QUAKE = load("/images/forestBiomeImages/earthquakeEventForest.PNG");
    private static final Image FOREST_INDUST = load("/images/forestBiomeImages/industrializationEventForest.PNG");
    private static final Image FOREST_VOLCANIC = load("/images/forestBiomeImages/volcanicerruptionEventForest.PNG");
    private static final Image FOREST_DROUGHT = load("/images/forestBiomeImages/droughtEventForest.PNG");
    private static final Image FOREST_PLAGUE = load("/images/forestBiomeImages/plagueEventForest.PNG");
    private static final Image FOREST_NUKE = load("/images/forestBiomeImages/nukeEventForest.PNG");
    private static final Image FOREST_WAR = load("/images/forestBiomeImages/worldWarEventForest.PNG");
    private static final Image FOREST_MEDICAL = load("/images/forestBiomeImages/medicalBreakthroughEventForest.PNG");
    private static final Image FOREST_GOLDEN = load("/images/forestBiomeImages/goldenAgeEventForest.PNG");

    private static final ImagePattern FOREST_NORMAL_PATTERN = new ImagePattern(FOREST_NORMAL);
    private static final ImagePattern FOREST_BLIZZ_PATTERN = new ImagePattern(FOREST_BLIZZ);
    private static final ImagePattern FOREST_METEOR_PATTERN = new ImagePattern(FOREST_METEOR);
    private static final ImagePattern FOREST_QUAKE_PATTERN = new ImagePattern(FOREST_QUAKE);
    private static final ImagePattern FOREST_INDUST_PATTERN = new ImagePattern(FOREST_INDUST);
    private static final ImagePattern FOREST_VOLCANIC_PATTERN = new ImagePattern(FOREST_VOLCANIC);
    private static final ImagePattern FOREST_DROUGHT_PATTERN = new ImagePattern(FOREST_DROUGHT);
    private static final ImagePattern FOREST_PLAGUE_PATTERN = new ImagePattern(FOREST_PLAGUE);
    private static final ImagePattern FOREST_NUKE_PATTERN = new ImagePattern(FOREST_NUKE);
    private static final ImagePattern FOREST_WAR_PATTERN = new ImagePattern(FOREST_WAR);
    private static final ImagePattern FOREST_MEDICAL_PATTERN = new ImagePattern(FOREST_MEDICAL);
    private static final ImagePattern FOREST_GOLDEN_PATTERN = new ImagePattern(FOREST_GOLDEN);

    public WorldMapView(List<Region> regions, PlanetConfig config, Set<String> flashingRegions, String lastEventId) {

        setPrefSize(1500, 1500);
        Group worldGroup = new Group();

        List<WorldMapModel.Star> stars = WorldMapModel.generateStars(42);

        for (WorldMapModel.Star s : stars) {
            Circle star = new Circle(s.x, s.y, s.radius);
            star.setFill(Color.WHITE);
            star.setOpacity(s.opacity);

            Timeline twinkle = new Timeline(
                    new KeyFrame(Duration.seconds(0), e -> star.setOpacity(s.opacity)),
                    new KeyFrame(Duration.seconds(.2), e -> star.setOpacity(s.opacity * (0.5 + Math.random())))
            );

            twinkle.setCycleCount(Animation.INDEFINITE);
            twinkle.setAutoReverse(true);
            twinkle.play();

            getChildren().add(star);
        }

        Circle planet = new Circle(PLANET_CENTER_X, PLANET_CENTER_Y, PLANET_RADIUS);
        planet.setFill(new ImagePattern(PLANET_WATER));
        planet.setStroke(Color.BLACK);
        planet.setStrokeWidth(8);
        worldGroup.getChildren().add(planet);

        addMoons(worldGroup, config);

        for (Region region : regions) {

            String pts = GameTypes.CONTINENT_POLYGONS[region.polygonIndex];

            Polygon poly = new Polygon();

            for (String pair : pts.split(" ")) {
                String[] xy = pair.split(",");
                poly.getPoints().addAll(
                        Double.parseDouble(xy[0]),
                        Double.parseDouble(xy[1])
                );
            }

            boolean affected = flashingRegions.contains(region.id);

            poly.setFill(FOREST_NORMAL_PATTERN);

            if (affected) {

                switch (lastEventId) {

                    case "ice_age" -> poly.setFill(FOREST_BLIZZ_PATTERN);

                    case "earthquakes" -> {
                        poly.setFill(FOREST_QUAKE_PATTERN);
                        shakePolygon(poly);
                    }

                    case "meteor" -> {
                        poly.setFill(FOREST_METEOR_PATTERN);
                        shakePolygon(poly);
                    }

                    case "industrial_revolution" ->
                            poly.setFill(FOREST_INDUST_PATTERN);

                    case "volcanic_eruptions" -> {
                        poly.setFill(FOREST_VOLCANIC_PATTERN);
                        shakePolygon(poly);
                    }

                    case "drought" ->
                            poly.setFill(FOREST_DROUGHT_PATTERN);

                    case "plague" ->
                            poly.setFill(FOREST_PLAGUE_PATTERN);

                    case "nuke" -> {
                        poly.setFill(FOREST_NUKE_PATTERN);
                        shakePolygon(poly);
                    }

                    case "world_war" -> {
                        poly.setFill(FOREST_WAR_PATTERN);
                        shakePolygon(poly);
                    }

                    case "medical_breakthrough" ->
                            poly.setFill(FOREST_MEDICAL_PATTERN);

                    case "golden_age" ->
                            poly.setFill(FOREST_GOLDEN_PATTERN);
                }
            }

            poly.setStroke(Color.BLACK);
            poly.setStrokeWidth(2);
            poly.setOpacity(0.85);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(18);
            shadow.setOffsetX(5);
            shadow.setOffsetY(5);
            shadow.setColor(Color.rgb(0, 0, 0, 0.6));
            poly.setEffect(shadow);

            worldGroup.getChildren().add(poly);

            double[] centroid = WorldMapModel.getCentroid(pts);

            Text label = new Text(region.name);
            label.setFill(Color.WHITE);
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

    private void shakePolygon(Polygon poly) {
        Timeline shake = new Timeline(
                new KeyFrame(Duration.millis(0), e -> poly.setTranslateX(0)),
                new KeyFrame(Duration.millis(25), e -> poly.setTranslateX(-5)),
                new KeyFrame(Duration.millis(50), e -> poly.setTranslateX(5)),
                new KeyFrame(Duration.millis(75), e -> poly.setTranslateX(-5)),
                new KeyFrame(Duration.millis(100), e -> poly.setTranslateX(5))
        );

        shake.setCycleCount(8); // faster total duration
        shake.setAutoReverse(false);

        shake.setOnFinished(e -> poly.setTranslateX(0));

        shake.play();
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

        double scaleX = (usableWidth * WORLD_FILL) / worldWidth;
        double scaleY = (usableHeight * WORLD_FILL) / worldHeight;
        double scale = Math.min(MAX_WORLD_SCALE, Math.min(scaleX, scaleY));

        worldGroup.setScaleX(scale);
        worldGroup.setScaleY(scale);

        double worldCenterX = worldGroup.getLayoutBounds().getMinX() + (worldWidth / 2.0);
        double worldCenterY = worldGroup.getLayoutBounds().getMinY() + (worldHeight / 2.0);
        double targetCenterX = availableWidth / 2.0;
        double targetCenterY = availableHeight / 2.0;

        double scaledPlanetCenterX = worldCenterX + ((PLANET_CENTER_X - worldCenterX) * scale);
        double scaledPlanetCenterY = worldCenterY + ((PLANET_CENTER_Y - worldCenterY) * scale);

        worldGroup.setLayoutX(targetCenterX - scaledPlanetCenterX);
        worldGroup.setLayoutY(targetCenterY - scaledPlanetCenterY);
    }

    private double distanceFromPlanetCenter(double x, double y) {
        double dx = x - PLANET_CENTER_X;
        double dy = y - PLANET_CENTER_Y;
        return Math.sqrt((dx * dx) + (dy * dy));
    }
}
