// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 9:26:23 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import gameMaster.Move;

public class ServerMove extends Move {

    public ServerMove(Object source) {
        super(source);
    }

    /*
     * The compiler makes me redeclare these to get package access to them, but
     * this seems wrong.  On the other hand, it also seems necessary.  Sigh.
     */
    protected void setPlayerID(long playerID) {
        super.setPlayerID(playerID);
    }

    protected void setTurn(int turn) {
        super.setTurn(turn);
    }
}
