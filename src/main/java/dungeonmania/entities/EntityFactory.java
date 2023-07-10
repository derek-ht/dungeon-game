package dungeonmania.entities;

import dungeonmania.Game;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.MidnightArmour;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.*;
import dungeonmania.entities.enemies.*;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.util.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class EntityFactory implements Serializable {
    private static JSONObject config;
    private Random ranGen = new Random();

    public EntityFactory(JSONObject config) {
        EntityFactory.config = config;
    }

    public Entity createEntity(JSONObject jsonEntity) {
        return constructEntity(jsonEntity, config);
    }

    public void spawnSpider(Game game) {
        int tick = game.getTick();
        int rate = config.optInt("spider_spawn_interval", 0);
        if (rate == 0 || (tick + 1) % rate != 0)
            return;
        int radius = 20;
        Position player = game.playerPosition();

        Spider dummySpider = buildSpider(new Position(0, 0)); // for checking possible positions

        List<Position> availablePos = new ArrayList<>();
        for (int i = player.getX() - radius; i < player.getX() + radius; i++) {
            for (int j = player.getY() - radius; j < player.getY() + radius; j++) {
                if (Position.calculatePositionBetween(player, new Position(i, j)).magnitude() > radius)
                    continue;
                Position np = new Position(i, j);
                if (!game.canMoveTo(dummySpider, np))
                    continue;
                availablePos.add(np);
            }
        }
        Position initPosition = availablePos.get(ranGen.nextInt(availablePos.size()));
        Spider spider = buildSpider(initPosition);
        game.addEntity(spider);
        game.register(() -> spider.move(game), Game.AI_MOVEMENT, spider.getId());
    }

    public static void spawnZombie(Game game, ZombieToastSpawner spawner) {
        int tick = game.getTick();
        Random randGen = new Random();
        int spawnInterval = config.optInt("zombie_spawn_interval", ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL);
        if (spawnInterval == 0 || (tick + 1) % spawnInterval != 0)
            return;
        List<Position> pos = spawner.getCardinallyAdjacentPositions();
        pos = pos
                .stream()
                .filter(p -> !game.getEntities(p).stream().anyMatch(e -> (e instanceof Wall)))
                .collect(Collectors.toList());
        if (pos.size() == 0)
            return;
        ZombieToast zt = buildZombieToast(pos.get(randGen.nextInt(pos.size())));
        game.addEntity(zt);
        game.register(() -> zt.move(game), Game.AI_MOVEMENT, zt.getId());
    }

    public Spider buildSpider(Position pos) {
        double spiderHealth = config.optDouble("spider_health", Spider.DEFAULT_HEALTH);
        double spiderAttack = config.optDouble("spider_attack", Spider.DEFAULT_ATTACK);
        return new Spider(pos, spiderHealth, spiderAttack);
    }

    public Player buildPlayer(Position pos) {
        double playerHealth = config.optDouble("player_health", Player.DEFAULT_HEALTH);
        double playerAttack = config.optDouble("player_attack", Player.DEFAULT_ATTACK);
        return new Player(pos, playerHealth, playerAttack);
    }

    public static ZombieToast buildZombieToast(Position pos) {
        double zombieHealth = config.optDouble("zombie_health", ZombieToast.DEFAULT_HEALTH);
        double zombieAttack = config.optDouble("zombie_attack", ZombieToast.DEFAULT_ATTACK);
        return new ZombieToast(pos, zombieHealth, zombieAttack);
    }

    public ZombieToastSpawner buildZombieToastSpawner(Position pos) {
        int zombieSpawnRate = config.optInt("zombie_spawn_interval", ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL);
        return new ZombieToastSpawner(pos, zombieSpawnRate);
    }

    public Mercenary buildMercenary(Position pos) {
        double mercenaryHealth = config.optDouble("mercenary_health", Mercenary.DEFAULT_HEALTH);
        double mercenaryAttack = config.optDouble("mercenary_attack", Mercenary.DEFAULT_ATTACK);
        int mercenaryBribeAmount = config.optInt("bribe_amount", Mercenary.DEFAULT_BRIBE_AMOUNT);
        int mercenaryBribeRadius = config.optInt("bribe_radius", Mercenary.DEFAULT_BRIBE_RADIUS);
        return new Mercenary(pos, mercenaryHealth, mercenaryAttack, mercenaryBribeAmount, mercenaryBribeRadius);
    }

    public Assassin buildAssassin(Position pos) {
        double assassinHealth = config.optDouble("assassin_health", Mercenary.DEFAULT_HEALTH);
        double assassinAttack = config.optDouble("assassin_attack", Mercenary.DEFAULT_ATTACK);
        int assassinBribeAmount = config.optInt("assassin_bribe_amount", Mercenary.DEFAULT_BRIBE_AMOUNT);
        int assassinBribeRadius = config.optInt("bribe_radius", Mercenary.DEFAULT_BRIBE_AMOUNT);
        double assassinBribeRate = config.optDouble("assassin_bribe_fail_rate", Mercenary.DEFAULT_ATTACK);
        return new Assassin(pos, assassinHealth, assassinAttack, assassinBribeAmount, assassinBribeRadius,

                assassinBribeRate);
    }

    public Bow buildBow(List<InventoryItem> items, Inventory inventory) {
        List<Wood> wood = inventory.getEntities(Wood.class);
        List<Arrow> arrows = inventory.getEntities(Arrow.class);
        items.remove(wood.get(0));
        items.remove(arrows.get(0));
        items.remove(arrows.get(1));
        items.remove(arrows.get(2));

        int bowDurability = config.optInt("bow_durability");
        return new Bow(bowDurability);
    }

    public Shield buildShield(List<InventoryItem> items, Inventory inventory) {
        List<Treasure> treasure = inventory.getEntities(Treasure.class);
        List<Key> keys = inventory.getEntities(Key.class);
        List<Wood> wood = inventory.getEntities(Wood.class);
        items.remove(wood.get(0));
        items.remove(wood.get(1));
        if (treasure.size() >= 1) {
            items.remove(treasure.get(0));
        } else if (keys.size() >= 1) {
            items.remove(keys.get(0));
        }

        int shieldDurability = config.optInt("shield_durability");
        double shieldDefence = config.optInt("shield_defence");
        return new Shield(shieldDurability, shieldDefence);
    }

    public Sceptre buildSceptre(List<InventoryItem> items, Inventory inventory) {
        List<Wood> wood = inventory.getEntities(Wood.class);
        List<Arrow> arrows = inventory.getEntities(Arrow.class);
        List<Treasure> treasure = inventory.getEntities(Treasure.class);
        List<Key> keys = inventory.getEntities(Key.class);
        List<SunStone> sunStones = inventory.getEntities(SunStone.class);

        if (wood.size() >= 1) {
            items.remove(wood.get(0));
        } else if (arrows.size() >= 2) {
            items.remove(arrows.get(0));
            items.remove(arrows.get(1));
        }

        if (keys.size() >= 1) {
            items.remove(keys.get(0));
        } else if (treasure.size() >= 1) {
            items.remove(treasure.get(0));
        }

        items.remove(sunStones.get(0));

        int mindControlDuration = config.optInt("mind_control_duration");
        return new Sceptre(mindControlDuration);
    }

    public MidnightArmour buildMidnightArmour(List<InventoryItem> items, Inventory inventory) {
        List<SunStone> sunStones = inventory.getEntities(SunStone.class);
        List<Sword> swords = inventory.getEntities(Sword.class);

        items.remove(swords.get(0));
        items.remove(sunStones.get(0));

        double midnightAttack = config.optDouble("midnight_armour_attack", MidnightArmour.DEFAULT_ATTACK);
        double midnightDefence = config.optDouble("midnight_armour_defense", MidnightArmour.DEFAULT_DEFENCE);
        return new MidnightArmour(midnightAttack, midnightDefence);
    }

    public SwampTile buildSwampTile(Position pos) {
        return new SwampTile(pos, getMovementFactor());
    }

    public int getMovementFactor() {
        return config.optInt("movement_factor");
    }

    public int getAllyAttack() {
        return config.optInt("ally_attack");
    }

    public int getAllyDefence() {
        return config.optInt("ally_defence");
    }

    public LightBulb buildLightBulb(Position pos, JSONObject jsonEntity) {
        String logic = jsonEntity.getString("logic");
        return new LightBulb(pos, logic);
    }

    public SwitchDoor buildSwitchDoor(Position pos, JSONObject jsonEntity) {
        String logic = jsonEntity.getString("logic");
        return new SwitchDoor(pos, logic);
    }

    private Entity constructEntity(JSONObject jsonEntity, JSONObject config) {
        Position pos = new Position(jsonEntity.getInt("x"), jsonEntity.getInt("y"));

        switch (jsonEntity.getString("type")) {
            case "player":
                return buildPlayer(pos);
            case "zombie_toast":
                return buildZombieToast(pos);
            case "zombie_toast_spawner":
                return buildZombieToastSpawner(pos);
            case "assassin":
                return buildAssassin(pos);
            case "mercenary":
                return buildMercenary(pos);
            case "wall":
                return new Wall(pos);
            case "wire":
                return new Wire(pos);
            case "light_bulb_off":
                return buildLightBulb(pos, jsonEntity);
            case "switch_door":
                return buildSwitchDoor(pos, jsonEntity);
            case "boulder":
                return new Boulder(pos);
            case "switch":
                return new Switch(pos);
            case "exit":
                return new Exit(pos);
            case "treasure":
                return new Treasure(pos);
            case "wood":
                return new Wood(pos);
            case "arrow":
                return new Arrow(pos);
            case "sun_stone":
                return new SunStone(pos);
            case "bomb":
                int bombRadius = config.optInt("bomb_radius", Bomb.DEFAULT_RADIUS);
                return new Bomb(pos, bombRadius);
            case "invisibility_potion":
                int invisibilityPotionDuration = config.optInt(
                        "invisibility_potion_duration",
                        InvisibilityPotion.DEFAULT_DURATION);
                return new InvisibilityPotion(pos, invisibilityPotionDuration);
            case "invincibility_potion":
                int invincibilityPotionDuration = config.optInt("invincibility_potion_duration",
                        InvincibilityPotion.DEFAULT_DURATION);
                return new InvincibilityPotion(pos, invincibilityPotionDuration);
            case "portal":
                return new Portal(pos, ColorCodedType.valueOf(jsonEntity.getString("colour")));
            case "sword":
                double swordAttack = config.optDouble("sword_attack", Sword.DEFAULT_ATTACK);
                int swordDurability = config.optInt("sword_durability", Sword.DEFAULT_DURABILITY);
                return new Sword(pos, swordAttack, swordDurability);
            case "spider":
                return buildSpider(pos);
            case "door":
                return new Door(pos, jsonEntity.getInt("key"));
            case "key":
                return new Key(pos, jsonEntity.getInt("key"));
            case "swamp_tile":
                return buildSwampTile(pos);
            default:
                return null;
        }
    }
}
