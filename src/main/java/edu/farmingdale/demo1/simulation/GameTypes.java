package edu.farmingdale.demo1.simulation;

import java.util.*;


public class GameTypes {

    /* -------------------------
       DATA MODELS
    ------------------------- */

    public static class Region {
        public String id;
        public String name;
        public double populationShare;
        public int stress;
        public int economicHealth;
        public int exposure;
        public int polygonIndex;

        public Region(String id, String name, double populationShare, int stress,
                      int economicHealth, int exposure, int polygonIndex) {
            this.id = id;
            this.name = name;
            this.populationShare = populationShare;
            this.stress = stress;
            this.economicHealth = economicHealth;
            this.exposure = exposure;
            this.polygonIndex = polygonIndex;
        }
    }

    public static class GlobalStats {
        public double population;
        public int stress;
        public int economicHealth;
        public int exposure;

        public GlobalStats(double population, int stress, int economicHealth, int exposure) {
            this.population = population;
            this.stress = stress;
            this.economicHealth = economicHealth;
            this.exposure = exposure;
        }
    }

    public static class PlanetConfig {
        public String name;
        public int continents;
        public String type;
        public int moons;

        public PlanetConfig(String name, int continents, String type, int moons) {
            this.name = name;
            this.continents = continents;
            this.type = type;
            this.moons = moons;
        }
    }

    public static class EventEffect {
        public double population;
        public int stress;
        public int economicHealth;
        public int exposure;

        public EventEffect(double population, int stress, int economicHealth, int exposure) {
            this.population = population;
            this.stress = stress;
            this.economicHealth = economicHealth;
            this.exposure = exposure;
        }
    }

    /* -------------------------
       GAME EVENT DEFINITION
    ------------------------- */

    public static class GameEventDef {

        public String id;
        public String name;
        public String category;
        public String emoji;
        public String description;
        public String flavour;

        public EventEffect globalEffects;
        public EventEffect regionEffects;

        public String affectedRegions;
        public int numRegionsAffected;

        public List<FeedPost> feedTemplates;

        public int cooldown;

        public GameEventDef(String id, String name, String category, String emoji,
                            String description, String flavour,
                            EventEffect globalEffects, EventEffect regionEffects,
                            String affectedRegions, int numRegionsAffected,
                            List<FeedPost> feedTemplates, int cooldown) {

            this.id = id;
            this.name = name;
            this.category = category;
            this.emoji = emoji;
            this.description = description;
            this.flavour = flavour;
            this.globalEffects = globalEffects;
            this.regionEffects = regionEffects;
            this.affectedRegions = affectedRegions;
            this.numRegionsAffected = numRegionsAffected;
            this.feedTemplates = feedTemplates;
            this.cooldown = cooldown;
        }
    }

    public static class FeedPost {
        public String id;
        public String username;
        public String handle;
        public String content;
        public String timeAgo;
        public int likes;
        public int reposts;
        public String sentiment;
        public String avatarColor;
        public String avatarInitial;
        public String eventId;

        public FeedPost(String id, String username, String handle, String content,
                        String timeAgo, int likes, int reposts, String sentiment,
                        String avatarColor, String avatarInitial, String eventId) {

            this.id = id;
            this.username = username;
            this.handle = handle;
            this.content = content;
            this.timeAgo = timeAgo;
            this.likes = likes;
            this.reposts = reposts;
            this.sentiment = sentiment;
            this.avatarColor = avatarColor;
            this.avatarInitial = avatarInitial;
            this.eventId = eventId;
        }
    }

    public static class EventLogEntry {
        public String id;
        public int year;
        public String eventName;
        public String emoji;
        public String category;
        public List<String> affectedRegions;
        public GlobalStats effects;

        public EventLogEntry(String id, int year, String eventName, String emoji,
                             String category, List<String> affectedRegions, GlobalStats effects) {

            this.id = id;
            this.year = year;
            this.eventName = eventName;
            this.emoji = emoji;
            this.category = category;
            this.affectedRegions = affectedRegions;
            this.effects = effects;
        }
    }

    public static class GameState {
        public PlanetConfig planet;
        public List<Region> regions;
        public GlobalStats globalStats;
        public int year;
        public List<EventLogEntry> eventLog;
        public List<FeedPost> feedPosts;
        public Map<String, Integer> cooldowns;
        public Set<String> flashingRegions;
        public String lastEventId;

        public GameState() {
            regions = new ArrayList<>();
            eventLog = new ArrayList<>();
            feedPosts = new ArrayList<>();
            cooldowns = new HashMap<>();
            flashingRegions = new HashSet<>();
            lastEventId = "";
        }
    }

    /* -------------------------
       CONTINENT POLYGONS
    ------------------------- */

