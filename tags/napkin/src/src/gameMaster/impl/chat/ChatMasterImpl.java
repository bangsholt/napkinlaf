/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 20, 2002
 * Time: 10:12:49 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl.chat;

import gameMaster.impl.ServerGameMasterImpl;
import gameMaster.impl.ServerGameImpl;
import gameMaster.GameType;

import java.rmi.RemoteException;

public class ChatMasterImpl extends ServerGameMasterImpl {
    protected ServerGameImpl createLocalGame() throws RemoteException {
	return new ChatGameImpl();
    }

    public GameType getGameType() {
	return new ChatGame();
    }
}
