// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 7:40:19 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster;

import net.jini.core.lease.Lease;

import java.rmi.RemoteException;

public interface PlayerControl {
    public Lease getLease();

    public void makeMove(Move move) throws RemoteException, RemoteException;
}
