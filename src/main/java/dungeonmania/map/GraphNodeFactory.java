package dungeonmania.map;

import org.json.JSONObject;

import dungeonmania.entities.EntityFactory;

public class GraphNodeFactory {
    public static GraphNode createEntity(JSONObject jsonEntity, EntityFactory factory) {
        return constructEntity(jsonEntity, factory);
    }

    private static GraphNode constructEntity(JSONObject jsonEntity, EntityFactory factory) {
        switch (jsonEntity.getString("type")) {
            case "player":
            case "zombie_toast":
            case "zombie_toast_spawner":
            case "assassin":
            case "mercenary":
            case "wall":
            case "boulder":
            case "switch":
            case "exit":
            case "treasure":
            case "wood":
            case "arrow":
            case "bomb":
            case "invisibility_potion":
            case "invincibility_potion":
            case "portal":
            case "sword":
            case "spider":
            case "sun_stone":
            case "door":
            case "light_bulb_off":
            case "wire":
            case "switch_door":
            case "key":
                return new GraphNode(factory.createEntity(jsonEntity));
            case "swamp_tile":
                return new GraphNode(factory.createEntity(jsonEntity), factory.getMovementFactor());
            default:
                return null;
        }
    }
}
