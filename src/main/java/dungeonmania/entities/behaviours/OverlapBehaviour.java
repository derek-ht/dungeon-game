package dungeonmania.entities.behaviours;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public interface OverlapBehaviour {
    public void onOverlap(GameMap map, Entity entity);
}
