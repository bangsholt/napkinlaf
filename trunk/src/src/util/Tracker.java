package util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tracker {
    private static class TrackerHandler implements InvocationHandler {
        private final Object obj;
        private final String name;

        public TrackerHandler(Object obj, String name) {
            this.obj = obj;
            this.name = name;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            System.out.println(
                    name + ": " + method.getName() + "(" + prArgs(args) + ")");
            return method.invoke(obj, args);
        }

        private static String prArgs(Object[] args) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < args.length; i++) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(args[i]);
            }
            return sb.toString();
        }
    }

    public static Object get(Object obj) {
        return get(obj, allInterfaces(obj));
    }

    private static Class[] allInterfaces(Object obj) {
        Set ifaceSet = new HashSet();
        allInterfaces(obj.getClass(), ifaceSet);
        return (Class[]) ifaceSet.toArray(new Class[ifaceSet.size()]);
    }

    private static void allInterfaces(Class type, Set ifaceSet) {
        if (type == null)
            return;
        Class[] ifaces = type.getInterfaces();
        ifaceSet.addAll(Arrays.asList(ifaces));
        allInterfaces(type.getSuperclass());
        for (int i = 0; i < ifaces.length; i++)
            allInterfaces(ifaces[i]);
    }

    public static Object get(Object obj, Class iface) {
        return get(obj, new Class[]{iface});
    }

    public static Object get(Object obj, Class[] ifaces) {
        return get(obj, ifaces, obj.toString());
    }

    public static Object get(Object obj, String name) {
        return get(obj, name);
    }

    public static Object get(Object obj, Class iface, String name) {
        return get(obj, iface, name);
    }

    public static Object get(Object obj, Class[] ifaces, String name) {
        TrackerHandler handler = new TrackerHandler(obj, name);
        ClassLoader loader = obj.getClass().getClassLoader();
        return Proxy.newProxyInstance(loader, ifaces, handler);
    }
}