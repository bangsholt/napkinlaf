// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 10:07:08 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

public class ServerPlayer {
    private long playerID;

    private static int nextPlayerID = 1;

    public ServerPlayer() {
        playerID = genPlayerID();
    }

    private synchronized static long genPlayerID() {
        return nextPlayerID++;
    }

    public long getPlayerID() {
        return playerID;
    }
}
