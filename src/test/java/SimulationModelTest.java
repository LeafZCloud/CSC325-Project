import edu.farmingdale.demo1.simulation.GameTypes;
import edu.farmingdale.demo1.simulation.SimulationModel;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class SimulationModelTest {

    @Test
    void generateId_shouldReturnValidUUID() {
        String id = SimulationModel.generateId();
        assertNotNull(id);
        assertDoesNotThrow(() -> UUID.fromString(id)); // throws if malformed
    }

    @Test
    void generateId_shouldReturnUniqueValues() {
        String id1 = SimulationModel.generateId();
        String id2 = SimulationModel.generateId();
        assertNotEquals(id1, id2);
    }

    @Test
    void buildRegions_shouldReturnCorrectCount() {
        List<GameTypes.Region> regions = SimulationModel.buildRegions(5);
        assertEquals(5, regions.size());
    }

    @Test
    void buildRegions_populationSharesShouldSumToOne() {
        List<GameTypes.Region> regions = SimulationModel.buildRegions(4);
        double total = regions.stream().mapToDouble(r -> r.populationShare).sum();
        assertEquals(1.0, total, 0.001); // small delta for floating point
    }

    @Test
    void buildRegions_eachRegionShouldHaveNonNullId() {
        List<GameTypes.Region> regions = SimulationModel.buildRegions(3);
        for (GameTypes.Region r : regions) {
            assertNotNull(r.id);
        }
    }

    @Test
    void buildInitialState_shouldStartAtYear2157() {
        GameTypes.PlanetConfig config = new GameTypes.PlanetConfig("TestWorld", 3, "terran", 1);
        GameTypes.GameState state = SimulationModel.buildInitialState(config);
        assertEquals(2157, state.year);
    }

    @Test
    void buildInitialState_shouldSetBasePopulationTo7Point2() {
        GameTypes.PlanetConfig config = new GameTypes.PlanetConfig("TestWorld", 3, "terran", 1);
        GameTypes.GameState state = SimulationModel.buildInitialState(config);
        assertEquals(7.2, state.globalStats.population, 0.01);
    }

    @Test
    void buildInitialState_regionCountShouldMatchConfig() {
        GameTypes.PlanetConfig config = new GameTypes.PlanetConfig("TestWorld", 6, "terran", 2);
        GameTypes.GameState state = SimulationModel.buildInitialState(config);
        assertEquals(6, state.regions.size());
    }

    @Test
    void isEventAvailable_shouldReturnTrueWhenNoCooldown() {
        GameTypes.PlanetConfig config = new GameTypes.PlanetConfig("TestWorld", 3, "terran", 1);
        GameTypes.GameState state = SimulationModel.buildInitialState(config);
        assertTrue(SimulationModel.isEventAvailableForPlayer(state, "meteor"));
    }

    @Test
    void isEventAvailable_shouldReturnFalseWhenOnCooldown() {
        GameTypes.PlanetConfig config = new GameTypes.PlanetConfig("TestWorld", 3, "terran", 1);
        GameTypes.GameState state = SimulationModel.buildInitialState(config);
        state.cooldowns.put("meteor", 3);
        assertFalse(SimulationModel.isEventAvailableForPlayer(state, "meteor"));
    }

    @Test
    void isEventAvailable_shouldReturnTrueWhenCooldownIsZero() {
        GameTypes.PlanetConfig config = new GameTypes.PlanetConfig("TestWorld", 3, "terran", 1);
        GameTypes.GameState state = SimulationModel.buildInitialState(config);
        state.cooldowns.put("meteor", 0);
        assertTrue(SimulationModel.isEventAvailableForPlayer(state, "meteor"));
    }
}