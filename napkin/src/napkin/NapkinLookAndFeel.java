// $Id$

package napkin;

import napkin.ComponentWalker.Visitor;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import javax.swing.*;
import javax.swing.UIDefaults.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinLookAndFeel extends BasicLookAndFeel {

    private LookAndFeel formal;
    private final Map flags = new WeakHashMap();

    private final Visitor clearKidsVisitor = new Visitor() {
        public boolean visit(Component c, int depth) {
            FormalityFlags ff = flags(c);
            System.out.println(
                    "clearKidsVisitor " + NapkinUtil.descFor(c) + ": " + ff);

            if (depth == 0) {
                System.out.println("    depth == 0, return true");
                return true;
            }

            ff.known = !ff.inherited;
            System.out.println("    return " + ff.inherited);
            return ff.inherited;
        }
    };

    private final Visitor updateUIVisitor = new Visitor() {
        public boolean visit(Component c, int depth) {
            System.out.println("updateUIVisitor " + NapkinUtil.descFor(c));
            FormalityFlags ff = flags(c, false);
            if (depth > 0 && !ff.inherited)
                return false;
            if (c instanceof JComponent) {
                System.out.println("    updateUI");
                ((JComponent) c).updateUI();
            }
            return true;
        }
    };

    private final Visitor addListenerVisitor = new Visitor() {
        public boolean visit(Component c, int depth) {
            System.out.println("addListenerVisitor " + NapkinUtil.descFor(c));
            if (!(c instanceof Container) || flags.containsKey(c))
                return false;

            FormalityFlags ff = flags(c, false);
            ((Container) c).addContainerListener(ff);
            System.out.println("adding listener for " + NapkinUtil.descFor(c) +
                    ", " +
                    System.identityHashCode(c));
            return true;
        }
    };

    private static final boolean JUST_NAPKIN = true;

    private static final String BASE_FONT = "aescr5b.ttf";
    private static final String FIXED_FONT = "Mcgf____.ttf";

    class DumpVisitor implements Visitor {
        private final PrintStream out;

        DumpVisitor(PrintStream out) {
            this.out = out;
        }

        public boolean visit(Component c, int depth) {
            FormalityFlags ff = flags(c);
            for (int i = 0; i < depth; i++) {
                out.print(i % 2 == 0 ? '|' : '.');
                out.print(' ');
            }
            String desc = NapkinUtil.descFor(c);
            out.print(desc);
            out.print(": ");
            out.print(ff);
            out.print(", " + c.isOpaque());
            out.println();
            return true;
        }
    };

    private class FormalityFlags implements ContainerListener {
        boolean known;
        boolean formal;
        boolean inherited = true;

        public void componentAdded(ContainerEvent e) {
            Component child = e.getChild();
            System.out.println("Added: " + NapkinUtil.descFor(child) + " to " +
                    e.getComponent());
            clear(child);
        }

        public void componentRemoved(ContainerEvent e) {
            Component child = e.getChild();
            System.out.println("Removed: " + NapkinUtil.descFor(child) +
                    " to " +
                    e.getComponent());
            clear(child);
        }

        public String toString() {
            if (!known)
                return "???";
            String desc = (formal ? "formal" : "napkin");
            if (!inherited)
                desc = desc.toUpperCase();
            return desc;
        }

        private void setFrom(FormalityFlags parentFlags) {
            if (parentFlags != null && parentFlags.known && inherited) {
                known = true;
                formal = parentFlags.formal;
            }
        }
    }

    private NapkinLookAndFeel(LookAndFeel formal) {
        setFormal(formal);
    }

    public NapkinLookAndFeel() {
        this(UIManager.getLookAndFeel());
    }

    public String getDescription() {
        String desc = "The Napkin Look and Feel";
        if (formal != null)
            desc += " [backed by " + formal.getDescription() + "]";
        return desc;
    }

    public String getID() {
        String desc = "Napkin";
        if (formal != null)
            desc += "[" + formal.getID() + "]";
        return desc;
    }

    public String getName() {
        return getID();
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public LookAndFeel getFormal() {
        return formal;
    }

    private void setFormal(LookAndFeel formal) {
        if (formal == null)
            throw new NullPointerException("formal");
        if (!JUST_NAPKIN)
            this.formal = formal;
    }

    public void provideErrorFeedback(Component component) {
        // nothing special needed here -- not a formal/informal thing
        if (formal != null)
            formal.provideErrorFeedback(component);
        else
            super.provideErrorFeedback(component);
    }

    public void initialize() {
        if (formal != null)
            formal.initialize();
    }

    public void uninitialize() {
        if (formal != null)
            formal.uninitialize();
    }

    public boolean isFormal(Component c) {
        if (JUST_NAPKIN)
            return false;

        FormalityFlags ff = flags(c);
        System.out.println("isFormal(" + NapkinUtil.descFor(c) + "): " + ff);
        if (ff.known)
            return ff.formal;

        FormalityFlags pff = inhieritedFormal(c.getParent());
        ff.setFrom(pff);
        return ff.formal;
    }

    private FormalityFlags inhieritedFormal(Container container) {
        System.out.println(
                "inheritedFormal(" + NapkinUtil.descFor(container) + ")");
        if (container == null)
            return null;
        FormalityFlags ff = flags(container);
        System.out.println("    flags = " + ff);
        if (ff.known)
            return ff;
        else {
            FormalityFlags pff = inhieritedFormal(container.getParent());
            ff.setFrom(pff);
            return pff;
        }
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        final String basicPackageName =
                NapkinLookAndFeel.class.getPackage().getName() + ".Napkin";
        for (int i = 0; i < NapkinGenerated.UI_TYPES.length; i++) {
            String uiType = NapkinGenerated.UI_TYPES[i];
            String uiClass = basicPackageName + uiType;
            table.put(uiType, uiClass);
        }
        Set keys = new HashSet(table.keySet());
        keys.removeAll(Arrays.asList(NapkinGenerated.UI_TYPES));
        System.out.println("keys we didn't overwrite: " + keys);
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        Font writtenBase = createFont(BASE_FONT);
        Font writtenPlain = writtenBase.deriveFont(Font.PLAIN, 12);
        Font writtenBold = writtenBase.deriveFont(Font.BOLD, 12);

        Font fixedBase = createFont(FIXED_FONT);
        Font fixedPlain = fixedBase.deriveFont(Font.PLAIN, 13);

        Object dialogPlain = writtenPlain;
        Object dialogBold = writtenBold;
        Object serifPlain = writtenPlain;
        Object sansSerifPlain = writtenPlain;
        Object monospacedPlain = fixedPlain;

        for (Iterator it = table.entrySet().iterator(); it.hasNext();) {
            Entry entry = (Entry) it.next();
            String key = (String) entry.getKey();
            if (!key.endsWith(".font"))
                continue;
            Object val = entry.getValue();
            if (val instanceof ProxyLazyValue) {
                ProxyLazyValue lazyValue = (ProxyLazyValue) val;
                val = lazyValue.createValue(table);
            }
            if (val instanceof FontUIResource) {
                FontUIResource resource = (FontUIResource) val;
                String name = resource.getFontName();
                if (name.equals("Dialog.plain")) {
                    entry.setValue(dialogPlain);
                } else if (name.equals("Dialog.bold")) {
                    entry.setValue(dialogBold);
                } else if (name.equals("Serif.plain")) {
                    entry.setValue(serifPlain);
                } else if (name.equals("SansSerif.plain")) {
                    entry.setValue(sansSerifPlain);
                } else if (name.equals("MonoSpaced.plain")) {
                    entry.setValue(monospacedPlain);
                } else {
                    System.err.println("unknown font; " + name + " for " + key);
                }
            }
        }

        Object buttonBorder =
                new ProxyLazyValue("napkin.NapkinBorders", "getButtonBorder");

        Integer zero = new Integer(0);

        Object[] napkinDefaults = {
            "Button.border", buttonBorder,
            "RadioButton.border", buttonBorder,
            "RadioButton.textIconGap", zero,
            "CheckBox.border", buttonBorder,
            "CheckBox.textIconGap", zero,
        };

        table.putDefaults(napkinDefaults);
    }

    private Font createFont(String fontName) {
        InputStream in = null;
        try {
            InputStream fin = getResourceAsStream("resources/" + fontName);
            in = new BufferedInputStream(fin);
            return Font.createFont(0, in);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            RuntimeException re = new IllegalStateException("font problem");
            throw (RuntimeException) re.initCause(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    ;   // we tried
                }
            }
        }
    }

    private InputStream getResourceAsStream(String path) {
        String fullPath = getClass().getPackage().getName() + "/" + path;
        return getClass().getClassLoader().getResourceAsStream(fullPath);
    }

    public void setIsFormal(Component c, boolean isFormal) {
        setIsFormal(c, isFormal, true);
    }

    public void setIsFormal(Component c, boolean isFormal, boolean impose) {
        if (JUST_NAPKIN)
            return;
        FormalityFlags ff = flags(c);
        ff.known = true;
        ff.formal = isFormal;
        ff.inherited = false;
        clearKids(c);
    }

    private void clear(Component c) {
        System.out.println("clear(" + NapkinUtil.descFor(c) + ")");
        clearKids(c);
        FormalityFlags ff = flags(c);
        ff.known = !ff.inherited;
    }

    private void clearKids(Component c) {
        new ComponentWalker(c, clearKidsVisitor);
        new ComponentWalker(c, updateUIVisitor);
    }

    private FormalityFlags flags(Component c) {
        return flags(c, true);
    }

    private FormalityFlags flags(Component c, boolean recurse) {
        if (JUST_NAPKIN)
            return null;
        FormalityFlags ff = (FormalityFlags) flags.get(c);
        if (ff == null) {
            System.out.println("adding flags: " + NapkinUtil.descFor(c));
            if (recurse && c instanceof Container) {
                new ComponentWalker(c, addListenerVisitor);
                ff = (FormalityFlags) flags.get(c);
            } else {
                ff = new FormalityFlags();
                flags.put(c, ff);
            }
        }
        return ff;
    }

    public void dumpFormality(Component top, PrintStream out) {
        new ComponentWalker(top, new DumpVisitor(out));
    }
}

