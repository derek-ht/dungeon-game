package dungeonmania.entities.logicConditions;

public interface Condition {
    public Boolean checkConditions(int connected, int baseConnections, long count, long sameTickCount);
}
