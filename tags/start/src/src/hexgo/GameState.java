package hexgo;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Jan 6, 2003
 * Time: 10:34:12 AM
 * To change this template use Options | File Templates.
 */
public class GameState implements Serializable {
    private Player[] players;
    private int turn;
    private Sequence rows;		// the rows of tiles/

    public GameState(Player[] players, int turn, Sequence rows) {
        this.players = players;
        this.turn = turn;
        this.rows = rows;
    }

    public GameState(GameState previous, Sequence rows) {
        this(previous.players, previous.turn + 1, rows);
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getTurn() {
        return turn;
    }

    public Sequence getRows() {
        return rows;
    }
}
