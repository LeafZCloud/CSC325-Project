package edu.farmingdale.demo1.simulation;

import java.util.*;

import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.GameTypes.Region;
import edu.farmingdale.demo1.simulation.GameTypes.GameState;
import edu.farmingdale.demo1.simulation.GameTypes.GameEventDef;
import edu.farmingdale.demo1.simulation.GameTypes.GlobalStats;
import edu.farmingdale.demo1.simulation.GameTypes.PlanetConfig;
import edu.farmingdale.demo1.simulation.GameTypes.EventEffect;
import edu.farmingdale.demo1.simulation.GameTypes.EventLogEntry;

public class SimulationModel {

    private static final Random random = new Random();
    private static final String[] FEED_USERNAMES = {
            "Ava Sol", "Orion Vale", "Mira Quill", "Juno Hart", "Soren Pike", "Lena Frost",
            "Kai Ember", "Nia Pulse", "Ezra Bloom", "Tala Reed", "Iris Voss", "Noah Skye"
    };
    private static final String[] FEED_HANDLES = {
            "@avasol", "@orionvale", "@miraquill", "@junohart", "@sorenpike", "@lenafrost",
            "@kaiember", "@niapulse", "@ezrabloom", "@talareed", "@irisvoss", "@noahskye"
    };
    private static final String[] FEED_COLORS = {
            "#38bdf8", "#f97316", "#22c55e", "#eab308", "#fb7185", "#a78bfa"
    };

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static List<Region> buildRegions(int continents) {

        List<String> shuffled = new ArrayList<>(Arrays.asList(GameTypes.REGION_NAMES_POOL));
        Collections.shuffle(shuffled);

        List<Region> regions = new ArrayList<>();

        double[] shares = new double[continents];
        double total = 0;

        for (int i = 0; i < continents; i++) {
            shares[i] = random.nextDouble();
            total += shares[i];
        }

        for (int i = 0; i < continents; i++) {
            shares[i] /= total;
        }

        for (int i = 0; i < continents; i++) {

            regions.add(new Region(
                    "region-" + i,
                    shuffled.get(i),
                    shares[i],
                    random.nextInt(20) + 10,
                    random.nextInt(30) + 55,
                    random.nextInt(20) + 5,
                    i % GameTypes.CONTINENT_POLYGONS.length
            ));
        }

        return regions;
    }

    public static GameState  buildInitialState(PlanetConfig config) {

        GameState state = new GameState();

        state.planet = config;
        state.regions = buildRegions(config.continents);

        double totalStress = 0;
        double totalEcon = 0;
        double totalExposure = 0;

        for (Region r : state.regions) {
            totalStress += r.stress * r.populationShare;
            totalEcon += r.economicHealth * r.populationShare;
            totalExposure += r.exposure * r.populationShare;
        }

        state.globalStats = new GlobalStats(
                7.2, // Base population
                (int) Math.round(totalStress),
                (int) Math.round(totalEcon),
                (int) Math.round(totalExposure)
        );

        state.year = 2157;
        state.feedPosts = generateOpeningFeedPosts(state);

        return state;
    }

    public static GameState applyEvent(GameState state, GameEventDef event) {

        GameState newState = new GameState();

        newState.planet = state.planet;
        newState.year = state.year + 1;

        newState.regions = new ArrayList<>();
        newState.eventLog = new ArrayList<>(state.eventLog);
        newState.feedPosts = new ArrayList<>(state.feedPosts);
        newState.cooldowns = new HashMap<>(state.cooldowns);
        newState.flashingRegions = new HashSet<>();
        newState.lastEventId = event.id;

        // Start with a copy of the previous global stats
        GlobalStats gs = new GlobalStats(
                state.globalStats.population,
                state.globalStats.stress,
                state.globalStats.economicHealth,
                state.globalStats.exposure
        );

        EventEffect g = event.globalEffects;

        List<String> affectedRegions = new ArrayList<>();

        if (event.affectedRegions.equals("all")) {
            for (Region r : state.regions) {
                affectedRegions.add(r.id);
            }
        } else {
            List<Region> shuffled = new ArrayList<>(state.regions);
            Collections.shuffle(shuffled);
            int count = Math.min(event.numRegionsAffected, shuffled.size());
            for (int i = 0; i < count; i++) {
                affectedRegions.add(shuffled.get(i).id);
            }
        }

        // Update regions
        double totalNewPopulation = 0;
        List<Double> regionPopulations = new ArrayList<>();

        for (Region r : state.regions) {
            double currentPop = state.globalStats.population * r.populationShare;
            // Apply global modifier first
            currentPop *= (1 + g.population);
            // Apply regional modifier if affected
            if (affectedRegions.contains(r.id)) {
                currentPop = Math.max(0.01, currentPop * (1 + event.regionEffects.population));
            }
            regionPopulations.add(currentPop);
            totalNewPopulation += currentPop;
        }

        for (int i = 0; i < state.regions.size(); i++) {
            Region r = state.regions.get(i);
            double newPopShare = regionPopulations.get(i) / totalNewPopulation;

            if (!affectedRegions.contains(r.id)) {
                Region unchanged = new Region(
                        r.id,
                        r.name,
                        newPopShare,
                        r.stress,
                        r.economicHealth,
                        r.exposure,
                        r.polygonIndex
                );
                newState.regions.add(unchanged);
                continue;
            }

            EventEffect re = event.regionEffects;
            Region updated = new Region(
                    r.id,
                    r.name,
                    newPopShare,
                    GameTypes.clamp(r.stress + re.stress, 0, 100),
                    GameTypes.clamp(r.economicHealth + re.economicHealth, 0, 100),
                    GameTypes.clamp(r.exposure + re.exposure, 0, 100),
                    r.polygonIndex
            );

            newState.regions.add(updated);
            newState.flashingRegions.add(r.id);
        }

        gs.population = totalNewPopulation;

        // Recalculate global weighted averages from the new regional states
        double totalStress = 0;
        double totalEcon = 0;
        double totalExposure = 0;

        for (Region r : newState.regions) {
            totalStress += r.stress * r.populationShare;
            totalEcon += r.economicHealth * r.populationShare;
            totalExposure += r.exposure * r.populationShare;
        }

        gs.stress = (int) Math.round(totalStress);
        gs.economicHealth = (int) Math.round(totalEcon);
        gs.exposure = (int) Math.round(totalExposure);

        newState.globalStats = gs;

        EventLogEntry logEntry = new EventLogEntry(
                generateId(),
                state.year,
                event.name,
                event.emoji,
                event.category,
                affectedRegions,
                new GlobalStats(
                        g.population * 100,
                        g.stress,
                        g.economicHealth,
                        g.exposure
                )
        );

        newState.eventLog.add(0, logEntry);
        newState.cooldowns.put(event.id, state.year + event.cooldown);
        newState.feedPosts.addAll(0, generateEventFeedPosts(newState, event, affectedRegions));

        return newState;
    }

