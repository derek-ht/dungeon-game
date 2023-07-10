package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.enemies.ZombieToastSpawner;

public class EnemiesNode implements Goal {
    private int target;

    public EnemiesNode(int target) {
        this.target = target;
    }

    @Override
    public boolean achieved(Game game) {
        return game.getEntities(ZombieToastSpawner.class).size() == 0 && game.getKills() >= target;
    }

    @Override
    public String toString(Game game) {
        if (this.achieved(game))
            return "";
        return ":enemies";
    }

}
