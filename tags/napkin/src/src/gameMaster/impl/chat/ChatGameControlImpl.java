/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 20, 2002
 * Time: 10:16:55 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl.chat;

import gameMaster.impl.ServerPlayerControlImpl;
import gameMaster.Move;

import java.rmi.RemoteException;

public class ChatGameControlImpl extends ServerPlayerControlImpl {
    private ChatGameImpl game;

    public ChatGameControlImpl(ChatGameImpl game) {
	this.game = game;
    }

    public synchronized int makeMove(Move move) throws RemoteException {
	game.sendAll(move);
	return game.nextTurn();
    }
}
