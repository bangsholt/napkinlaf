/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 19, 2002
 * Time: 3:13:15 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo.jini;

import gameMaster.impl.ServerGameMasterImpl;
import gameMaster.impl.ServerGameImpl;
import gameMaster.GameType;
import hexgo.jini.HexgoGameImpl;

import java.rmi.RemoteException;

public class HexgoMasterImpl extends ServerGameMasterImpl {
    protected ServerGameImpl createLocalGame() throws RemoteException {
	return new HexgoGameImpl();
    }

    protected GameType getGameType() {
	return new HexgoGame();
    }
}