    private static List<GameTypes.FeedPost> generateOpeningFeedPosts(GameState state) {
        List<GameTypes.FeedPost> posts = new ArrayList<>();
        String planetName = state.planet != null ? state.planet.name : "the world";

        posts.add(createFeedPost(
                0,
                "Just landed eyes on " + planetName + ". " + state.regions.size() + " major regions on the map already.",
                "positive",
                "now",
                null
        ));

        if (!state.regions.isEmpty()) {
            Region region = state.regions.get(random.nextInt(state.regions.size()));
            posts.add(createFeedPost(
                    1,
                    region.name + " is trending for rapid growth. People are calling it the planet's next powerhouse.",
                    "neutral",
                    "3m",
                    null
            ));
        }

        posts.add(createFeedPost(
                2,
                "Watching stress and economic indicators closely. This world has potential if leadership stays sharp.",
                "neutral",
                "9m",
                null
        ));

        return posts;
    }

    private static List<GameTypes.FeedPost> generateEventFeedPosts(GameState state, GameEventDef event, List<String> affectedRegions) {
        List<GameTypes.FeedPost> posts = new ArrayList<>();
        List<String> affectedNames = new ArrayList<>();

        for (Region region : state.regions) {
            if (affectedRegions.contains(region.id)) {
                affectedNames.add(region.name);
            }
        }

        String spotlight = affectedNames.isEmpty() ? "multiple regions" : String.join(", ", affectedNames);
        String sentiment = switch (event.category) {
            case "technology" -> "positive";
            case "society" -> "positive";
            case "conflict" -> "negative";
            default -> "panic";
        };

        posts.add(createFeedPost(
                random.nextInt(FEED_USERNAMES.length),
                event.name + " just hit " + spotlight + ". " + event.flavour,
                sentiment,
                "1m",
                event.id
        ));

        posts.add(createFeedPost(
                random.nextInt(FEED_USERNAMES.length),
                switch (event.category) {
                    case "technology" -> "Massive shift underway. Infrastructure numbers are going to look very different next year.";
                    case "society" -> "You can feel morale changing already. The whole planet conversation just softened.";
                    case "conflict" -> "Supply chains are going to take a hit if this spreads beyond " + spotlight + ".";
                    default -> "Emergency channels are overloaded. People in " + spotlight + " need support immediately.";
                },
                sentiment,
                "4m",
                event.id
        ));

        posts.add(createFeedPost(
                random.nextInt(FEED_USERNAMES.length),
                "Planet update: population " + String.format("%.1fB", state.globalStats.population)
                        + ", stress " + state.globalStats.stress + "%, economy " + state.globalStats.economicHealth + "%.",
                "neutral",
                "7m",
                event.id
        ));

        return posts;
    }

    private static GameTypes.FeedPost createFeedPost(int identityIndex, String content, String sentiment, String timeAgo, String eventId) {
        int safeIndex = Math.floorMod(identityIndex, FEED_USERNAMES.length);
        String username = FEED_USERNAMES[safeIndex];
        String handle = FEED_HANDLES[safeIndex];
        String initial = username.substring(0, 1).toUpperCase();

        return new GameTypes.FeedPost(
                generateId(),
                username,
                handle,
                content,
                timeAgo,
                120 + random.nextInt(9400),
                20 + random.nextInt(2200),
                sentiment,
                FEED_COLORS[random.nextInt(FEED_COLORS.length)],
                initial,
                eventId
        );
    }

}
