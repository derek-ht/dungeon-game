package dungeonmania.battles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.NameConverter;

public class BattleFacade implements Serializable {
    private List<BattleResponse> battleResponses = new ArrayList<>();

    public void battle(Game game, Player player, Enemy enemy) {
        // 0. init
        double initialPlayerHealth = player.getInitialPlayerHealth();
        double initialEnemyHealth = enemy.getInitialEnemyHealth();
        String enemyString = NameConverter.toSnakeCase(enemy);

        // 1. apply buff provided by the game and player's inventory
        // getting buffing amount
        List<BattleItem> battleItems = new ArrayList<>();
        BattleStatistics playerBuff = new BattleStatistics(0, 0, 0, 1, 1, false, true);
        playerBuff = player.applyBuffs(battleItems, playerBuff);

        // 2. Battle the two stats
        BattleStatistics playerBattleStatistics = player.updateStats(playerBuff);

        // // Apply ally buffs
        playerBattleStatistics = BattleStatistics.applyBuff(playerBattleStatistics,
                player.getAllyBuffs(game));

        BattleStatistics enemyBattleStatistics = enemy.getBattleStatistics();
        if (!player.isStatsEnabled(playerBattleStatistics) || !enemy.isStatsEnabled())
            return;

        List<BattleRound> rounds = BattleStatistics.battle(playerBattleStatistics, enemyBattleStatistics);

        // 3. update health to the actual statistics
        player.updateHealth(playerBattleStatistics.getHealth());
        enemy.updateHealth(enemyBattleStatistics.getHealth());

        // 4. call to decrease durability of items
        player.useItems(game, battleItems);

        // 5. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(
                enemyString,
                rounds.stream()
                        .map(ResponseBuilder::getRoundResponse)
                        .collect(Collectors.toList()),
                battleItems.stream()
                        .map(Entity.class::cast)
                        .map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList()),
                initialPlayerHealth,
                initialEnemyHealth));
    }

    public List<BattleResponse> getBattleResponses() {
        return battleResponses;
    }
}
