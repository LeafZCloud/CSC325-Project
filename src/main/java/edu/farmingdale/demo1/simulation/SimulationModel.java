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

        return newState;
    }

}