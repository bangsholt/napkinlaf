/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 20, 2002
 * Time: 10:14:59 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl.chat;

import gameMaster.Move;
import gameMaster.impl.GameEventSender;
import gameMaster.impl.ServerGameImpl;
import gameMaster.impl.ServerPlayer;
import gameMaster.impl.ServerPlayerControl;
import gameMaster.impl.ServerPlayerControlImpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatGameImpl extends ServerGameImpl {
    private GameEventSender sender = new GameEventSender();
    private int nextTurn = 1;

    public ChatGameImpl() throws RemoteException {
    }

    public String getName() {
	return "Chat Game";
    }

    protected ServerPlayerControlImpl getPlayerControl(ServerPlayer player) {
	return new ChatPlayerControlImpl(this);
    }

    public boolean isJoinable() throws RemoteException {
	return true;
    }

    public void sendAll(Move move) {
	sender.send(move, getPlayers());
    }

    public synchronized int nextTurn() {
	return nextTurn++;
    }
}
