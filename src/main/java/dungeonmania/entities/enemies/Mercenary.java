package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;
    private boolean allied = false;
    private boolean mindControlled = false;
    private int controlled = 0;
    private int duration;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
    }

    public boolean isAllied() {
        return allied;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        // Account for multiple allies in the same position
        if (entity instanceof Mercenary && ((Mercenary) entity).isAllied()
                && allied) {
            return true;
        } else {
            return entity instanceof Player;
        }
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied)
            return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     *
     * @param player
     * @return
     */
    public boolean canBeBribed(Player player) {
        return bribeRadius >= 0 && inRange(getPosition(), player.getPosition())
                && player.countEntityOfType(Treasure.class) >= bribeAmount;
    }

    // Check if mercenary is within bribe radius
    public boolean inRange(Position playerPos, Position mercPos) {
        return (Math.max(Math.abs(playerPos.getX() - mercPos.getX()),
                Math.abs(playerPos.getY() - mercPos.getY())) <= bribeRadius);
    }

    /**
     * bribe the merc
     */
    public void bribe(Player player) {
        Sceptre sceptre = player.hasItem(Sceptre.class);
        if (sceptre != null) {
            allied = mindControlled = true;
            duration = sceptre.getDuration();
            player.use(Sceptre.class);
            return;
        }

        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }

    }

    @Override
    public void interact(Player player, Game game) {
        allied = true;
        bribe(player);
    }

    @Override
    public void move(Game game) {
        Position nextPos = game.dijkstraPathFind(getPosition(), game.getPlayerPosition(), this);

        if (allied) {

            if (mindControlled) {
                controlled++;
            }

            if (game.getPlayerAdjacentPositions().contains(this.getPosition())
                    && game.getPlayerPreviousDistinctPosition() == null) {
                return;
            } else if (game.getPlayerAdjacentPositions().contains(this.getPosition())
                    || nextPos.equals(game.getPlayerPosition())) {
                game.moveTo(this, game.getPlayerPreviousDistinctPosition());
            } else {
                if (isStuck()) {
                    notControlledCheck();
                    return;
                }
                game.moveTo(this, nextPos);
            }
            notControlledCheck();

        } else if (isStuck()) {
            return;
        } else {
            // Follow hostile
            game.moveTo(this, nextPos);
        }
    }

    @Override
    public boolean isInteractable(Player player) {
        Sceptre sceptre = player.hasItem(Sceptre.class);
        if (sceptre != null) {
            return true;
        }
        return !allied && canBeBribed(player);
    }

    public void notControlledCheck() {
        if (controlled >= duration && controlled > 0) {
            allied = mindControlled = false;
            controlled = 0;
        }
    }

    public void setAllied(boolean allied) {
        this.allied = allied;
    }

    public boolean isMindControlled() {
        return mindControlled;
    }

    public void setMindControlled(boolean mindControlled) {
        this.mindControlled = mindControlled;
    }

}
