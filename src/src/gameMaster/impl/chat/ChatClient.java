/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 20, 2002
 * Time: 9:40:20 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl.chat;

import gameMaster.Game;
import gameMaster.GameEvent;
import gameMaster.GameMaster;
import gameMaster.GameWatcher;
import gameMaster.PlayerControl;
import net.jini.core.entry.Entry;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryManagement;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.ServiceDiscoveryManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.rmi.RMISecurityManager;

public class ChatClient implements GameWatcher {
    private Game game;
    private boolean prompted;

    public ChatClient(Game game) {
	this.game = game;
    }

    /**
     * Starts this application.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception Let all the exceptions that lurk in the code leak out.
     */
    public static void main(String[] args) throws Exception {
	if (System.getSecurityManager() == null)
	    System.setSecurityManager(new RMISecurityManager());

	DiscoveryManagement ldm = new LookupDiscoveryManager(args, null, null);
	ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(ldm, null);

	Class[] serviceTypes = new Class[]{GameMaster.class};
	Entry[] attrSetTemplates = new Entry[]{new ChatGame()};
	attrSetTemplates = new Entry[]{};
	ServiceTemplate tmpl =
		new ServiceTemplate(null, serviceTypes, attrSetTemplates);

	System.out.println("tmpl = " + tmpl.serviceTypes[0]);
	ServiceItem si = sdm.lookup(tmpl, null, 5000);
	if (si == null)
	    System.out.println("none found");
	else {
	    GameMaster master = (GameMaster) si.service;
	    System.out.println("master = " + master);
	    new ChatClient(master.gameList(1)[0]).chat();
	}
    }

    private void chat() {
	System.out.println(game.getName());

	PlayerControl control = null;
	try {
	    control = game.addPlayer(this);
	    control.makeMove(new ChatEnter("Ken"));
	} catch (Exception e) {
	    System.out.println("Cannot join: " + e);
	    e.printStackTrace(System.out);
	    System.exit(-1);
	}

	BufferedReader in =
		new BufferedReader(new InputStreamReader(System.in));

	try {
	    String line;
	    do {
		prompt();
		line = in.readLine();
		setUnprompted();
		if (line != null)
		    control.makeMove(new ChatUtter("Ken", line));
	    } while (line != null);
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(-2);
	}

	System.exit(0);
    }

    private synchronized void setUnprompted() {
	prompted = false;
    }

    private synchronized void prompt() {
	if (!prompted)
	    System.out.print("> ");
	prompted = true;
    }

    public void notify(GameEvent event) throws UnknownEventException {
	if (event instanceof ChatEnter) {
	    ChatEnter enter = (ChatEnter) event;
	    printMsg(enter.getName() + " enters");
	} else if (event instanceof ChatUtter) {
	    ChatUtter utter = (ChatUtter) event;
	    printMsg(utter.getSpeaker() + ": " + utter.getUtterance());
	}
    }

    private synchronized void printMsg(String msg) {
	if (prompted) {
	    System.out.println();
	    setUnprompted();
	}
	try {
	    BufferedReader mbuf = new BufferedReader(new StringReader(msg));
	    String line;
	    while ((line = mbuf.readLine()) != null) {
		System.out.print("  * ");
		System.out.println(line);
	    }
	} catch (IOException e) {
	    System.err.println("IO exception reading string??");
	    e.printStackTrace();
	    System.exit(-2);
	}
	prompt();
    }
}
