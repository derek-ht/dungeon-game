package dungeonmania.entities.enemies;

import java.util.Random;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {

    public static final double DEFAULT_RATE = 0.0;

    private double bribeRate = Assassin.DEFAULT_RATE;
    private static Random random = new Random();

    public Assassin(Position position, double health, double attack, int bribeAmount, int bribeRadius,
            double bribeRate) {
        super(position, health, attack, bribeAmount, bribeRadius);
        this.bribeRate = bribeRate;
    }

    @Override
    public void interact(Player player, Game game) {
        bribe(player);
        if (random.nextDouble() > bribeRate) {
            this.setAllied(true);
        }
    }

}
