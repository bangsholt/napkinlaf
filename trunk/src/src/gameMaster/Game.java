// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 7:35:35 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster;

import net.jini.core.lease.Lease;

import java.rmi.RemoteException;

public interface Game {
    public String getName();

    public GameType getGameType();

    public boolean isJoinable() throws RemoteException;

    public Lease addWatcher(GameWatcher watcher) throws RemoteException;

    public PlayerControl addPlayer(GameWatcher watcher)
            throws RemoteException, GameFullException;
}
