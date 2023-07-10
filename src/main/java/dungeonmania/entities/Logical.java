package dungeonmania.entities;

import java.util.List;

import dungeonmania.entities.logicConditions.AndCondition;
import dungeonmania.entities.logicConditions.CoAndCondition;
import dungeonmania.entities.logicConditions.Condition;
import dungeonmania.entities.logicConditions.OrCondition;
import dungeonmania.entities.logicConditions.XorCondition;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Logical extends Entity {
    private Boolean activated = false;
    private int connected = 0;
    private String logic;
    private Condition condition;
    private long sameTickCount;

    public Logical(Position position, String logic) {
        super(position);
        this.logic = logic.toLowerCase();
        setCondition(this.logic);
    }

    public void activate(GameMap map, long count) {
        int baseConnections = getConnections(getPosition(), map);

        if (Math.abs(count) >= 2) {
            sameTickCount += count;
        }

        connected += count;
        activated = condition.checkConditions(connected, baseConnections, count, sameTickCount);
    }

    public int getConnections(Position position, GameMap map) {
        List<Position> posList = position.getCardinallyAdjacentPositions();
        int count = 0;

        for (Position p : posList) {
            List<Entity> entities = map.getEntities(p);
            for (Entity e : entities) {
                if (e instanceof Wire || e instanceof Switch) {
                    count++;
                }
            }
        }

        return count;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setCondition(String logic) {
        switch (logic) {
            case "and":
                condition = new AndCondition();
                break;
            case "or":
                condition = new OrCondition();
                break;
            case "xor":
                condition = new XorCondition();
                break;
            case "co_and":
                condition = new CoAndCondition();
                break;
            default:
                return;
        }
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

}
