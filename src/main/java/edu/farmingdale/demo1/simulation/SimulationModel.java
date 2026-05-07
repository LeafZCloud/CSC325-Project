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
            "Kai Ember", "Nia Pulse", "Ezra Bloom", "Tala Reed", "Iris Voss", "Noah Skye",
            "Rhea North", "Malik Stone", "Cora Wren", "Dax Rivers", "Selene Cross", "Theo Ward",
            "Amara Finch", "Vik Rao", "Pia Storm", "Cal Hartwell", "Nova Lane", "Zed Marlow"
    };
    private static final String[] FEED_HANDLES = {
            "@avasol", "@orionvale", "@miraquill", "@junohart", "@sorenpike", "@lenafrost",
            "@kaiember", "@niapulse", "@ezrabloom", "@talareed", "@irisvoss", "@noahskye",
            "@rheanorth", "@malikstone", "@corawren", "@daxrivers", "@selenecross", "@theoward",
            "@amarafinch", "@vikrao", "@piastorm", "@calhartwell", "@novalane", "@zedmarlow"
    };
    private static final String[] FEED_COLORS = {
            "#38bdf8", "#f97316", "#22c55e", "#eab308", "#fb7185", "#a78bfa",
            "#14b8a6", "#f43f5e", "#8b5cf6", "#06b6d4", "#84cc16", "#f59e0b"
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

            double regionPop = 7.2 * shares[i];
            regions.add(new Region(
                    "region-" + i,
                    shuffled.get(i),
                    regionPop,
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
        state.lastTriggeredCheckYear = state.year;

        return state;
    }

    public static GameState applyEvent(GameState state, GameEventDef event) {
        return applySingleEvent(state, event, true);
    }

    public static GameState applyPlayerCommand(GameState state, GameEventDef event) {
        GameState updatedState = applySingleEvent(state, event, true);
        updatedState.commandHistory.add(event.id);
        updatedState.pendingTriggeredEventId = null;

        if (updatedState.year - updatedState.lastTriggeredCheckYear >= 5) {
            GameEventDef triggeredEvent = determineTriggeredEvent(updatedState);
            updatedState.lastTriggeredCheckYear = updatedState.year;

            if (triggeredEvent != null) {
                updatedState = applySingleEvent(updatedState, triggeredEvent, false);
                updatedState.pendingTriggeredEventId = triggeredEvent.id;
                updatedState.lastTriggeredCheckYear = updatedState.year;
            }
        }

        return updatedState;
    }

    private static GameState applySingleEvent(GameState state, GameEventDef event, boolean advanceYear) {

        GameState newState = new GameState();

        newState.planet = state.planet;
        newState.year = state.year + (advanceYear ? 1 : 0);

        newState.regions = new ArrayList<>();
        newState.eventLog = new ArrayList<>(state.eventLog);
        newState.feedPosts = new ArrayList<>(state.feedPosts);
        newState.cooldowns = new HashMap<>(state.cooldowns);
        newState.flashingRegions = new HashSet<>();
        newState.lastEventId = event.id;
        newState.commandHistory = new ArrayList<>(state.commandHistory);
        newState.lowEconomyStreak = state.lowEconomyStreak;
        newState.temperatureVolatility = state.temperatureVolatility;
        newState.pendingTriggeredEventId = state.pendingTriggeredEventId;
        newState.lastTriggeredCheckYear = state.lastTriggeredCheckYear;

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
            double newPop = regionPopulations.get(i);
            double newPopShare = newPop / totalNewPopulation;

            if (!affectedRegions.contains(r.id)) {
                Region unchanged = new Region(
                        r.id,
                        r.name,
                        newPop,
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
                    newPop,
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
        double populationDelta = totalNewPopulation - state.globalStats.population;

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
        updateTrendTrackers(newState, event);

        EventLogEntry logEntry = new EventLogEntry(
                generateId(),
                newState.year,
                event.name,
                event.emoji,
                event.category,
                affectedRegions,
                new GlobalStats(
                        populationDelta,
                        event.regionEffects.stress,
                        event.regionEffects.economicHealth,
                        event.regionEffects.exposure
                )
        );

        newState.eventLog.add(0, logEntry);
        newState.cooldowns.put(event.id, newState.year + event.cooldown);
        newState.feedPosts.addAll(0, generateEventFeedPosts(newState, event, affectedRegions));

        return newState;
    }

    private static void updateTrendTrackers(GameState state, GameEventDef event) {
        if (state.globalStats.economicHealth <= 35) {
            state.lowEconomyStreak += 1;
        } else {
            state.lowEconomyStreak = 0;
        }

        int volatilityShift = switch (event.id) {
            case "ice_age" -> 45;
            case "drought" -> 30;
            case "volcanic_eruptions" -> 22;
            case "meteor" -> 12;
            case "industrial_revolution" -> 4;
            case "medical_breakthrough", "economic_boom" -> -16;
            default -> -8;
        };

        state.temperatureVolatility = Math.max(0, Math.min(100, state.temperatureVolatility + volatilityShift));
    }

    private static GameEventDef determineTriggeredEvent(GameState state) {
        if (shouldTriggerTsunami(state)) {
            return GameTypes.findEventById("tsunami");
        }

        if (shouldTriggerIceAge(state)) {
            return GameTypes.findEventById("ice_age");
        }

        if (shouldTriggerVirus(state)) {
            return GameTypes.findEventById("virus");
        }

        if (shouldTriggerFamine(state)) {
            return GameTypes.findEventById("famine");
        }

        if (shouldTriggerRebellion(state)) {
            return GameTypes.findEventById("rebellion");
        }

        if (shouldTriggerDepression(state)) {
            return GameTypes.findEventById("depression");
        }

        if (shouldTriggerEconomicBoom(state)) {
            return GameTypes.findEventById("economic_boom");
        }

        return null;
    }

    private static boolean shouldTriggerTsunami(GameState state) {
        return state.planet != null
                && state.planet.moons >= 3
                && isAvailable(state, "tsunami");
    }

    private static boolean shouldTriggerIceAge(GameState state) {
        int meteorCount = 0;
        int startIndex = Math.max(0, state.commandHistory.size() - 3);

        for (int i = startIndex; i < state.commandHistory.size(); i++) {
            if ("meteor".equals(state.commandHistory.get(i))) {
                meteorCount += 1;
            }
        }

        return meteorCount >= 2 && isAvailable(state, "ice_age");
    }

    private static boolean shouldTriggerVirus(GameState state) {
        return state.temperatureVolatility >= 60 && isAvailable(state, "virus");
    }

    private static boolean shouldTriggerFamine(GameState state) {
        return (hasRecentEvent(state, "Ice Age", 2) || hasRecentEvent(state, "Drought", 2))
                && isAvailable(state, "famine");
    }

    private static boolean shouldTriggerRebellion(GameState state) {
        return state.globalStats.stress >= 70
                && state.globalStats.economicHealth <= 35
                && isAvailable(state, "rebellion");
    }

    private static boolean shouldTriggerDepression(GameState state) {
        return state.globalStats.economicHealth <= 35
                && (state.lowEconomyStreak >= 2 || countRecentDisasters(state, 4) >= 3)
                && isAvailable(state, "depression");
    }

    private static boolean shouldTriggerEconomicBoom(GameState state) {
        return state.globalStats.economicHealth >= 72
                && state.globalStats.stress <= 28
                && isAvailable(state, "economic_boom");
    }

    private static boolean isAvailable(GameState state, String eventId) {
        Integer nextAvailableYear = state.cooldowns.get(eventId);
        return nextAvailableYear == null || state.year >= nextAvailableYear;
    }

    private static int countRecentDisasters(GameState state, int limit) {
        int disasterCount = 0;
        int checked = 0;

        for (EventLogEntry entry : state.eventLog) {
            if (checked >= limit) {
                break;
            }

            if ("disaster".equals(entry.category) || "conflict".equals(entry.category)) {
                disasterCount += 1;
            }

            checked += 1;
        }

        return disasterCount;
    }

    private static boolean hasRecentEvent(GameState state, String eventName, int limit) {
        int checked = 0;

        for (EventLogEntry entry : state.eventLog) {
            if (checked >= limit) {
                break;
            }

            if (eventName.equals(entry.eventName)) {
                return true;
            }

            checked += 1;
        }

        return false;
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

    public static GameTypes.FeedPost generateLiveFeedPost(GameState state) {
        Region region = pickRegion(state);
        String regionName = region != null ? region.name : "the capital";
        GlobalStats stats = state.globalStats;

        List<LiveFeedTemplate> templates = new ArrayList<>();
        templates.add(new LiveFeedTemplate(
                "Just checked the public dashboard. Population is holding around " + String.format("%.1fB", stats.population) + ".",
                "neutral"
        ));
        templates.add(new LiveFeedTemplate(
                "People in " + regionName + " are talking about the next big policy move. Everyone is watching the numbers.",
                "neutral"
        ));
        templates.add(new LiveFeedTemplate(
                "Local markets in " + regionName + " are busy today. Economy score is sitting at " + stats.economicHealth + "%.",
                stats.economicHealth >= 60 ? "positive" : "negative"
        ));
        templates.add(new LiveFeedTemplate(
                "Exposure reports are at " + stats.exposure + "%. Emergency crews say preparation matters more than panic.",
                stats.exposure >= 55 ? "negative" : "neutral"
        ));
        templates.add(new LiveFeedTemplate(
                "The mood in " + regionName + " feels different this year. Stress is at " + stats.stress + "% and people notice it.",
                stats.stress >= 65 ? "panic" : stats.stress >= 40 ? "negative" : "positive"
        ));
        templates.add(new LiveFeedTemplate(
                "Schools in " + regionName + " are running debates about the planet's future. The younger crowd is very tuned in.",
                "positive"
        ));

        if (stats.stress >= 70) {
            templates.add(new LiveFeedTemplate(
                    "Hard to sleep with stress this high. People want answers from leadership tonight.",
                    "panic"
            ));
        }
        if (stats.economicHealth <= 40) {
            templates.add(new LiveFeedTemplate(
                    "Small businesses are worried. A weak economy is starting to show up in everyday life.",
                    "negative"
            ));
        }
        if (stats.economicHealth >= 75 && stats.stress <= 35) {
            templates.add(new LiveFeedTemplate(
                    "This is the most optimistic the feed has felt in years. Growth is up and people are calmer.",
                    "positive"
            ));
        }
        if (state.lastEventId != null && !state.lastEventId.isBlank()) {
            templates.add(new LiveFeedTemplate(
                    "Still seeing reactions to the latest event. The planet feed has not slowed down.",
                    stats.stress >= 55 ? "negative" : "neutral"
            ));
        }

        LiveFeedTemplate template = templates.get(random.nextInt(templates.size()));
        return createFeedPost(
                random.nextInt(FEED_USERNAMES.length),
                template.content,
                template.sentiment,
                "now",
                state.lastEventId == null || state.lastEventId.isBlank() ? null : state.lastEventId
        );
    }

    private static Region pickRegion(GameState state) {
        if (state == null || state.regions == null || state.regions.isEmpty()) {
            return null;
        }

        return state.regions.get(random.nextInt(state.regions.size()));
    }

    private static class LiveFeedTemplate {
        String content;
        String sentiment;

        LiveFeedTemplate(String content, String sentiment) {
            this.content = content;
            this.sentiment = sentiment;
        }
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
