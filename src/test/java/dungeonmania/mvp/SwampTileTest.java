package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SwampTileTest {
    @Test
    @DisplayName("Player moves through swamp tile")
    public void playerMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_playerMove", "c_swampTileTest");

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 0), getPlayerPos(res));
    }

    @Test
    @DisplayName("Hostile mercenary moves through swamp tile")
    public void hostileMercMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_hostileMercMove", "c_swampTileTest");
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 0), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 0), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 0), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 0), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(3, 0), getMercPos(res));
    }

    @Test
    @DisplayName("Hostile mercenary gets stuck in 2 swamp tiles")
    public void doubleStuckMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_doubleStuck", "c_swampTileTest");
        assertEquals(new Position(1, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(3, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 1), getMercPos(res));
    }

    @Test
    @DisplayName("Non adjacent ally moves through swamp tile")
    public void nonAdjAllyMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_nonAdjAlly", "c_swampTileTest");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 0), getMercPos(res));

        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 0), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 0), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 0), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 0), getMercPos(res));
    }

    @Test
    @DisplayName("Adjacent ally moves through swamp tile")
    public void adjAllyMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_adjAlly", "c_swampTileTest");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 0), getMercPos(res));

        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 0), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 0), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 0), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 0), getMercPos(res));
    }

    @Test
    @DisplayName("Zombie moves through swamp tile")
    public void zombieMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_zombie", "c_swampTileTest");
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getZombiePos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getZombiePos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 1), getZombiePos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 1), getZombiePos(res));
    }

    @Test
    @DisplayName("Spider moves through swamp tile")
    public void spiderMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_spider", "c_swampTileTest");
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 0), getSpiderPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 0), getSpiderPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 0), getSpiderPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 0), getSpiderPos(res));
    }

    @Test
    @DisplayName("Mercenary pathfinding accounts for swamp path")
    public void mercenaryPathFindMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_swampPath", "c_swampTileTest_longSlow");
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 2), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 3), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 3), getMercPos(res));
    }

    @Test
    @DisplayName("Ally pathfinding accounts for swamp path")
    public void allyPathFindMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_swampPath", "c_swampTileTest_longSlow");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 1), getMercPos(res));

        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 2), getMercPos(res));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(3, 3), getMercPos(res));
    }

    @Test
    @DisplayName("Hostile mercenary spawns on swamp tile")
    public void spawnMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_spawn", "c_swampTileTest");
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 0), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 0), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 0), getMercPos(res));
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 0), getMercPos(res));
    }

    @Test
    @DisplayName("Zombie spawned on to swamp tile should be stuck")
    public void zombieSpawnMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_zombieSpawn", "c_swampTileTest");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 1), getZombiePos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getZombiePos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getZombiePos(res));
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(new Position(2, 1), getZombiePos(res));
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private Position getZombiePos(DungeonResponse res) {
        return TestUtils.getEntities(res, "zombie_toast").get(0).getPosition();
    }

    private Position getSpiderPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "spider").get(0).getPosition();
    }
}
