/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Sep 19, 2002
 * Time: 3:56:06 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import java.lang.reflect.Method;

public class Stats {
    /**
     * Starts this application.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception Let all the exceptions that lurk in the code leak out.
     */
    public static void main(String[] args) throws Exception {
	for (int i = 0; i < args.length; i++) {
	    String cname = args[i];
	    Class c = Class.forName(cname);
	    Method[] methods = c.getDeclaredMethods();
	}
    }
}
