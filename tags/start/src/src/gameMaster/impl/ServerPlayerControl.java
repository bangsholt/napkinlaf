// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 8:35:06 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import gameMaster.Move;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerPlayerControl extends Remote {
    public int makeMove(Move move) throws RemoteException;
}
