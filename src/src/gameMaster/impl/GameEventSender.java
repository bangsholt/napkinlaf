/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 19, 2002
 * Time: 1:23:55 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import com.sun.jini.constants.ThrowableConstants;
import com.sun.jini.thread.RetryTask;
import com.sun.jini.thread.TaskManager;
import gameMaster.GameEvent;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

import java.rmi.MarshalledObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class GameEventSender {
    private TaskManager tasks;
    private Long eventSource;
    private int defaultRetryCount;
    private long myIDBits;
    private long nextSeqNum = 1000;

    private class MessageTask extends RetryTask {
	private RemoteEvent event;
	private RemoteEventListener listener;
	private int retryCount;
	private long eventID;
	private long seqNum;

	MessageTask(GameEvent msg, RemoteEventListener listener) {
	    super(tasks);
	    this.listener = listener;
	    retryCount = defaultRetryCount;
	    eventID = eventID(msg);
	    seqNum = nextSeqNum(msg);
	    event = new GameEventWrapper(eventSource, eventID, seqNum, null, msg);
	}

	public boolean runAfter(List tasks, int size) {
	    for (int i = 0; i < tasks.size(); i++) {
		TaskManager.Task task = (TaskManager.Task) tasks.get(i);
		if (task instanceof MessageTask) {
		    MessageTask msgTask = (MessageTask) task;
		    if (msgTask.listener.equals(listener))
			return true;
		}
	    }
	    return false;
	}

	public boolean tryOnce() {
	    try {
		listener.notify(event);
		return true;
	    } catch (UnknownEventException e) {
		//!! Should unregister the listener
		cancel();
		return false;
	    } catch (Exception e) {
		switch (ThrowableConstants.retryable(e)) {
		case ThrowableConstants.BAD_OBJECT:
		    //!! Should unregister the listener
		case ThrowableConstants.BAD_INVOCATION:
		    cancel();

		default:
		    if (attempt() >= retryCount)
			cancel();
		}
		return false;
	    }
	}
    }

    public GameEventSender() {
	this(5);
    }

    public GameEventSender(int defaultRetryCount) {
	this.defaultRetryCount = defaultRetryCount;
	eventSource = new Long(myIDBits);
	tasks = new TaskManager();
	myIDBits = System.identityHashCode(this) << 32;
    }

    public void send(GameEvent msg, Collection targets) {
	for (Iterator it = targets.iterator(); it.hasNext();) {
	    WatcherDesc desc = (WatcherDesc) it.next();
	    tasks.add(new MessageTask(msg, desc.listener));
	}
    }

    protected synchronized long nextSeqNum(GameEvent msg) {
	return nextSeqNum++;
    }

    protected long eventID(GameEvent msg) {
	return myIDBits | System.identityHashCode(msg.getClass());
    }
}
