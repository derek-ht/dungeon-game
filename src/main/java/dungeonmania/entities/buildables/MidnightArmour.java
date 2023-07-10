package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;

public class MidnightArmour extends Buildable implements BattleItem {
    public static final double DEFAULT_ATTACK = 2.0;
    public static final double DEFAULT_DEFENCE = 2.0;

    private double midnightAttack;
    private double midnightDefence;

    public MidnightArmour(double midnightAttack, double midnightDefence) {
        super(null);
        this.midnightAttack = midnightAttack;
        this.midnightDefence = midnightDefence;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            midnightAttack,
            midnightDefence,
            1,
            1));
    }

    @Override
    public void use(Game game) {
    }

    @Override
    public int getDurability() {
        return 1;
    }

}
