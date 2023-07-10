package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Treasure;

public class TreasureNode implements Goal {
    private int target;

    public TreasureNode(int target) {
        this.target = target;
    }

    @Override
    public boolean achieved(Game game) {
        return game.getInitialTreasureCount() - getTreasureCount(game) >= target;
    }

    @Override
    public String toString(Game game) {
        if (this.achieved(game))
            return "";
        return ":treasure";
    }

    public int getTreasureCount(Game game) {
        return game.getEntities(Treasure.class).size() + game.getEntities(SunStone.class).size();
    }

}
