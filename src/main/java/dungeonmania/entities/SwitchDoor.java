package dungeonmania.entities;

import dungeonmania.entities.enemies.Spider;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends Logical {
    public SwitchDoor(Position position, String logic) {
        super(position, logic);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (this.getActivated() || entity instanceof Spider) {
            return true;
        }
        return false;
    }
}
