// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 7:49:13 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import gameMaster.Game;
import gameMaster.GameMaster;
import gameMaster.GameType;
import net.jini.core.entry.Entry;
import net.jini.discovery.DiscoveryManagement;
import net.jini.discovery.LookupDiscovery;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;

import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;

public abstract class ServerGameMasterImpl implements ServerGameMaster {
    private ServerGameImpl localGame;
    private ProxyGame proxyGame;

    private static RemoteStub remote;
    private static GameMaster me;

    public ServerGameMasterImpl() {
    }

    public synchronized Game[] gameList(int max) throws RemoteException {
        if (localGame == null) {
            localGame = createLocalGame();
            UnicastRemoteObject.exportObject(localGame);
            proxyGame = new ProxyGame(localGame, localGame.getName(),
                    getGameType());
        }
        return new Game[]{proxyGame};
    }

    protected abstract ServerGameImpl createLocalGame() throws RemoteException;

    protected abstract GameType getGameType();

    /**
     * Starts this application.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception Let all the exceptions that lurk in the code leak out.
     */
    public static void main(String[] args) throws Exception {
        Class masterClass = Class.forName(args[0]);
        ServerGameMasterImpl master =
                (ServerGameMasterImpl) masterClass.newInstance();
        remote = UnicastRemoteObject.exportObject(master);
        me = (GameMaster) new MarshalledObject(remote).get();
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());

        String[] groups = new String[args.length - 1];
        System.arraycopy(args, 1, groups, 0, groups.length);
        DiscoveryManagement dm = new LookupDiscovery(groups);

        Entry[] attrSets = new Entry[]{master.getGameType()};
        new JoinManager(me, attrSets, (ServiceIDListener) null, dm, null);
        System.out.println("Join started");
    }
}
