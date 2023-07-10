package dungeonmania.entities.behaviours;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public interface MovedAwayBehaviour {
    public void onMovedAway(GameMap map, Entity entity);
}
