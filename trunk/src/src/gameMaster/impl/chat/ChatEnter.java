/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 21, 2002
 * Time: 10:42:08 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl.chat;

import gameMaster.impl.ServerMove;

public class ChatEnter extends ServerMove {
    private String name;

    public ChatEnter(String name) {
	super(name);
	this.name = name;
    }

    public String getName() {
	return name;
    }

    // inherit doc comment
    public String toString() {
	return "ChatEnter: " + name;
    }
}
