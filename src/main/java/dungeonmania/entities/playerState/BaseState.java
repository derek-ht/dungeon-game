package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public class BaseState extends PlayerState {

    private Player player = getPlayer();

    public BaseState(Player player) {
        super(player, false, false);
    }

    @Override
    public void transitionBase() {
        // Do nothing
    }

    @Override
    public void transitionInvincible() {
        player.changeState(new InvincibleState(player));
    }

    @Override
    public void transitionInvisible() {
        player.changeState(new InvisibleState(player));
    }

    @Override
    public BattleStatistics buff(BattleStatistics origin) {
        return origin;
    }
}
