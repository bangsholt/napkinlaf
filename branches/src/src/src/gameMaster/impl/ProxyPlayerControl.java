// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 8:20:21 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import gameMaster.Move;
import gameMaster.PlayerControl;
import net.jini.core.lease.Lease;

import java.io.Serializable;
import java.rmi.RemoteException;

public class ProxyPlayerControl implements PlayerControl, Serializable {
    private Lease lease;
    private ServerPlayerControl spc;
    private long playerID;
    private int turn;

    ProxyPlayerControl(long id, ServerPlayerControl spc) {
        this.spc = spc;
        playerID = id;
        turn = 1;
    }

    public Lease getLease() {
        return lease;
    }

    void setLease(Lease lease) {
        this.lease = lease;
    }

    public void makeMove(Move move) throws RemoteException {
        if (!(move instanceof ServerMove))
            throw new IllegalArgumentException("Unknown Move Type");
        ServerMove sm = (ServerMove) move;
        sm.setPlayerID(playerID);
        sm.setTurn(turn);
        turn = spc.makeMove(move);
    }

    long getPlayerID() {
        return playerID;
    }
}