    public static final String[] CONTINENT_POLYGONS = {

            // top continent (12 o'clock)
            "303,102 403,82 503,142 463,242 363,222",

            // top right continent (2 o'clock)
            "508,252 608,232 708,312 668,412 568,372",

            // middle right continent (5 o'clock)
            "498,450 598,410 698,490 658,590 558,550",

            // bottom (6 o'clock)
            "366,552 466,512 546,592 506,692 406,672",

            // bottom left (7 o'clock)
            "146,530 246,490 346,570 306,670 206,630",

            // middle left (9 o'clock)
            "80,320 180,300 280,380 240,480 140,440",

            // top left (11 o'clock)
            "150,190 250,150 350,230 310,330 210,290",

            // middle
            "300,360 400,310 500,390 460,490 360,450"

    };

    public static final String[] REGION_NAMES_POOL = {
            "Valdoria", "Keth Magna", "The Sunken Expanse", "Crystalline Reach",
            "Ironshard Plains", "Novum Coast", "The Pale Wilds", "Stormwall Basin",
            "Ember Fields", "Azure Shore", "Greymount", "Veskara"
    };

    /* -------------------------
       UTILITY FUNCTIONS
    ------------------------- */

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static String regionHealthColor(Region region) {

        double score = (region.economicHealth * 0.5) + ((100 - region.stress) * 0.5);

        if (score > 65) return "#2d7a45";
        if (score > 45) return "#7a6b2d";
        if (score > 25) return "#8b4513";
        return "#7a1515";
    }

    public static String statColor(int value, boolean invert) {

        int v = invert ? 100 - value : value;

        if (v > 66) return "#22c55e";
        if (v > 33) return "#eab308";
        return "#ef4444";
    }

    /* -------------------------
       GAME EVENTS
     ------------------------- */

    public static final List<GameEventDef> GAME_EVENTS = new ArrayList<>();

    static {

        GAME_EVENTS.add(
                new GameEventDef(
                        "meteor",
                        "Meteor Strike",
                        "disaster",
                        "☄️",
                        "A massive meteor impacts the surface.",
                        "The sky burns.",
                        new EventEffect(-0.12, 28, -18, 22),
                        new EventEffect(-0.30, 55, -40, 50),
                        "random",
                        1,
                        new ArrayList<>(),
                        3
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "pandemic",
                        "Global Pandemic",
                        "disaster",
                        "🦠",
                        "A deadly pathogen spreads across the planet.",
                        "The invisible enemy arrives.",
                        new EventEffect(-0.18, 40, -28, 20),
                        new EventEffect(-0.18, 40, -28, 20),
                        "all",
                        0,
                        new ArrayList<>(),
                        5
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "ice_age",
                        "Ice Age",
                        "disaster",
                        "❄️",
                        "Global temperatures plummet, and glaciers expand across the planet.",
                        "The world freezes over.",
                        new EventEffect(-0.15, 35, -25, 40),
                        new EventEffect(-0.25, 50, -35, 60),
                        "all",
                        0,
                        new ArrayList<>(),
                        8
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "resource_war",
                        "Resource War",
                        "conflict",
                        "X",
                        "Major powers clash over water and mineral reserves.",
                        "Borders ignite under pressure.",
                        new EventEffect(-0.08, 0, 0, 0),
                        new EventEffect(-0.14, 26, -18, 24),
                        "random",
                        2,
                        new ArrayList<>(),
                        4
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "civil_unrest",
                        "Civil Unrest",
                        "conflict",
                        "!",
                        "Mass protests and riots destabilize major regions.",
                        "Cities fill with smoke and sirens.",
                        new EventEffect(-0.04, 0, 0, 0),
                        new EventEffect(-0.09, 22, -14, 18),
                        "random",
                        2,
                        new ArrayList<>(),
                        3
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "fusion_breakthrough",
                        "Fusion Breakthrough",
                        "technology",
                        "+",
                        "Cheap clean energy transforms planetary infrastructure.",
                        "A century of power problems ends overnight.",
                        new EventEffect(0.06, 0, 0, 0),
                        new EventEffect(0.10, -12, 20, -10),
                        "all",
                        0,
                        new ArrayList<>(),
                        5
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "orbital_network",
                        "Orbital Network",
                        "technology",
                        "^",
                        "A new satellite grid improves coordination across the world.",
                        "The planet becomes fully connected.",
                        new EventEffect(0.02, 0, 0, 0),
                        new EventEffect(0.03, -8, 12, -6),
                        "all",
                        0,
                        new ArrayList<>(),
                        4
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "cultural_renaissance",
                        "Cultural Renaissance",
                        "society",
                        "*",
                        "A surge of art, education, and civic pride lifts morale.",
                        "People begin believing in the future again.",
                        new EventEffect(0.03, 0, 0, 0),
                        new EventEffect(0.05, -14, 8, -4),
                        "all",
                        0,
                        new ArrayList<>(),
                        4
                )
        );

        GAME_EVENTS.add(
                new GameEventDef(
                        "unification_summit",
                        "Unification Summit",
                        "society",
                        "=",
                        "Regional leaders sign agreements that reduce tensions.",
                        "Old rivals sit at the same table.",
                        new EventEffect(0.01, 0, 0, 0),
                        new EventEffect(0.02, -10, 10, -8),
                        "random",
                        3,
                        new ArrayList<>(),
                        3
                )
        );
    }
}
