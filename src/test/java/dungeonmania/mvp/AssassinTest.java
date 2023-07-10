package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssassinTest {
    @Test
    @DisplayName("Test Assassin bribe works with 0% fail rate")
    public void assassinBribeWorks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest", "c_assassinTest");

        String assassinId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();
        // Picks up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Only need 1 treasure to interact
        res = assertDoesNotThrow(() -> dmc.interact(assassinId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @DisplayName("Test Assassin bribe works with 100% fail rate")
    public void assassinBribeFail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest", "c_assassinTestFail");

        String assassinId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();
        // Picks up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Only need 1 treasure to interact
        res = assertDoesNotThrow(() -> dmc.interact(assassinId));

        // Since failed to bribe, player kills assassin
        assertEquals(0, TestUtils.getEntities(res, "assassin").size());
        // Loses treasure as well
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }
}
