package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.behaviours.OverlapBehaviour;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Collectable;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.ZombieToast;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable, OverlapBehaviour {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;

    private PlayerState state;

    public Player(Position position, double health, double attack) {
        super(position);
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        state = new BaseState(this);
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public BattleItem getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables(GameMap map) {
        int zombieCount = map.getEntities(ZombieToast.class).size();
        return inventory.getBuildables(zombieCount);
    }

    public boolean build(String entity, EntityFactory factory) {
        InventoryItem item = inventory.checkBuildCriteria(this, entity, factory);
        if (item == null)
            return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied())
                    return;
            }
            map.battle(this, (Enemy) entity);
        } else if (entity instanceof Collectable) {
            if (this.pickUp(entity)) {
                map.destroyEntity(entity);
            }
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        if (inventory.getEntities(Key.class).size() >= 1 && item instanceof Key) {
            return false;
        }
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null)
            inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            state.transitionBase();
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        } else {
            state.transitionInvisible();
        }
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        return state.buff(origin);
    }

    public double getInitialPlayerHealth() {
        return battleStatistics.getHealth();
    }

    public BattleStatistics applyBuffs(List<BattleItem> battleItems, BattleStatistics playerBuff) {
        Potion effectivePotion = getEffectivePotion();
        if (effectivePotion != null) {
            playerBuff = applyBuff(playerBuff);
        } else {
            for (BattleItem item : inventory.getEntities(BattleItem.class)) {
                if (!(item instanceof Potion)) {
                    playerBuff = item.applyBuff(playerBuff);
                    battleItems.add(item);
                }
            }
        }
        return playerBuff;
    }

    public BattleStatistics updateStats(BattleStatistics playerBuff) {
        return BattleStatistics.applyBuff(battleStatistics, playerBuff);
    }

    public boolean isStatsEnabled(BattleStatistics playerStats) {
        return playerStats.isEnabled();
    }

    public void updateHealth(double health) {
        battleStatistics.setHealth(health);
    }

    public void useItems(Game game, List<BattleItem> battleItems) {
        for (BattleItem item : battleItems) {
            if (item instanceof InventoryItem)
                item.use(game);
        }
    }

    public <T extends InventoryItem> T hasItem(Class<T> itemType) {
        return inventory.getFirst(itemType);
    }

    public void useWeapon(Game game) {
        getWeapon().use(game);
    }

    public BattleStatistics getAllyBuffs(Game game) {
        int allyCount = (int) game.getEntities(Mercenary.class).stream().filter(Mercenary::isAllied).count();
        return new BattleStatistics(
                0,
                allyCount * game.getAllyAttack(),
                allyCount * game.getAllyDefence(),
                1,
                1,
                false,
                true);
    }
}
