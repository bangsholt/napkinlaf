// $Id$

package napkin;

import javax.swing.plaf.basic.*;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Fold {

    /**
     * Run this class as a program
     *
     * @param args The command line arguments.
     *
     * @throws Exception Exception we don't recover from.
     */
    public static void main(String[] args) throws Exception {
        String clsName = BasicLookAndFeel.class.getName();
        String clsDirPath = clsName.replace('.', File.separatorChar) +
                ".class";
        String clsJarPath = clsName.replace('.', '/') + ".class";
        String pkgJarPath = clsJarPath.substring(0,
                clsJarPath.lastIndexOf('/'));

        String classpath = System.getProperty("java.class.path");
        StringTokenizer toks =
                new StringTokenizer(classpath, File.pathSeparator);

        boolean isJar = false;
        Set classes = null;
        File elem;
        while (toks.hasMoreTokens()) {
            elem = new File(toks.nextToken());
            if (elem.isDirectory()) {
                File classFile = new File(elem, clsDirPath);
                if (classFile.exists()) {
                    classes = readClasses(classFile.getParentFile());
                    break;
                }
            } else if (elem.getName().endsWith(".jar")) {
                JarFile jar = new JarFile(elem);
                classes = new TreeSet();
                Enumeration entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = (JarEntry) entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(pkgJarPath)) {
                        classes.add(name);
                        if (name.equals(clsJarPath))
                            isJar = true;
                    }
                }
                if (isJar)
                    break;
            }
        }

        if (classes == null) {
            System.err.println("Could not find " + clsName);
            System.exit(0);
        }

        genClasses(isJar, classes);
    }

    private static void genClasses(boolean jar, Set classes) throws Exception {
        final char sep = (jar ? '/' : File.separatorChar);
        for (Iterator it = classes.iterator(); it.hasNext();) {
            String name = (String) it.next();
            if (!name.endsWith("UI.class"))
                continue;
            String className = name.substring(0, name.length() - 6);
            className = className.replace(sep, '.');
            Class uiClass = Class.forName(className);
            genClass(uiClass);
        }
    }

    private static void genClass(Class uiClass) {
        System.out.println(uiClass.getName());
        showMethods(uiClass);
    }

    private static void showMethods(Class uiClass) {
        String name = uiClass.getName();
        if (!name.endsWith("UI"))
            return;
        Method[] methods = uiClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (!Modifier.isStatic(method.getModifiers()))
                System.out.println("  " + method + " // " + name);
        }
        showMethods(uiClass.getSuperclass());
    }

    private static Set readClasses(File parent) {
        String[] classFiles = parent.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });
        return new TreeSet(Arrays.asList(classFiles));
    }
}

