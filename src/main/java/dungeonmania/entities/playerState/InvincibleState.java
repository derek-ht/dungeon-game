package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.Player;

public class InvincibleState extends PlayerState {

    private Player player = getPlayer();

    public InvincibleState(Player player) {
        super(player, true, false);
    }

    @Override
    public void transitionBase() {
        player.changeState(new BaseState(player));
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
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                0,
                0,
                1,
                1,
                true,
                true));
    }
}
