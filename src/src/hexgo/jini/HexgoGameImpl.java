/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 19, 2002
 * Time: 3:15:03 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo.jini;

import gameMaster.impl.ServerGameImpl;
import gameMaster.impl.ServerPlayerControlImpl;
import gameMaster.impl.ServerPlayer;
import gameMaster.GameType;

import java.rmi.RemoteException;

public class HexgoGameImpl extends ServerGameImpl {
    private HexgoGameType gameType;

    public HexgoGameImpl() throws RemoteException {
	gameType = new HexgoGameType();
    }

    public String getName() {
	return "Hexgo";
    }

    public GameType getGameType() {
	return gameType;
    }

    protected ServerPlayerControlImpl getPlayerControl(ServerPlayer player) {
	return new HexgoPlayerControl(this);
    }

    public boolean isJoinable() throws RemoteException {
	return false;
    }
}
