/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Oct 31, 2002
 * Time: 12:34:28 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import net.jini.core.lease.Lease;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.LeaseMap;

import java.rmi.RemoteException;

public class TestLRM {
    private static long start;

    /**
     * Starts this application.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception Let all the exceptions that lurk in the code leak out.
     */
    public static void main(String[] args) throws Exception {
	LeaseRenewalManager lrm = new LeaseRenewalManager();
	long time = Long.parseLong(args[0]) * 1000;
	start = System.currentTimeMillis();
	log("start");
	lrm.renewUntil(new TestLease(time), Lease.FOREVER, new LeaseListener() {
	    public void notify(LeaseRenewalEvent e) {
		log("notified");
	    }
	});
	Thread.sleep(time + 5000);
	log("woke up");
	System.exit(-1);
    }

    private static void log(String msg) {
	System.out.println(System.currentTimeMillis() - start + ": " + msg);
    }

    static class TestLease implements Lease {
	private long expires;
	private int format;

	TestLease(long time) {
	    log("TestLease(" + time + ")");
	    expires = start + time;
	}

	public long getExpiration() {
	    return expires;
	}

	public void cancel() throws UnknownLeaseException, RemoteException {
	    log("cancel");
	    expires = 0;
	}

	public void renew(long duration)
		throws LeaseDeniedException, UnknownLeaseException, RemoteException {
	    log("renew");
	    throw new RemoteException("testing");
	}

	public void setSerialFormat(int format) {
	    this.format = format;
	}

	public int getSerialFormat() {
	    return format;
	}

	public LeaseMap createLeaseMap(long duration) {
	    log("createLeaseMap");
	    return null;
	}

	public boolean canBatch(Lease lease) {
	    log("canBatch");
	    return false;
	}
    }
}
