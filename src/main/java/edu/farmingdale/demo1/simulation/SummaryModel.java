package edu.farmingdale.demo1.simulation;

import edu.farmingdale.demo1.simulation.GameTypes.GameState;
import edu.farmingdale.demo1.simulation.GameTypes.Region;

import java.util.HashMap;
import java.util.Map;

public class SummaryModel {

    public static class Outcome {

        public String emoji;
        public String title;
        public String description;

        public Outcome(String emoji, String title, String description) {
            this.emoji = emoji;
            this.title = title;
            this.description = description;
        }
    }

    public static Outcome getOutcome(GameState state) {

        double pop = state.globalStats.population;

        double score =
                state.globalStats.economicHealth * 0.4 +
                        (100 - state.globalStats.stress) * 0.4 +
                        (100 - state.globalStats.exposure) * 0.2;

        if (pop < 0.5) {
            return new Outcome(
                    "💀",
                    "Extinction",
                    "Civilisation did not survive. The planet is silent now."
            );
        }

        if (score > 75) {
            return new Outcome(
                    "🌟",
                    "Golden Civilisation",
                    "Against all odds, your world reached prosperity and peace."
            );
        }

        if (score > 55) {
            return new Outcome(
                    "🌿",
                    "Stable Society",
                    "Your planet endured its challenges and built a resilient civilisation."
            );
        }

        if (score > 35) {
            return new Outcome(
                    "⚠️",
                    "Fractured World",
                    "Your planet survived, but bears deep scars."
            );
        }

        return new Outcome(
                "🔥",
                "Age of Collapse",
                "Civilisation exists in fragments."
        );
    }

    public static int getYearsSimulated(GameState state) {
        return state.year - 2157;
    }

    public static Map<String, Integer> getCategoryCounts(GameState state) {

        Map<String, Integer> counts = new HashMap<>();

        state.eventLog.forEach(event -> {
            counts.put(
                    event.category,
                    counts.getOrDefault(event.category, 0) + 1
            );
        });

        return counts;
    }

    public static double getRegionScore(Region r) {

        return (r.economicHealth + (100 - r.stress)) / 2.0;
    }

}