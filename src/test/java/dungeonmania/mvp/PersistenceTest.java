package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class PersistenceTest {
    @Test
    @DisplayName("Test IllegalArgumentException from loadGame")
    public void loadGameExcep() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame("fail"));
    }

    @Test
    @DisplayName("Tests if inventory persists")
    public void inventoryPersists() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_PersistenceTest_doorBribeMercenaries", "c_PersistenceTest");
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position savePosition = TestUtils.getPlayerPos(res);
        assertEquals(TestUtils.getInventory(res, "key").size(), 1);
        dmc.saveGame("save1");

        // Position should be same and key should still be in inventory
        DungeonResponse res2 = dmc.loadGame("save1");
        assertEquals(TestUtils.getPlayerPos(res2), savePosition);
        assertEquals(TestUtils.getInventory(res2, "key").size(), 1);

        // player opens door should be gone
        res2 = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntities(res2, "key").size(), 0);
        dmc.saveGame("save2");

        // key should still be gone on load
        DungeonResponse res3 = dmc.loadGame("save2");
        assertEquals(TestUtils.getEntities(res3, "key").size(), 0);
    }

    @Test
    @DisplayName("Mercenaries remain bribed and battles ")
    public void battlesPersists() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_PersistenceTest_doorBribeMercenaries", "c_PersistenceTest");
        String mercId = TestUtils.getEntityAtPos(res, "mercenary", new Position(8, 1)).get().getId();
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);

        RoundResponse firstRound = battle.getRounds().get(0);

        // (15 - 5) / 10 == 1 damage to player
        assertEquals(1, -firstRound.getDeltaCharacterHealth(), 0.001);

        dmc.saveGame("save1");

        dmc.loadGame("save1");

        DungeonResponse postBattleResponse = dmc.tick(Direction.RIGHT);

        BattleResponse secondBattle = res.getBattles().get(0);

        RoundResponse secondRound = secondBattle.getRounds().get(0);

        // (15 - 5) / 10 == 1 damage to player
        assertEquals(1, -secondRound.getDeltaCharacterHealth(), 0.001);
        List<EntityResponse> entities = postBattleResponse.getEntities();

        // player only had 2 health so player dies
        assertTrue(
                TestUtils.countEntityOfType(entities, "player") == 0);
    }
}
