/*
 * Created by IntelliJ IDEA.
 * User: arnold
 * Date: Nov 8, 2002
 * Time: 5:58:20 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

import java.lang.reflect.Method;

public class MethodLookup {

    /**
     * The order for casting from one type to another.
     *
     * @see #castOrder
     */
    private static Class[] castOrder = {
	Boolean.TYPE,
	Byte.TYPE,
	Character.TYPE,
	Short.TYPE,
	Integer.TYPE,
	Long.TYPE,
	Float.TYPE,
	Double.TYPE,
    };

    /**
     * The order number for <CODE>char</CODE>.
     */
    private static final int CHAR_ORDER = castOrder(Character.TYPE);

    /**
     * Starts this application.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception Let all the exceptions that lurk in the code leak out.
     */
    public static void main(String[] args) throws Exception {
	Class clazz = Class.forName(args[0]);
	String methodName = args[1];
	Class[] types = typesFromStrings(args, 2);

	Method method = lookupMethod(clazz, methodName, types);

	System.out.println(method);
    }

    /**
     * Only static methods, so no objects every constructed.
     */
    private MethodLookup() {
    }

    /**
     * Returns the most specific method defined by the class, name, and argument
     * types.  This uses the same definition of "most specific" used in Java,
     * namely given a method <I>T<SUB>m</SUB></I>
     * declared in class <I>T</I> with parameter types <I>T<SUB>1</SUB></I>..<I>T<SUB>n</SUB></I>
     * and a method <I>U<SUB>m</SUB></I> with the same name and number of
     * parameters as <I>T<SUB>m</SUB></I>, with <I>U<SUB>m</SUB></I>
     * declared in class <I>U</I> with parameter types <I>U<SUB>1</SUB></I>..<I>U<SUB>n</SUB></I>,
     * <I>T<SUB>m</SUB></I> is more specific than <I>U<SUB>m</SUB></I> if and
     * only if
     * <UL>
     * <LI><I>T</I> can be converted to <I>U</I> using paramter conversion, and
     * <LI>For all <I>T<SUB>j</SUB></I>,
     * <LI><I>T<SUB>j</SUB></I> can be converted to <I>U<SUB>j</SUB></I> using paramter conversion.
     *
     * @param clazz The class to look for methods in.
     * @param methodName The name of the method we want to find.
     * @param argTypes The types of arguments used with the method.
     *
     * @return The most specific method matching the description.
     *
     * @throws NoSuchMethodException No most specific method could be found.
     */
    public static Method
	    lookupMethod(Class clazz, String methodName, Class[] argTypes)
	    throws NoSuchMethodException {

	Method[] methods = clazz.getMethods();
	Method bestMethod = null;	// current best match
	boolean duplicate = false;	// have multiple methods been as good?

	// look through the methods for matches
	for (int i = 0; i < methods.length; i++) {
	    Method method = methods[i];

	    // name match?
	    if (!method.getName().equals(methodName))
		continue;

	    // parameter count match?
	    Class[] paramTypes = method.getParameterTypes();
	    if (paramTypes.length != argTypes.length)
		continue;

	    log(method.toString());	// starting serious examination

	    // can we assign all arguments to their respective parameters?
	    if (!canAssign(paramTypes, argTypes))
		continue;

	    // if the first time we've found a match, it's the best (for now)
	    if (bestMethod == null) {
		log("    MOST SPECIFIC");
		bestMethod = method;
		continue;
	    }

	    // now see if T(i) = U(i) and T = U are legal, and vice versa
	    Class bestClazz = bestMethod.getDeclaringClass();
	    Class[] bestArgTypes = bestMethod.getParameterTypes();
	    Class currClazz = method.getDeclaringClass();
	    Class[] currArgTypes = method.getParameterTypes();
	    boolean c2b = canAssign(bestArgTypes, currArgTypes) &&
		    canAssign(bestClazz, currClazz);
	    boolean b2c = canAssign(currArgTypes, bestArgTypes) &&
		    canAssign(currClazz, bestClazz);

	    log("    c -> b: " + c2b);
	    log("    b -> c: " + b2c);
	    if (c2b == b2c) {
		// if they are both true or both false, both are equally good
		log("    duplicate");
		duplicate = true;
		continue;
	    } else if (b2c) {
		// if best -> current is legal, new winner
		log("    MOST SPECIFIC");
		duplicate = false;
		bestMethod = method;
	    } else {
		// if current -> best is legal, best is still better
		log("    skipping less specific");
	    }
	}

	// check to see if we've found a match
	if (bestMethod == null)
	    noSuchMethod("No match found");
	else if (duplicate)
	    noSuchMethod("No unique match");

	log("bestMethod = " + bestMethod);
	return bestMethod;
    }

    /**
     * Throws a <CODE>NoSuchMethodException</CODE> after logging its message.
     *
     * @param msg The message for the exception
     *
     * @throws NoSuchMethodException always, with the given message
     */
    private static void noSuchMethod(String msg) throws NoSuchMethodException {
	log(msg);
	throw new NoSuchMethodException(msg);
    }

    /**
     * Returns <CODE>true</CODE> if each element of <CODE>argTypes</CODE>
     * can be assigned to the corresponding element of <CODE>paramTypes</CODE>.
     *
     * @param paramTypes The parameter types to check assignment against.
     * @param argTypes The argument types to check for assignment.
     *
     * @return <CODE>true</CODE> if all are allowed.
     *
     * @see #canAssign(java.lang.Class, java.lang.Class)
     */
    private static boolean canAssign(Class[] paramTypes, Class[] argTypes) {
	for (int i = 0; i < paramTypes.length; i++) {
	    Class paramType = paramTypes[i];
	    Class argType = argTypes[i];
	    if (!canAssign(paramType, argType)) {
		log("    skip: cannot assign " + paramType.getName() + " = " +
			argType.getName());
		return false;
	    }
	}
	return true;
    }

    /**
     * Returns <CODE>true</CODE> if the type defined in <CODE>argType</CODE>
     * can be assigned to the type the type defined in <CODE>paramType</CODE>.
     * This is checked using paramter conversion (from <I>The Java Language
     * Specification</I>), so it also includes primitive type assignments.
     *
     * @param paramType The type to check assignment against.
     * @param argType The type to check assignment from.
     *
     * @return <CODE>true</CODE> if the assignment is allowed.
     */
    private static boolean canAssign(Class paramType, Class argType) {
	if (!paramType.isPrimitive())
	    return paramType.isAssignableFrom(argType);
	else if (!argType.isPrimitive())
	    return false;

	// booleans are only assignable to booleans
	if (paramType == Boolean.TYPE || argType == Boolean.TYPE)
	    return argType == paramType;

	int argOrder = castOrder(argType);
	int paramOrder = castOrder(paramType);

	// can't assign byte or short to char
	if (paramType == Character.TYPE && argOrder < CHAR_ORDER)
	    return false;
	return paramOrder >= argOrder;
    }

    /**
     * Returns the cast order index for the given primitive type.
     *
     * @param type The primitive type (<CODE>Integer.TYPE</CODE>, etc.)
     *
     * @return A number that represents the distance from <CODE>boolean</CODE>
     * 		of the type.  For example, <CODE>int</CODE> < <CODE>long</CODE>
     * 		by a value of 1.  This doesn't mean the cast is allowable,
     * 		although it always if the two types have the same cast order.
     */
    private static int castOrder(Class type) {
	for (int i = 0; i < castOrder.length; i++) {
	    Class aClass = castOrder[i];
	    if (type == aClass)
		return i;
	}
	throw new IllegalArgumentException(type.getName());
    }

    /**
     * Creates an array of types from an array of string, starting in the
     * string array at the given index.  Primitive names are accepted, as
     * are class names in the <CODE>java.lang</CODE> package.
     *
     * @param args The array of strings with class names.
     * @param start The position of the first class name in the array.
     *
     * @return An array of size <CODE>(args.length - start)</CODE>, where
     * 		<CODE>return[0]</CODE> is the class from
     * 		<CODE>args[start]</CODE> and so on.
     *
     * @throws ClassNotFoundException A class could not be found.  The method
     * 		stops executing after the first unfound class.
     */
    private static Class[] typesFromStrings(String[] args, int start)
	    throws ClassNotFoundException {
	Class[] classes = new Class[args.length - start];
	for (int i = start; i < args.length; i++) {
	    classes[i - start] = lookupClass(args[i]);
	}
	return classes;
    }

    /**
     * Returns the class for the given name.  This takes the names of primitive
     * types into account; thus <CODE>"int"</CODE> returns
     * <CODE>Integer.TYPE</CODE>.  If the name isn't found, and the type is not
     * a primitive name, it also tries <CODE>"java.lang.<I>name</I></CODE>
     * before giving up.
     *
     * @param name The type name.
     *
     * @return The class for the type name.
     *
     * @throws ClassNotFoundException No class object can be found from the name.
     */
    private static Class lookupClass(String name)
	    throws ClassNotFoundException {
	if (name.equals("boolean"))
	    return Boolean.TYPE;
	else if (name.equals("byte"))
	    return Byte.TYPE;
	else if (name.equals("char"))
	    return Character.TYPE;
	else if (name.equals("short"))
	    return Short.TYPE;
	else if (name.equals("int"))
	    return Integer.TYPE;
	else if (name.equals("long"))
	    return Long.TYPE;
	else if (name.equals("float"))
	    return Float.TYPE;
	else if (name.equals("double")) return Double.TYPE;
	try {
	    return Class.forName(name);
	} catch (ClassNotFoundException e) {
	    return Class.forName("java.lang." + name);
	}
    }

    /**
     * Logs the given message.
     *
     * @param msg The message to log.
     */
    private static void log(String msg) {
	System.out.println(msg);
    }
}
