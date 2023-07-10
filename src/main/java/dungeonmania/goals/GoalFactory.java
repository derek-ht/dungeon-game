package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoalFactory {
    public static Goal createGoal(JSONObject jsonGoal, JSONObject config) {
        JSONArray subgoals;
        Goal g1;
        Goal g2;
        switch (jsonGoal.getString("goal")) {
            case "AND":
                subgoals = jsonGoal.getJSONArray("subgoals");
                g1 = createGoal(subgoals.getJSONObject(0), config);
                g2 = createGoal(subgoals.getJSONObject(1), config);
                return new AndGoal(g1, g2);
            case "OR":
                subgoals = jsonGoal.getJSONArray("subgoals");
                g1 = createGoal(subgoals.getJSONObject(0), config);
                g2 = createGoal(subgoals.getJSONObject(1), config);
                return new OrGoal(g1, g2);
            case "exit":
                return new ExitNode();
            case "boulders":
                return new BoulderNode();
            case "treasure":
                int treasureGoal = config.optInt("treasure_goal", 1);
                return new TreasureNode(treasureGoal);
            case "enemies":
                int enemyGoal = config.optInt("enemy_goal", 1);
                return new EnemiesNode(enemyGoal);
            default:
                return null;
        }
    }
}
