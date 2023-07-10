package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DungeonGenerationTest {

    @Test
    @DisplayName("Test dungeon generation with negative coordinates")
    public void negativeCoords() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(-10, -10, -5, -5, "c_alliesMovementTest");
        assertEquals(getPlayerPos(res), new Position(-9, -9));
        assertEquals(getExitPos(res), new Position(-7, -7));
    }

    @Test
    @DisplayName("Test dungeon generation creates player at top left")
    public void playerTopLeft() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 10, 10, "c_alliesMovementTest");
        assertEquals(getPlayerPos(res), new Position(1, 1));
    }

    @Test
    @DisplayName("Test dungeon generation creates exit at bottom right")
    public void exitBottomRight() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 10, 10, "c_alliesMovementTest");
        assertEquals(getExitPos(res), new Position(8, 8));
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }

    private Position getExitPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "exit").get(0).getPosition();
    }

}
