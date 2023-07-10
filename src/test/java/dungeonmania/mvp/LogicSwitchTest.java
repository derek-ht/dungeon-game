package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogicSwitchTest {

    private boolean boulderAt(DungeonResponse res, int x, int y) {
        Position pos = new Position(x, y);
        return TestUtils.getEntitiesStream(res, "boulder").anyMatch(
                it -> it.getPosition().equals(pos));
    }

    @Test
    @DisplayName("Test and logic works")
    public void logicSwitchAnd() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitchAnd", "c_assassinTest");

        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an unactivated lightbulb on the map
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 2 wires connected
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // No off light bulbs on the map anymore
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());

        // Makes sure it turns off again once condition is not satisfied
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Test or logic works")
    public void logicSwitchOr() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitchOr", "c_assassinTest");

        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an unactivated lightbulb on the map
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 3 wires connected (2 on 1 off)
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // No off light bulbs on the map anymore
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());

        // Makes sure it turns off again once condition is not satisfied
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Test xor logic works")
    public void logicSwitchXor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitchXor", "c_assassinTest");

        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an unactivated lightbulb on the map
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 3 wires connected (2 on 1 off)
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // Light bulb does not get turned on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // Makes sure it turns off again once condition is not satisfied
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 3 wires connected (1 on 2 off)
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // Light bulb turns on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Test co_and logic works")
    public void logicSwitchCoAnd() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitchCoAnd", "c_assassinTest");

        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an unactivated lightbulb on the map
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 4 wires connected (2 on 2 off)
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // Light bulb does get turned on
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());

        // Makes sure it turns off again once condition is not satisfied
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 4 wires connected (1 on 3 off)
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Light bulb does not turn on
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Move boulder on switch (2 on 2 off)
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        // Light bulb should not be on even with 2 activated
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }

    @Test
    @DisplayName("Test switch door works")
    public void switchDoorWork() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor", "c_assassinTest");

        Position pos = TestUtils.getEntities(res, "switch_door").get(0).getPosition();
        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an locked door on the map
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
        assertEquals(0, TestUtils.getEntities(res, "switch_door_open").size());

        // Player moves boulder onto switch with 3 wires connected (2 on 1 off)
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // Door opens
        assertEquals(1, TestUtils.getEntities(res, "switch_door_open").size());

        // Try to walk onto door
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        // Checks if they are ontop of each other
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Test switch door works and lightbulb from one switch")
    public void bothOneSwitch() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bothLogical", "c_assassinTest");

        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an locked door on the map
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
        assertEquals(0, TestUtils.getEntities(res, "switch_door_open").size());

        // Asserts there is a turned off light bulb
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 3 wires connected (2 on 1 off)
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // Door should open and light bulb should light up
        assertEquals(1, TestUtils.getEntities(res, "switch_door_open").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
    }

    @Test
    @DisplayName("Test switch door works and lightbulb from one switch but one fails condition")
    public void bothOneSwitchOneFail() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bothLogicalFail", "c_assassinTest");

        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an locked door on the map
        assertEquals(1, TestUtils.getEntities(res, "switch_door").size());
        assertEquals(0, TestUtils.getEntities(res, "switch_door_open").size());

        // Asserts there is a turned off light bulb
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 3 wires connected (2 on 1 off)
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // Door should open but light bulb should not light up
        assertEquals(1, TestUtils.getEntities(res, "switch_door_open").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
    }

    @Test
    @DisplayName("Test Two switches each connected to 2 wires")
    public void doubleCoAnd() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_doubleDouble", "c_assassinTest");

        assertTrue(boulderAt(res, 3, 1));

        // Asserts that there is an unactivated lightbulb on the map
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 4 wires connected (2 on 2 off)
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(boulderAt(res, 4, 1));

        // Light bulb does not get turned on
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());

        // Makes sure it turns off again once condition is not satisfied
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Player moves boulder onto switch with 4 wires connected (2 on 2 off)
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // Light bulb turns on
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());

        // Push second boulder onto switch (4 wires 4 activated)
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Light bulb should turn off after boulder is on switch
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());

        // Move boulder off switch
        res = dmc.tick(Direction.DOWN);
        // Light bulb should turn back on after boulder mvoes off
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
    }
}
