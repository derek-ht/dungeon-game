package dungeonmania;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalFactory;
import dungeonmania.map.GameMap;
import dungeonmania.map.GraphNode;
import dungeonmania.map.GraphNodeFactory;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

/**
 * GameBuilder -- A builder to build up the whole game
 *
 * @author Webster Zhang
 * @author Tina Ji
 */
public class GameBuilder {
    private String configName;
    private String dungeonName;

    private JSONObject config;
    private JSONObject dungeon;

    public GameBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public GameBuilder setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
        return this;
    }

    public Game buildGame() {
        loadConfig();
        loadDungeon();
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }

        Game game = new Game(dungeonName);
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);
        game.init();

        return game;
    }

    private void loadConfig() {
        String configFile = String.format("/configs/%s.json", configName);
        try {
            config = new JSONObject(FileLoader.loadResourceFile(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            config = null;
        }
    }

    private void loadDungeon() {
        String dungeonFile = String.format("/dungeons/%s.json", dungeonName);
        try {
            dungeon = new JSONObject(FileLoader.loadResourceFile(dungeonFile));
        } catch (IOException e) {
            dungeon = null;
        }
    }

    private void buildMap(Game game) {
        GameMap map = new GameMap();
        map.setGame(game);
        dungeon.getJSONArray("entities").forEach(e -> {
            JSONObject jsonEntity = (JSONObject) e;
            GraphNode newNode = GraphNodeFactory.createEntity(jsonEntity, game.getEntityFactory());
            Entity entity = newNode.getEntities().get(0);

            if (newNode != null) {
                map.addNode(newNode);
            }

            if (entity instanceof Player)
                map.setPlayer((Player) entity);
        });
        game.setMap(map);
    }

    public void buildGoals(Game game) {
        if (!dungeon.isNull("goal-condition")) {
            Goal goal = GoalFactory.createGoal(dungeon.getJSONObject("goal-condition"), config);
            game.setGoals(goal);
        }
    }

    private static boolean[][] randomizedPrims(int width, int height) {
        boolean[][] maze = new boolean[width][height];
        // Start is empty
        maze[1][1] = true;

        List<Position> options = new ArrayList<>();

        // Add non boundary neighbours of start to options
        options.add(new Position(1, 3));
        options.add(new Position(3, 1));

        while (!options.isEmpty()) {
            Position next = options.remove(new Random().nextInt(options.size()));
            List<Position> neighbours = new ArrayList<Position>();

            // add each neighbour of distance 2 from next not on boundary that are empty
            if (next.getX() + 2 < width)
                neighbours.add(new Position(next.getX() + 2, next.getY()));

            if (next.getX() - 2 > 0)
                neighbours.add(new Position(next.getX() - 2, next.getY()));

            if (next.getY() + 2 < height)
                neighbours.add(new Position(next.getX(), next.getY() + 2));

            if (next.getY() - 2 > 0)
                neighbours.add(new Position(next.getX(), next.getY() - 2));

            List<Position> emptyNeighbours = new ArrayList<>(
                    neighbours.stream().filter(neighbour -> maze[neighbour.getX()][neighbour.getY()])
                            .collect(Collectors.toList()));

            if (!emptyNeighbours.isEmpty()) {
                Position neighbour = emptyNeighbours.get(new Random().nextInt(emptyNeighbours.size()));
                // maze[ next ] = empty (i.e. true)
                maze[next.getX()][next.getY()] = true;

                // maze[ position inbetween next and neighbour ] = empty (i.e. true)
                int inBetweenX = next.getX();
                int inBetweenY = next.getY();
                if (neighbour.getX() > next.getX()) {
                    inBetweenX = next.getX() + 1;
                } else if (neighbour.getX() < next.getX()) {
                    inBetweenX = next.getX() - 1;
                } else if (neighbour.getY() > next.getY()) {
                    inBetweenY = next.getY() + 1;
                } else if (neighbour.getY() < next.getY()) {
                    inBetweenY = next.getY() - 1;
                }
                maze[inBetweenX][inBetweenY] = true;

                // maze[ neighbour ] = empty (i.e. true)
                maze[neighbour.getX()][neighbour.getY()] = true;
            }

            // add to options all neighbours of 'next' not on boundary that are of
            // 2 away and are walls
            options.addAll(neighbours.stream().filter(neighbour -> !maze[neighbour.getX()][neighbour.getY()])
                    .collect(Collectors.toList()));
        }

        if (!maze[width - 2][height - 2]) {
            maze[width - 2][height - 2] = true;

            List<Position> neighbours = new ArrayList<Position>();
            neighbours.add(new Position(width - 2, height - 3));
            neighbours.add(new Position(width - 3, height - 2));

            // if there are no cells in neighbours that are empty:
            if (neighbours.stream().allMatch(neighbour -> !maze[neighbour.getX()][neighbour.getY()])) {
                Position neighbour = neighbours.get(new Random().nextInt(neighbours.size()));
                maze[neighbour.getX()][neighbour.getY()] = true;
            }
        }
        return maze;
    }

    private JSONObject createDungeon(boolean[][] boolMap, int xStart, int yStart, int xEnd, int yEnd) {
        JSONObject generatedDungeon = new JSONObject();
        JSONArray entities = new JSONArray();
        generatedDungeon.put("entities", entities);

        // Create player at start
        JSONObject player = new JSONObject();
        player.put("type", "player");
        player.put("x", 1 + xStart);
        player.put("y", 1 + yStart);
        entities.put(player);

        // Create exit at end
        JSONObject exit = new JSONObject();
        exit.put("type", "exit");
        exit.put("x", xEnd - 2);
        exit.put("y", yEnd - 2);
        entities.put(exit);

        for (int i = 0; i < xEnd - xStart; i++) {
            for (int j = 0; j < yEnd - yStart; j++) {
                // If the boolean is false, place wall at position
                // + x,y offset
                if (!boolMap[i][j] || j == yEnd - yStart - 1 || i == xEnd - xStart - 1) {
                    JSONObject wall = new JSONObject();
                    wall.put("type", "wall");
                    wall.put("x", i + xStart);
                    wall.put("y", j + yStart);
                    entities.put(wall);
                }
            }
        }

        generatedDungeon.put("goal-condition", new JSONObject().put("goal", "exit"));
        return generatedDungeon;
    }

    public Game buildGameGeneratedDungeon(int xStart, int yStart, int xEnd, int yEnd) {
        loadConfig();
        dungeon = createDungeon(randomizedPrims(xEnd - xStart, yEnd - yStart), xStart, yStart, xEnd, yEnd);
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }
        Game game = new Game("Generated Dungeon");
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);
        game.init();

        return game;
    }
}
