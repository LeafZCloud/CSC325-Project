import edu.farmingdale.demo1.simulation.GameTypes;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTypesTest {

    @Test
    void clamp_shouldReturnMinWhenValueIsBelowMin() {
        assertEquals(0, GameTypes.clamp(-10, 0, 100));
    }

    @Test
    void clamp_shouldReturnMaxWhenValueIsAboveMax() {
        assertEquals(100, GameTypes.clamp(150, 0, 100));
    }

    @Test
    void clamp_shouldReturnValueWhenWithinRange() {
        assertEquals(50, GameTypes.clamp(50, 0, 100));
    }

    @Test
    void clamp_shouldReturnMinWhenValueEqualsMin() {
        assertEquals(0, GameTypes.clamp(0, 0, 100));
    }

    @Test
    void clamp_shouldReturnMaxWhenValueEqualsMax() {
        assertEquals(100, GameTypes.clamp(100, 0, 100));
    }

    @Test
    void findEventById_shouldReturnCorrectPlayerEvent() {
        GameTypes.GameEventDef event = GameTypes.findEventById("meteor");
        assertNotNull(event);
        assertEquals("meteor", event.id);
    }

    @Test
    void findEventById_shouldReturnCorrectTriggeredEvent() {
        GameTypes.GameEventDef event = GameTypes.findEventById("tsunami");
        assertNotNull(event);
        assertEquals("tsunami", event.id);
    }

    @Test
    void findEventById_shouldReturnNullForUnknownId() {
        assertNull(GameTypes.findEventById("nonexistent_event"));
    }

    @Test
    void allEvents_shouldContainBothPlayerAndTriggeredEvents() {
        int total = GameTypes.allEvents().size();
        int expected = GameTypes.GAME_EVENTS.size() + GameTypes.TRIGGERED_EVENTS.size();
        assertEquals(expected, total);
    }

    @Test
    void allEvents_shouldContainMeteorAndTsunami() {
        boolean hasMeteor = GameTypes.allEvents().stream().anyMatch(e -> "meteor".equals(e.id));
        boolean hasTsunami = GameTypes.allEvents().stream().anyMatch(e -> "tsunami".equals(e.id));
        assertTrue(hasMeteor);
        assertTrue(hasTsunami);
    }

    @Test
    void regionHealthColor_shouldReturnGreenForHighHealth() {
        GameTypes.Region region = new GameTypes.Region("r1", "Test", 1.0, 0.5, 10, 90, 10, 0);
        assertEquals("#2d7a45", GameTypes.regionHealthColor(region));
    }

    @Test
    void regionHealthColor_shouldReturnRedForLowHealth() {
        GameTypes.Region region = new GameTypes.Region("r1", "Test", 1.0, 0.5, 90, 10, 80, 0);
        assertEquals("#7a1515", GameTypes.regionHealthColor(region));
    }

    @Test
    void statColor_shouldReturnGreenForHighValue() {
        assertEquals("#22c55e", GameTypes.statColor(80, false));
    }

    @Test
    void statColor_shouldReturnRedForLowValue() {
        assertEquals("#ef4444", GameTypes.statColor(10, false));
    }

    @Test
    void statColor_shouldInvertCorrectly() {
        assertEquals("#22c55e", GameTypes.statColor(20, true));
    }
}