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

import java.rmi.server.ObjID;

class PlayerDesc extends WatcherDesc {
    ServerPlayer player;
    ServerPlayerControlImpl control;
    ProxyPlayerControl proxy;

    public PlayerDesc(ObjID proxyID, ServerPlayer player,
                      ServerPlayerControlImpl control,
                      ProxyPlayerControl proxy) {
        super(proxyID);
        this.player = player;
        this.control = control;
        this.proxy = proxy;
    }
}
