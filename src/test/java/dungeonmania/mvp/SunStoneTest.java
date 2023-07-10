package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SunStoneTest {

    @Test
    @DisplayName("Tests sunStone as a key/treasure substitute for buildables")
    public void sunStoneSubstitute() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneSub", "c_assassinTest");

        // Picks up sunStone
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up Wood x2
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        // Makes sure there are no keys or treasures
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        // Build the shield with sunstone
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());

        // Makes sure sunstone does not get used
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @DisplayName("Tests sunStone can't be used to bribe")
    public void sunStoneBribe() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneSub", "c_assassinTest");

        // Picks up sunStone
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Asserts that sunStone cannot be used to bribe
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        // Makes sure sunstone does not get used
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @DisplayName("Tests sunStone can be used to open doors")
    public void sunStoneOpenDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneDoors", "c_assassinTest");

        // Picks up sunStone
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up Wrong key to door
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // Get position next to door
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        // Opens door with sunstone and wrong key, makes sure can move onto door
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());

        // Makes sure sunstone does not get used
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Makes sure key does not get used
        assertEquals(1, TestUtils.getInventory(res, "key").size());
    }
}

