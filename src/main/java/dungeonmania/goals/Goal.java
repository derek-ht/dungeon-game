package dungeonmania.goals;

import java.io.Serializable;

import dungeonmania.Game;

public interface Goal extends Serializable {
    /**
     * @return true if the goal has been achieved, false otherwise
     */
    public abstract boolean achieved(Game game);

    public abstract String toString(Game game);
}
