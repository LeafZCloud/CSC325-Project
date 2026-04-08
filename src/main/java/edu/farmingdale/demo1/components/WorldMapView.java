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

public class WorldMapView extends Pane {

    public WorldMapView(List<Region> regions, PlanetConfig config, Set<String> flashingRegions, String lastEventId) {

        setPrefSize(400, 400);

        // Stars
        List<WorldMapModel.Star> stars = WorldMapModel.generateStars(42);

        for (WorldMapModel.Star s : stars) {
            Circle star = new Circle(s.x, s.y, s.radius);
            star.setFill(Color.WHITE);
            star.setOpacity(s.opacity);
            getChildren().add(star);
        }

        // Planet base
        Circle planet = new Circle(200, 200, 170);
        planet.setFill(Color.DARKSLATEBLUE);
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
            Image forestQuakeImage = new Image (getClass().getResource("/images/forestBiomeImages/meteorEventForest.PNG").toExternalForm());

            //creating the image patterns
            ImagePattern forestNormPattern = new ImagePattern(forestNormImage);
            ImagePattern forestBlizzPattern = new ImagePattern(forestBlizzImage);
            ImagePattern forestQuakeattern = new ImagePattern(forestQuakeImage);

            //application of forestNorm
            poly.setFill(forestNormPattern);


            Color color = Color.web(GameTypes.regionHealthColor(region));

            if ("ice_age".equals(lastEventId)) {
                poly.setFill(forestBlizzPattern);
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