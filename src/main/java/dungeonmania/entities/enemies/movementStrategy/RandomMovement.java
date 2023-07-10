package dungeonmania.entities.enemies.movementStrategy;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class RandomMovement implements Movement {
    @Override
    public void move(Game game, Entity entity) {
        Position nextPos;
        Random randGen = new Random();
        List<Position> pos = entity.getCardinallyAdjacentPositions();
        pos = pos
                .stream()
                .filter(p -> game.canMoveTo(entity, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            nextPos = entity.getPosition();
            game.moveTo(entity, nextPos);
        } else {
            nextPos = pos.get(randGen.nextInt(pos.size()));
            game.moveTo(entity, nextPos);
        }
    }

}
