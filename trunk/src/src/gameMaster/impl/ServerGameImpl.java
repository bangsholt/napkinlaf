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

import com.sun.jini.lease.landlord.Landlord;
import com.sun.jini.lease.landlord.LandlordUtil;
import com.sun.jini.lease.landlord.LeaseDurationPolicy;
import com.sun.jini.lease.landlord.LeasePolicy;
import com.sun.jini.lease.landlord.LocalLandlord;
import gameMaster.GameFullException;
import gameMaster.PlayerControl;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;

import java.rmi.RemoteException;
import java.rmi.server.ObjID;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerGameImpl implements ServerGame {
    private Map watchers;
    private ArrayList players;
    private GameLandlord landlord;
    private LeasePolicy leasePolicy;

    private class GameLandlord implements Landlord, LocalLandlord {
        GameLandlord() throws RemoteException {
            UnicastRemoteObject.exportObject(this);
        }

        public long renew(Object cookie, long extension)
                throws LeaseDeniedException, UnknownLeaseException {
            WatcherDesc desc = findWatcherDesc((ObjID) cookie);
            // We check to see if the lease has expired twice so we avoid
            // unnecessary synchronization
            if (desc == null || !leasePolicy.ensureCurrent(desc)) {
                // Lease has expired, don't renew
                throw new UnknownLeaseException("Lease has already expired");
            }
            return leasePolicy.renew(desc, extension);
        }

        public synchronized
        void cancel(Object cookie) throws UnknownLeaseException {
            if (removePlayerDesc((ObjID) cookie) == null)
                throw new UnknownLeaseException();
        }

        public Landlord.RenewResults renewAll(Object[] cookies, long[] extensions) {
            return LandlordUtil.renewAll(this, cookies, extensions);
        }

        public Map cancelAll(Object[] cookies) {
            return LandlordUtil.cancelAll(this, cookies);
        }
    }

    public ServerGameImpl() throws RemoteException {
        watchers = new HashMap();
        players = new ArrayList();
        landlord = new GameLandlord();
        leasePolicy = new LeaseDurationPolicy(60 * 1000, 60 * 1000, landlord,
                null, null);
    }

    public abstract String getName();

    public synchronized Lease
            addWatcher(ObjID proxyID, RemoteEventListener watcher) {
        WatcherDesc desc = new WatcherDesc(proxyID);
        return addWatcher(desc, watcher);
    }

    private synchronized Lease
            addWatcher(WatcherDesc desc, RemoteEventListener watcher) {
        try {
            desc.listener = watcher;
            watchers.put(desc.proxyID, desc);
            return leasePolicy.leaseFor(desc, Lease.ANY);
        } catch (LeaseDeniedException e) {
            throw new IllegalStateException("denied our own lease?");
        }
    }

    public synchronized PlayerControl
            addPlayer(ObjID proxyID, RemoteEventListener watcher)
            throws RemoteException, GameFullException {
        WatcherDesc watcherDesc = findWatcherDesc(proxyID);
        if (watcherDesc instanceof PlayerDesc)
            return ((PlayerDesc) watcherDesc).proxy;

        if (!isJoinable())
            throw new GameFullException();

        ServerPlayer player = new ServerPlayer();
        ServerPlayerControlImpl control = getPlayerControl(player);
        UnicastRemoteObject.exportObject(control);
        ProxyPlayerControl proxy =
                new ProxyPlayerControl(player.getPlayerID(), control);
        PlayerDesc playerDesc = new PlayerDesc(proxyID, player, control, proxy);
        Lease lease = addWatcher(playerDesc, watcher);

        proxy.setLease(lease);
        addPlayer(proxyID);

        return proxy;
    }

    private void addPlayer(ObjID proxyID) {
        players.add(proxyID);
    }

    private WatcherDesc findWatcherDesc(ObjID proxyID) {
        return (WatcherDesc) watchers.get(proxyID);
    }

    private WatcherDesc removePlayerDesc(ObjID proxyID) {
        WatcherDesc desc = findWatcherDesc(proxyID);
        players.remove(proxyID);
        return desc;
    }

    protected abstract
    ServerPlayerControlImpl getPlayerControl(ServerPlayer player);

    public Collection getPlayers() {
        ArrayList pds = new ArrayList(players.size());
        for (int i = 0; i < players.size(); i++) {
            ObjID objID = (ObjID) players.get(i);
            pds.add(watchers.get(objID));
        }
        return pds;
    }
}
