/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 19, 2002
 * Time: 2:37:55 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster;

import java.util.EventObject;

public class GameEvent extends EventObject {
    public GameEvent(Object source) {
	super(source);
    }
}
