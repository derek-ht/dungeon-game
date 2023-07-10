package dungeonmania.entities;

import dungeonmania.entities.behaviours.OverlapBehaviour;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwampTile extends Entity implements OverlapBehaviour {
    private int movementFactor;

    public SwampTile(Position position, int movementFactor) {
        super(position);
        this.movementFactor = movementFactor;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player || (entity instanceof Mercenary && ((Mercenary) entity).isAllied()
                && map.getPlayer().getPosition().getCardinallyAdjacentPositions().contains(entity.getPosition()))) {
            return;
        } else if (entity instanceof Enemy) {
            ((Enemy) entity).setMovementFactor(movementFactor);
        }
    }

    public int getMovementFactor() {
        return movementFactor;
    }

}
