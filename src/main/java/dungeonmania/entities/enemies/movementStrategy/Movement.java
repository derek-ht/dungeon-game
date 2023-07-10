package dungeonmania.entities.enemies.movementStrategy;

import dungeonmania.Game;
import dungeonmania.entities.Entity;

public interface Movement {
    public void move(Game game, Entity entity);
}
