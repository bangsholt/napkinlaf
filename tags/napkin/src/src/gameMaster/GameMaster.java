// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 7:33:45 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster;

import java.rmi.RemoteException;

public interface GameMaster {
    public Game[] gameList(int max) throws RemoteException;
}
