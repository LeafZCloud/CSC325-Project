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

    public static GameState buildInitialState(PlanetConfig config) {

        GameState state = new GameState();

        state.planet = config;
        state.regions = buildRegions(config.continents);

        state.globalStats = new GlobalStats(
                7.2,
                20,
                70,
                15
        );

        state.year = 2157;

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

        GlobalStats gs = new GlobalStats(
                state.globalStats.population,
                state.globalStats.stress,
                state.globalStats.economicHealth,
                state.globalStats.exposure
        );

        EventEffect g = event.globalEffects;

        gs.population = Math.max(0.01, gs.population * (1 + g.population));
        gs.stress = GameTypes.clamp(gs.stress + g.stress, 0, 100);
        gs.economicHealth = GameTypes.clamp(gs.economicHealth + g.economicHealth, 0, 100);
        gs.exposure = GameTypes.clamp(gs.exposure + g.exposure, 0, 100);

        newState.globalStats = gs;

        List<String> affectedRegions = new ArrayList<>();

        if (event.affectedRegions.equals("all")) {

            for (Region r : state.regions) {
                affectedRegions.add(r.id);
            }

        } else {

            List<Region> shuffled = new ArrayList<>(state.regions);
            Collections.shuffle(shuffled);

            for (int i = 0; i < event.numRegionsAffected; i++) {
                affectedRegions.add(shuffled.get(i).id);
            }
        }

        for (Region r : state.regions) {

            if (!affectedRegions.contains(r.id)) {
                newState.regions.add(r);
                continue;
            }

            EventEffect re = event.regionEffects;

            Region updated = new Region(
                    r.id,
                    r.name,
                    r.populationShare,
                    GameTypes.clamp(r.stress + re.stress, 0, 100),
                    GameTypes.clamp(r.economicHealth + re.economicHealth, 0, 100),
                    GameTypes.clamp(r.exposure + re.exposure, 0, 100),
                    r.polygonIndex
            );

            newState.regions.add(updated);
            newState.flashingRegions.add(r.id);
        }

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

        return newState;
    }

}