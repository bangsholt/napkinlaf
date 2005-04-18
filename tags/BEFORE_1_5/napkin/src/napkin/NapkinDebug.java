// $Id$

package napkin;

import java.awt.*;
import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

public class NapkinDebug {
    private static final Map fieldsForType = new WeakHashMap();
    private static final Set skip = new HashSet();

    static int count = 0;

    public static void dumpObject(Object obj, String fileName) {
        PrintStream out = null;
        try {
            out = new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(fileName)));
            dumpObject(obj, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
    }

    static String descFor(Object obj) {
        if (obj instanceof Component)
            return descFor((Component) obj);
        else
            return obj.getClass().getName();
    }

    static String descFor(Component c) {
        if (c == null)
            return "[null]";
        //noinspection StringReplaceableByStringBuffer
        String desc;
        String idStr = "[" + System.identityHashCode(c) + "]";
        if ((desc = c.getName()) != null)
            return desc.trim() + idStr + "/" + c.getClass().getName();
        desc = c.getClass().getName();
        int dot = desc.lastIndexOf('.');
        if (dot > 0)
            desc = desc.substring(dot + 1);
        desc += idStr;

        if (c instanceof JLabel)
            desc += ": " + ((JLabel) c).getText();
        else if (c instanceof AbstractButton)
            desc += ": " + ((AbstractButton) c).getText();
        else if (c instanceof JTextComponent)
            desc += ": " + ((JTextComponent) c).getText();
        else if (c instanceof JPopupMenu)
            desc += ": " + ((JPopupMenu) c).getLabel();
        else if (c instanceof Label)
            desc += ": " + ((Label) c).getText();
        else if (c instanceof Button)
            desc += ": " + ((Button) c).getLabel();
        else if (c instanceof Checkbox)
            desc += ": " + ((Checkbox) c).getLabel();
        else if (c instanceof Dialog)
            desc += ": " + ((Dialog) c).getTitle();
        else if (c instanceof Frame)
            desc += ": " + ((Frame) c).getTitle();
        else if (c instanceof JInternalFrame)
            desc += ": " + ((JInternalFrame) c).getTitle();
        desc = desc.trim();

        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Border border = jc.getBorder();
            if (border instanceof TitledBorder)
                desc += ": " + ((TitledBorder) border).getTitle();
        }
        desc = desc.trim();

        return desc;
    }

    static void dumpTo(String file, JComponent c) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            Set dumped = new HashSet();
            dumpTo(out, c, c.getClass(), 0, dumped);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
    }

    private static void dumpTo(PrintWriter out, Object obj, Class cl, int level,
            Set dumped)
            throws IllegalAccessException {
        if (cl == null)
            return;
        dumpTo(out, obj, cl.getSuperclass(), level, dumped);
        Field[] fields = cl.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            Object val = field.get(obj);
            for (int l = 0; l < level; l++)
                out.print("    ");
            out.println(field.getName() + ": " + val);
            if (val != null && !dumped.contains(obj) &&
                    !field.getType().isPrimitive()) {
                dumpTo(out, val, val.getClass(), level + 1, dumped);
                dumped.add(obj);
            }
        }
    }

    public static void dumpObject(Object obj, PrintStream out) {
        Map known = new HashMap();
        dumpObject(obj, out, 0, known);
    }

    private static void
            dumpObject(Object obj, PrintStream out, int depth, Map known) {

        Object id = known.get(obj);
        if (id != null) {
            out.println("<known: " + id + ">");
            return;
        }
        id = new Integer(known.size());
        known.put(obj, id);

        out.println(descFor(obj) + " <" + id + ">");

        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i <= depth; i++)
                sb.append(i % 2 == 0 ? '.' : '|').append(' ');
            String indent = sb.toString();

            Field[] fields = getFields(obj);
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (skip.contains(field.getName()))
                    continue;
                Class type = field.getType();
                out.print(indent);
                out.print(field.getName() + " [" + field.getType().getName() +
                        "]: ");
                Object val = field.get(obj);
                dumpValue(type, out, val, depth, known);
            }

            if (obj.getClass().isArray()) {
                Class type = obj.getClass().getComponentType();
                int length = Array.getLength(obj);
                for (int i = 0; i < length; i++) {
                    Object val = Array.get(obj, i);
                    if (val == null)
                        continue;
                    out.print(indent);
                    out.print(i + ": ");
                    dumpValue(type, out, val, depth, known);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void dumpValue(Class type, PrintStream out, Object val,
            int depth, Map known) {
        if (type.isPrimitive())
            out.println(val);
        else if (val == null || type == String.class)
            out.println(val);
        else {
            if (type.isArray()) {
                Class aType = type.getComponentType();
                out.println(" " + aType.getName() + "[" +
                        Array.getLength(val) + "]");
            }
            dumpObject(val, out, depth + 1, known);
        }
    }

    private static Field[] getFields(Object obj) {
        Class type = obj.getClass();
        Field[] fields = (Field[]) fieldsForType.get(type);
        if (fields != null)
            return fields;

        Set fSet = new HashSet();
        int skip = Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT;
        while (type != Object.class) {
            Field[] declaredFields = type.getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                Field field = declaredFields[i];
                int mods = field.getModifiers();
                if (!field.getDeclaringClass().isAssignableFrom(obj.getClass()))
                    fSet.size();
                if ((mods & skip) == 0)
                    fSet.add(field);
            }
            type = type.getSuperclass();
        }
        fields = (Field[]) fSet.toArray(new Field[fSet.size()]);
        Arrays.sort(fields, new Comparator() {
            public int compare(Object o1, Object o2) {
                Field f1 = (Field) o1;
                Field f2 = (Field) o2;
                int d = f1.getName().compareTo(f2.getName());
                if (d != 0)
                    return d;
                Class c1 = f1.getDeclaringClass();
                Class c2 = f2.getDeclaringClass();
                return c1.getName().compareTo(c2.getName());
            }
        });
        AccessibleObject.setAccessible(fields, true);
        fieldsForType.put(obj.getClass(), fields);
        return fields;
    }

    public static String toString(Color c) {
        return "#" + Integer.toHexString(c.getRGB()) + "/" +
                Integer.toHexString(c.getAlpha());
    }
}