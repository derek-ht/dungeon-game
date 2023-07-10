package dungeonmania.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dungeonmania.entities.behaviours.MovedAwayBehaviour;
import dungeonmania.entities.behaviours.OverlapBehaviour;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Entity implements OverlapBehaviour, MovedAwayBehaviour {
    private boolean activated;
    private List<Bomb> bombs = new ArrayList<>();
    private List<Logical> logicals = new ArrayList<>();

    public Switch(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (activated) {
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = true;
            bombs.stream().forEach(b -> b.notify(map));
            logicals = new ArrayList<>();
            checkWires(map);
            Set<Logical> noDupe = new HashSet<>(logicals);
            for (Logical l : noDupe) {
                long count = logicals.stream().filter(e -> e.equals(l)).count();
                l.activate(map, count);
            }
        }

    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = false;
            Set<Logical> noDupe = new HashSet<>(logicals);
            for (Logical l : noDupe) {
                long count = logicals.stream().filter(e -> e.equals(l)).count();
                l.activate(map, -count);
            }
        }

    }

    public boolean isActivated() {
        return activated;
    }

    public void checkWires(GameMap map) {
        List<Position> pos = getCardinallyAdjacentPositions();
        for (Position p : pos) {
            List<Entity> entities = map.getEntities(p);
            for (Entity e : entities) {
                if (e instanceof Wire) {
                    ((Wire) e).conduct(map, p, logicals);
                } else if (e instanceof Logical) {
                    logicals.add((Logical) e);
                }
            }
        }
    }

}
