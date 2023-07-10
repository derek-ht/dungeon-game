package dungeonmania.entities.playerState;

import java.io.Serializable;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public abstract class PlayerState implements Serializable {
    private Player player;
    private boolean isInvincible = false;
    private boolean isInvisible = false;

    PlayerState(Player player, boolean isInvincible, boolean isInvisible) {
        this.player = player;
        this.isInvincible = isInvincible;
        this.isInvisible = isInvisible;
    }

    public boolean isInvincible() {
        return isInvincible;
    };

    public boolean isInvisible() {
        return isInvisible;
    };

    public Player getPlayer() {
        return player;
    }

    public abstract void transitionInvisible();

    public abstract void transitionInvincible();

    public abstract void transitionBase();

    public abstract BattleStatistics buff(BattleStatistics origin);

}
