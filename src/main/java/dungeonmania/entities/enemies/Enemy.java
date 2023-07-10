package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.behaviours.DestroyBehaviour;
import dungeonmania.entities.behaviours.OverlapBehaviour;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends Entity implements Battleable, OverlapBehaviour, DestroyBehaviour {
    private BattleStatistics battleStatistics;
    private int movementFactor = -1;
    private int stuckDuration = 0;

    public Enemy(Position position, double health, double attack) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            map.battle(player, this);
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        map.unsubscribe(getId());
    }

    public abstract void move(Game game);

    public double getInitialEnemyHealth() {
        return battleStatistics.getHealth();
    }

    public boolean isStatsEnabled() {
        return battleStatistics.isEnabled();
    }

    public void updateHealth(double health) {
        battleStatistics.setHealth(health);
    }

    public void setMovementFactor(int movementFactor) {
        this.movementFactor = movementFactor;
    }

    public boolean isStuck() {
        if (stuckDuration < movementFactor) {
            stuckDuration++;
            return true;
        } else {
            stuckDuration = 0;
            movementFactor = -1;
            return false;
        }
    }
}
