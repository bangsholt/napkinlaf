// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 7:42:14 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster;

public abstract class Move extends GameEvent {
    private long playerID;
    private int turn;

    public Move(Object eventSource) {
        super(eventSource);
    }

    public long getPlayerID() {
        return playerID;
    }

    public int getTurn() {
        return turn;
    }

    protected void setPlayerID(long playerID) {
        this.playerID = playerID;
    }

    protected void setTurn(int turn) {
        this.turn = turn;
    }
}
