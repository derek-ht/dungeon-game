package dungeonmania.entities.logicConditions;

public class OrCondition implements Condition {

    @Override
    public Boolean checkConditions(int connected, int baseConnections, long count, long sameTickCount) {
        return connected >= 1;
    }
}
