package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class AllyBattleTest {
    @Test
    @DisplayName("Test singular ally increases damage and defence")
    public void singleAllyBuff() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_allyBattleTest_singleAlly",
                "c_allyBattleTest");
        String mercId = TestUtils.getEntitiesStream(res,
                "mercenary").findFirst().get().getId();

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        // Bribe 1st merc
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Battle 2nd mercenary
        DungeonResponse postBattleResponse = dmc.tick(Direction.DOWN);
        List<EntityResponse> entities = postBattleResponse.getEntities();

        // Only your ally mercenary should be alive
        assertTrue(TestUtils.countEntityOfType(entities, "mercenary") == 1);

        BattleResponse battle = res.getBattles().get(0);

        RoundResponse firstRound = battle.getRounds().get(0);

        // (15 - 5) / 10 == 1 damage to player
        assertEquals(1, -firstRound.getDeltaCharacterHealth(), 0.001);
    }

    @Test
    @DisplayName("Test mulltiple allies increases damage and defence")
    public void multipleAllyBuff() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_allyBattleTest_multipleAllies",
                "c_allyBattleTest_multipleAllies");

        String mercSecondId = TestUtils.getEntitiesStream(res,
                "mercenary").skip(1).findFirst().get().getId();

        String mercThirdId = TestUtils.getEntitiesStream(res,
                "mercenary").skip(2).findFirst().get().getId();

        // Pick up treasure
        res = dmc.tick(Direction.DOWN);

        // Pick up second treasure
        res = dmc.tick(Direction.DOWN);

        res = dmc.tick(Direction.DOWN);
        // Bribe 2nd merc
        res = assertDoesNotThrow(() -> dmc.interact(mercThirdId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        res = dmc.tick(Direction.DOWN);

        res = assertDoesNotThrow(() -> dmc.interact(mercSecondId));

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        // Battle 1st mercenary
        DungeonResponse postBattleResponse = dmc.tick(Direction.UP);
        List<EntityResponse> entities = postBattleResponse.getEntities();

        // Only your allies should be alive
        assertTrue(TestUtils.countEntityOfType(entities, "mercenary") == 2);

        BattleResponse battle = res.getBattles().get(0);

        RoundResponse firstRound = battle.getRounds().get(0);

        // (15 - 10) / 10 == 0.5 damage to player
        assertEquals(0.5, -firstRound.getDeltaCharacterHealth(), 0.001);
    }

    @Test
    @DisplayName("Test assassin and mercenary allies increases damage and defence")
    public void assassinAllyBuff() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_allyBattleTest_assassinAllies",
                "c_allyBattleTest_multipleAllies");

        String assassinId = TestUtils.getEntitiesStream(res,
                "assassin").findFirst().get().getId();

        String mercSecondId = TestUtils.getEntitiesStream(res,
                "mercenary").skip(1).findFirst().get().getId();

        // Pick up treasure
        res = dmc.tick(Direction.DOWN);

        // Pick up second treasure
        res = dmc.tick(Direction.DOWN);

        res = dmc.tick(Direction.DOWN);
        // Bribe assassin
        res = assertDoesNotThrow(() -> dmc.interact(mercSecondId));

        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        res = dmc.tick(Direction.DOWN);

        res = assertDoesNotThrow(() -> dmc.interact(assassinId));

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        // Battle 1st mercenary
        DungeonResponse postBattleResponse = dmc.tick(Direction.UP);
        List<EntityResponse> entities = postBattleResponse.getEntities();

        // Only your allies should be alive
        assertTrue(TestUtils.countEntityOfType(entities, "mercenary") == 1);
        assertTrue(TestUtils.countEntityOfType(entities, "assassin") == 1);

        BattleResponse battle = res.getBattles().get(0);

        RoundResponse firstRound = battle.getRounds().get(0);

        // (15 - 10) / 10 == 0.5 damage to player
        assertEquals(0.5, -firstRound.getDeltaCharacterHealth(), 0.001);
    }
}
