package edu.farmingdale.demo1.simulation;

import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;
import edu.farmingdale.demo1.simulation.GameTypes.Region;

import java.util.ArrayList;
import java.util.List;

public class WorldMapModel {

    public static class Star {
        public double x;
        public double y;
        public double radius;
        public double opacity;

        public Star(double x, double y, double radius, double opacity) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.opacity = opacity;
        }
    }

    public static final int STAR_COUNT = 400;

    public static List<Star> generateStars(int seed) {

        List<Star> stars = new ArrayList<>();
        long s = seed;

        for (int i = 0; i < STAR_COUNT; i++) {

            s = (s * 1664525 + 1013904223) & 0xffffffff;
            double x = Math.abs(s % 1500);

            s = (s * 1664525 + 1013904223) & 0xffffffff;
            double y = Math.abs(s % 1500);

            s = (s * 1664525 + 1013904223) & 0xffffffff;
            double r = (Math.abs(s % 100) / 100.0) * 1.5 + 0.3;

            s = (s * 1664525 + 1013904223) & 0xffffffff;
            double opacity = (Math.abs(s % 100) / 100.0) * 0.7 + 0.3;

            stars.add(new Star(x, y, r, opacity));
        }

        return stars;
    }

    public static double[] getCentroid(String points) {

        String[] pairs = points.trim().split(" ");

        double sumX = 0;
        double sumY = 0;

        for (String pair : pairs) {

            String[] xy = pair.split(",");

            double x = Double.parseDouble(xy[0]);
            double y = Double.parseDouble(xy[1]);

            sumX += x;
            sumY += y;
        }

        double cx = sumX / pairs.length;
        double cy = sumY / pairs.length;

        return new double[]{cx, cy};
    }

    public static String getRegionColor(Region region, boolean flashing) {

        if (flashing) {
            return "#ff2222";
        }

        return GameTypes.regionHealthColor(region);
    }

    public static int getMoonCount(PlanetConfig config) {
        return config.moons;
    }

}