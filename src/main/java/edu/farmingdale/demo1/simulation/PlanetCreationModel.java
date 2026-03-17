package edu.farmingdale.demo1.simulation;

import java.util.List;

public class PlanetCreationModel {

    public static class PlanetType {
        public String id;
        public String label;
        public String description;
        public String emoji;

        public PlanetType(String id, String label, String description, String emoji) {
            this.id = id;
            this.label = label;
            this.description = description;
            this.emoji = emoji;
        }
    }


    public static final List<PlanetType> PLANET_TYPES = List.of(

            new PlanetType(
                    "terran",
                    "Terran",
                    "Earth-like, with oceans and lush land",
                    "🌍"
            ),

            new PlanetType(
                    "arid",
                    "Arid",
                    "Harsh deserts and dust storms",
                    "🪨"
            ),

            new PlanetType(
                    "oceanic",
                    "Oceanic",
                    "Mostly ocean, with scattered islands",
                    "🌊"
            ),

            new PlanetType(
                    "volcanic",
                    "Volcanic",
                    "Geologically violent, full of fire",
                    "🌋"
            )
    );

}
