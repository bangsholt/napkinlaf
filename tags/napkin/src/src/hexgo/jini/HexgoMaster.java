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
import hexgo.jini.HexgoGame;

public class HexgoMaster extends ServerGameMasterImpl {
    protected ServerGameImpl createLocalGame() {
	return null;
    }

    protected GameType getGameType() {
        return new HexgoGame();
    }
}
