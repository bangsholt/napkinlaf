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

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;

import java.rmi.RemoteException;

public interface GameDesc {
    public String getName();

    public GameType getGameType();

    public boolean isJoinable() throws RemoteException;

    public Lease addWatcher(RemoteEventListener watcher) throws RemoteException, RemoteException;

    public PlayerControl addPlayer(RemoteEventListener watcher)
            throws RemoteException, RemoteException, GameFullException;
}
