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

import gameMaster.Game;
import gameMaster.GameFullException;
import gameMaster.GameType;
import gameMaster.GameWatcher;
import gameMaster.PlayerControl;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.UnicastRemoteObject;
import java.util.WeakHashMap;

public class ProxyGame implements Game, Serializable {
    private ServerGame sgd;
    private String name;
    private GameType type;
    private transient WeakHashMap listeners;
    private transient ObjID proxyID;

    private static class LocalListener implements RemoteEventListener {
        private WeakReference watcherRef;

        LocalListener(GameWatcher watcher) {
            watcherRef = new WeakReference(watcher);
        }

        public void notify(RemoteEvent event)
                throws UnknownEventException, RemoteException {

            GameWatcher watcher = (GameWatcher) watcherRef.get();
            if (watcher == null)
                return;	// been collected, but not before we got here

            if (!(event instanceof GameEventWrapper))
                throw new UnknownEventException("only accepts SMQEvents");
            watcher.notify(((GameEventWrapper) event).getMsg());
        }
    }

    public ProxyGame(ServerGame sgd, String name, GameType type) {
        this.sgd = sgd;
        this.name = name;
        this.type = type;
        createTransients();
    }

    /**
     * Read in this class's part of the object from the stream.
     *
     * @param in The stream containing the data.
     *
     * @throws IOException            Any I/O problem.
     * @throws ClassNotFoundException A relevant class cannot be found.
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        createTransients();
    }

    private void createTransients() {
        listeners = new WeakHashMap(3);
        proxyID = new ObjID();
    }

    public String getName() {
        return name;
    }

    public GameType getGameType() {
        return type;
    }

    public boolean isJoinable() throws RemoteException {
        return sgd.isJoinable();
    }

    public Lease addWatcher(GameWatcher watcher)
            throws RemoteException {
        return sgd.addWatcher(proxyID, listenerFor(watcher));
    }

    public PlayerControl addPlayer(GameWatcher watcher)
            throws RemoteException, GameFullException {
        return sgd.addPlayer(proxyID, listenerFor(watcher));
    }

    private synchronized RemoteEventListener listenerFor(GameWatcher watcher)
            throws RemoteException {
        RemoteEventListener l = (RemoteEventListener) listeners.get(watcher);
        if (l == null) {
            l = new LocalListener(watcher);
            UnicastRemoteObject.exportObject(l);
            listeners.put(watcher, l);
        }
        return l;
    }
}
