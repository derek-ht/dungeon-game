package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SceptreTest {
    @Test
    @DisplayName("Test Sceptre can be built and used")
    public void sceptreBuild() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest", "c_assassinTest");

        // Picks up key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // Picks up wood
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Go pick up sun stone
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Build Sceptre
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Use on mercenary anywhere
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "sceptre").size());
    }

    @Test
    @DisplayName("Test Sceptre can be built with 2 sunstones")
    public void sceptreMultipleSunStones() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest", "c_assassinTest");

        // Picks up wood
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Go pick up sun stones
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        // Build the sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());

        // Sunstone is substitue for the treasure/key and 1 sunstone and wood is used
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
    }

    @Test
    @DisplayName("Test mind control duration")
    public void mindControlDuration() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreDurationtest", "c_assassinTest");

        // Get all components of sceptre
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);

        // Build the sceptre
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        // Use on mercenary anywhere
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Duration of 3
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());

        res = dmc.tick(Direction.LEFT);
        assertEquals(0, res.getBattles().size());

        // Effect wears off
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

}
