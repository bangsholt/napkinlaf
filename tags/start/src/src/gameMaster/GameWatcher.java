/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 19, 2002
 * Time: 2:37:15 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster;

import net.jini.core.event.UnknownEventException;

public interface GameWatcher {
    public void notify(GameEvent event) throws UnknownEventException;
}
