/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 19, 2002
 * Time: 3:07:25 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import net.jini.core.event.RemoteEvent;
import gameMaster.GameEvent;

import java.rmi.MarshalledObject;

class GameEventWrapper extends RemoteEvent {
    private GameEvent msg;

    public GameEventWrapper(Object source, long eventID, long seqNum,
		    MarshalledObject handback, GameEvent msg) {
	super(source, eventID, seqNum, handback);
	this.msg = msg;
    }

    GameEvent getMsg() {
	return msg;
    }
}
