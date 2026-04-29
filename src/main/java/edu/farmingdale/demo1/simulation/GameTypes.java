package edu.farmingdale.demo1.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameTypes {

    /* -------------------------
       DATA MODELS
    ------------------------- */

    public static class Region {
        public String id;
        public String name;
        public double population;
        public double populationShare;
        public int stress;
        public int economicHealth;
        public int exposure;
        public int polygonIndex;

        public Region(String id, String name, double population, double populationShare, int stress,
                      int economicHealth, int exposure, int polygonIndex) {
            this.id = id;
            this.name = name;
            this.population = population;
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
        public List<String> commandHistory;
        public int lowEconomyStreak;
        public int temperatureVolatility;
        public String pendingTriggeredEventId;
        public int lastTriggeredCheckYear;

        public GameState() {
            regions = new ArrayList<>();
            eventLog = new ArrayList<>();
            feedPosts = new ArrayList<>();
            cooldowns = new HashMap<>();
            flashingRegions = new HashSet<>();
            lastEventId = "";
            commandHistory = new ArrayList<>();
            lowEconomyStreak = 0;
            temperatureVolatility = 0;
            pendingTriggeredEventId = null;
            lastTriggeredCheckYear = 0;
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
    public static final List<GameEventDef> TRIGGERED_EVENTS = new ArrayList<>();

    static {

        //applied image for meteor event
        GAME_EVENTS.add(
                new GameEventDef(
                        "meteor",
                        "Meteor Strike",
                        "disaster",
                        "☄",
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

        //applied image for earthquake event
        GAME_EVENTS.add(
                new GameEventDef(
                        "earthquakes",
                        "Earthquakes",
                        "disaster",
                        "",
                        "Violent tectonic shocks tear through cities and infrastructure.",
                        "The ground refuses to stay still.",
                        new EventEffect(-0.06, 18, -14, 15),
                        new EventEffect(-0.14, 34, -24, 26),
                        "random",
                        2,
                        new ArrayList<>(),
                        4
                )
        );

        //applied image for ice age event
        GAME_EVENTS.add(
                new GameEventDef(
                        "ice_age",
                        "Ice Age",
                        "disaster",
                        "❄",
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

        //applied image for volcanic eruption event
        GAME_EVENTS.add(
                new GameEventDef(
                        "volcanic_eruptions",
                        "Volcanic Eruptions",
                        "disaster",
                        "",
                        "Ash clouds and lava flows devastate nearby regions.",
                        "The crust splits open and fire follows.",
                        new EventEffect(-0.08, 24, -17, 21),
                        new EventEffect(-0.16, 40, -28, 30),
                        "random",
                        2,
                        new ArrayList<>(),
                        5
                )
        );

        //applied image for volcanic eruption event
        GAME_EVENTS.add(
                new GameEventDef(
                        "drought",
                        "Drought",
                        "disaster",
                        "",
                        "Long-term water shortages cripple crops and strain every settlement.",
                        "Reservoirs crack and fields turn to dust.",
                        new EventEffect(-0.07, 16, -16, 18),
                        new EventEffect(-0.12, 28, -20, 24),
                        "random",
                        3,
                        new ArrayList<>(),
                        5
                )
        );

        //applied image for plague event
        GAME_EVENTS.add(
                new GameEventDef(
                        "plague",
                        "Plague",
                        "conflict",
                        "",
                        "A deadly plague spreads through trade routes and crowded cities.",
                        "Fear moves faster than the symptoms.",
                        new EventEffect(-0.14, 30, -22, 18),
                        new EventEffect(-0.18, 38, -24, 22),
                        "all",
                        0,
                        new ArrayList<>(),
                        6
                )
        );

        //applied image for nuke event
        GAME_EVENTS.add(
                new GameEventDef(
                        "nuke",
                        "Nuke",
                        "conflict",
                        "",
                        "A nuclear strike wipes out infrastructure and poisons the surrounding region.",
                        "One flash changes the century.",
                        new EventEffect(-0.16, 38, -26, 30),
                        new EventEffect(-0.36, 65, -44, 58),
                        "random",
                        1,
                        new ArrayList<>(),
                        8
                )
        );

        //applied image for world_war event
        GAME_EVENTS.add(
                new GameEventDef(
                        "world_war",
                        "World War",
                        "conflict",
                        "",
                        "Global alliances collapse into open war across multiple continents.",
                        "Every border becomes a front line.",
                        new EventEffect(-0.12, 34, -24, 26),
                        new EventEffect(-0.20, 48, -34, 32),
                        "random",
                        4,
                        new ArrayList<>(),
                        7
                )
        );

        //applied image for industrial_revolution event
        GAME_EVENTS.add(
                new GameEventDef(
                        "industrial_revolution",
                        "Industrial Revolution",
                        "technology",
                        "",
                        "Advanced factories, transport, and energy systems scale production beyond all previous limits.",
                        "A new era of manufacturing begins, and productivity skyrockets.",
                        new EventEffect(0.07, -4, 18, 2),
                        new EventEffect(0.10, -10, 24, 0),
                        "all",
                        0,
                        new ArrayList<>(),
                        6
                )
        );

        //applied image for medical_breakthrough event
        GAME_EVENTS.add(
                new GameEventDef(
                        "medical_breakthrough",
                        "Medical Breakthrough",
                        "technology",
                        "",
                        "Revolutionary treatments and automated prevention systems eliminate numerous chronic ailments.",
                        "Humanity's lifespan takes a giant leap forward.",
                        new EventEffect(0.08, -12, 10, -14),
                        new EventEffect(0.10, -20, 12, -18),
                        "all",
                        0,
                        new ArrayList<>(),
                        5
                )
        );

        //applied image for golden_age event
        GAME_EVENTS.add(
                new GameEventDef(
                        "golden_age",
                        "Golden Age",
                        "society",
                        "",
                        "Unprecedented prosperity, total stability, and absolute confidence lift the whole civilization to new heights.",
                        "Peace and abundance are no longer just dreams.",
                        new EventEffect(0.07, -18, 20, -12),
                        new EventEffect(0.08, -24, 24, -14),
                        "all",
                        0,
                        new ArrayList<>(),
                        7
                )
        );

        TRIGGERED_EVENTS.add(
                new GameEventDef(
                        "tsunami",
                        "Tsunami",
                        "disaster",
                        "",
                        "A massive wave floods coastal regions and destroys infrastructure.",
                        "The extra moons drag the oceans into chaos.",
                        new EventEffect(-0.05, 18, -14, 18),
                        new EventEffect(-0.14, 28, -22, 20),
                        "random",
                        2,
                        new ArrayList<>(),
                        5
                )
        );

        TRIGGERED_EVENTS.add(
                new GameEventDef(
                        "virus",
                        "Virus",
                        "disaster",
                        "",
                        "Rapid environmental changes unleash a deadly global virus.",
                        "The biosphere mutates faster than medicine can react.",
                        new EventEffect(-0.09, 24, -15, 12),
                        new EventEffect(-0.12, 32, -18, 14),
                        "all",
                        0,
                        new ArrayList<>(),
                        6
                )
        );

        TRIGGERED_EVENTS.add(
                new GameEventDef(
                        "depression",
                        "Depression",
                        "society",
                        "",
                        "The economy collapses, leading to job loss and instability.",
                        "Confidence disappears and markets freeze.",
                        new EventEffect(-0.03, 20, -20, 6),
                        new EventEffect(-0.04, 24, -24, 8),
                        "all",
                        0,
                        new ArrayList<>(),
                        7
                )
        );

        TRIGGERED_EVENTS.add(
                new GameEventDef(
                        "famine",
                        "Famine",
                        "disaster",
                        "",
                        "Food shortages spread and population begins to decline.",
                        "Fields fail, storage empties, and hunger takes over.",
                        new EventEffect(-0.10, 16, -12, 8),
                        new EventEffect(-0.14, 20, -16, 10),
                        "all",
                        0,
                        new ArrayList<>(),
                        5
                )
        );

        TRIGGERED_EVENTS.add(
                new GameEventDef(
                        "rebellion",
                        "Rebellion",
                        "conflict",
                        "",
                        "High stress and a failing economy push citizens into revolt.",
                        "Order breaks down as the population turns on leadership.",
                        new EventEffect(-0.04, 22, -12, 8),
                        new EventEffect(-0.06, 30, -16, 10),
                        "random",
                        3,
                        new ArrayList<>(),
                        6
                )
        );

        TRIGGERED_EVENTS.add(
                new GameEventDef(
                        "economic_boom",
                        "Economic Boom",
                        "technology",
                        "",
                        "Strong growth boosts jobs, production, and morale.",
                        "Stable systems open the door to rapid expansion.",
                        new EventEffect(0.06, -10, 18, -8),
                        new EventEffect(0.08, -12, 20, -10),
                        "all",
                        0,
                        new ArrayList<>(),
                        6
                )
        );
    }

    public static GameEventDef findEventById(String id) {
        for (GameEventDef event : GAME_EVENTS) {
            if (event.id.equals(id)) {
                return event;
            }
        }

        for (GameEventDef event : TRIGGERED_EVENTS) {
            if (event.id.equals(id)) {
                return event;
            }
        }

        return null;
    }

    public static List<GameEventDef> allEvents() {
        List<GameEventDef> events = new ArrayList<>(GAME_EVENTS);
        events.addAll(TRIGGERED_EVENTS);
        return events;
    }

    public static List<GameEventDef> playerCommandEvents() {
        List<GameEventDef> events = new ArrayList<>();

        for (GameEventDef event : GAME_EVENTS) {
            if ("ice_age".equals(event.id) || "world_war".equals(event.id) || "golden_age".equals(event.id)) {
                continue;
            }
            events.add(event);
        }

        return Collections.unmodifiableList(events);
    }
}
