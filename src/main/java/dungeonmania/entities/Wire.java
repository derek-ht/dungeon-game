package dungeonmania.entities;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wire extends Entity {
    private ArrayList<Position> checked = new ArrayList<>();

    public Wire(Position position) {
        super(position);
    }

    public void conduct(GameMap map, Position pos, List<Logical> logicals) {
        List<Position> posList = getCardinallyAdjacentPositions();

        for (Position p : posList) {
            Entity e = map.logicEntity(p);
            if (checked.contains(p)) {
                return;
            }

            checked.add(p);

            if (e instanceof Wire) {
                ((Wire) e).conduct(map, p, logicals);
            } else if (e instanceof Logical) {
                logicals.add((Logical) e);
            }
        }
        checked = new ArrayList<>();
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

}
