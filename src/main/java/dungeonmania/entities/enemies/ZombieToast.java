package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.enemies.movementStrategy.Movement;
import dungeonmania.entities.enemies.movementStrategy.RandomMovement;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;

    private Movement movement = new RandomMovement();

    public ZombieToast(Position position, double health, double attack) {
        super(position, health, attack);
    }

    @Override
    public void move(Game game) {
        if (isStuck())
            return;
        movement.move(game, this);
    }

}
