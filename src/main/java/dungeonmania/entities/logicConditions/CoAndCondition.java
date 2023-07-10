package dungeonmania.entities.logicConditions;


public class CoAndCondition implements Condition {

    @Override
    public Boolean checkConditions(int connected, int baseConnections, long count, long sameTickCount) {
        return count >= 2 && connected == count
            || sameTickCount == connected && sameTickCount >= 0 && sameTickCount == Math.abs(count);
    }
}
