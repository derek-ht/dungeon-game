package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyGoalsTest {

    @Test
    @DisplayName("Testing enemies goal of needing to kill 1")
    public void enemyGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemy", "c_basicGoalsTest_enemy");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        for (int i = 0; i < 3; i++) {
            dmc.tick(Direction.LEFT);
        }
        res = dmc.tick(Direction.UP);
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    @DisplayName("Testing enemies goal of needing to destroy all spawners")
    public void enemyGoalWithSpawners() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_spawner", "c_basicGoalsTest_spawner");

        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Pick up the sword
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, TestUtils.countEntityOfTypeInInventory(res, "sword"));

        // Killed 1 enemy for target but goal still not achieved
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        dmc.tick(Direction.DOWN);
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));

        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
    }

    @Test
    @DisplayName("Testing enemies goal with OR")
    public void testOr() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_EnemyGoalsOrAll", "c_complexGoalsTest_orAll");

        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move onto exit
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName("Testing enemies goal with AND")
    public void testAnd() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemyGoalAndAll", "c_complexGoalsTest_andAll");

        System.out.println(TestUtils.getGoals(res));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // kill spider
        res = dmc.tick(Direction.RIGHT);
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        // pickup treasure
        res = dmc.tick(Direction.DOWN);
        assertEquals("", TestUtils.getGoals(res));

    }

}
