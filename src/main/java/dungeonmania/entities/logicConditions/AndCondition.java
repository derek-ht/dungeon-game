package dungeonmania.entities.logicConditions;

public class AndCondition implements Condition {

    @Override
    public Boolean checkConditions(int connected, int baseConnections, long count, long sameTickCount) {
        return (baseConnections >= 2 && connected == baseConnections);
    }
}
