// $Header$

/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 18, 2002
 * Time: 10:13:37 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package gameMaster.impl;

import com.sun.jini.lease.landlord.LeasedResource;
import net.jini.core.event.RemoteEventListener;

import java.rmi.server.ObjID;

class WatcherDesc implements LeasedResource {
    ObjID proxyID;
    RemoteEventListener listener;

    private long expires;

    public WatcherDesc(ObjID proxyID) {
        this.proxyID = proxyID;
    }

    public void setExpiration(long newExpiration) {
        expires = newExpiration;
    }

    public long getExpiration() {
        return expires;
    }

    public Object getCookie() {
        return proxyID;
    }
}
