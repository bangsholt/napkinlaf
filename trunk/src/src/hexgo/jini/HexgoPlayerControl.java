/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 19, 2002
 * Time: 3:17:41 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package hexgo.jini;

import gameMaster.impl.ServerPlayerControlImpl;
import gameMaster.Move;

import java.rmi.RemoteException;

public class HexgoPlayerControl extends ServerPlayerControlImpl {
    private HexgoGameImpl game;

    public HexgoPlayerControl(HexgoGameImpl game) {
	this.game = game;
    }

    public int makeMove(Move move) throws RemoteException {
	return 0;
    }
}
