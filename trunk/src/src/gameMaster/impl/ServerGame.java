// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 7:53:34 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import gameMaster.GameFullException;
import gameMaster.PlayerControl;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;

public interface ServerGame extends Remote {
    public boolean isJoinable() throws RemoteException;

    public Lease addWatcher(ObjID proxyID, RemoteEventListener watcher)
            throws RemoteException;

    public PlayerControl addPlayer(ObjID proxyID, RemoteEventListener watcher)
            throws RemoteException, GameFullException;
}
