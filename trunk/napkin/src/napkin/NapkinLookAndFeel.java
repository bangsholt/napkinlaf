// $Id$

package napkin;

import napkin.ComponentWalker.Visitor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
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

public class NapkinLookAndFeel extends BasicLookAndFeel
        implements NapkinConstants {

    private LookAndFeel formal;
    private final Map flags = new WeakHashMap();

    private static boolean gotFonts;
    private static Font scrawl;
    private static Font scrawlBold;
    private static Font fixed;

    //!! Want to make this selectable
//    private static final String SCRAWL_NAME = "nigmaScrawl5bBRK";
    private static final String SCRAWL_NAME = "Felt Tip Roman";
    private static final String SCRAWL_BOLD_NAME = "Felt Tip Roman-Bold";
    private static final String FIXED_NAME = "ProFont";

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

    protected void initSystemColorDefaults(UIDefaults table) {
        super.initSystemColorDefaults(table);
        // make a copy so we can modify the table as we read the key set
        Set keys = new HashSet(table.keySet());
        for (Iterator it = keys.iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (key.endsWith("Text")) {
                table.put(key, BLACK);
                if (key.indexOf("Caption") < 0)
                    table.put(key.substring(0, key.length() - 4), CLEAR);
            }
        }
    }

    protected void initComponentDefaults(UIDefaults table) {
        super.initComponentDefaults(table);

        getFonts();
        Integer size = new Integer(15);
        Integer plain = new Integer(Font.PLAIN);
        Integer bold = new Integer(Font.BOLD);
        Object dialogPlain = fontValue(scrawl, SCRAWL_NAME, plain, size);
        Object dialogBold = fontValue(scrawlBold, SCRAWL_BOLD_NAME, bold, size);
        Object serifPlain = fontValue(scrawl, SCRAWL_NAME, plain, size);
        Object sansSerifPlain = fontValue(scrawl, SCRAWL_NAME, plain, size);
        Object monospacedPlain = fontValue(fixed, FIXED_NAME, plain, size);

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
            "TabbedPane.contentBorderInsets", DrawnBorder.DEFAULT_INSETS,
        };

        table.putDefaults(napkinDefaults);
    }

    private static synchronized void getFonts() {
        if (gotFonts)
            return;
        //!! Make this selectable
//        scrawl = tryToLoadFont("aescr5b.ttf");
        scrawl = tryToLoadFont("FeltTipRoman.ttf");
        scrawlBold = tryToLoadFont("FeltTipRoman-Bold.ttf");
        fixed = tryToLoadFont("Mcgf____.ttf");
    }

    private static Font tryToLoadFont(final String fontName) {
        try {
            ClassLoader cl = NapkinLookAndFeel.class.getClassLoader();
            String fontRes = "napkin/resources/" + fontName;
            InputStream fontDef = cl.getResourceAsStream(fontRes);
            if (fontDef == null)
                System.err.println("Could not find font " + fontName);
            else
                return Font.createFont(Font.TRUETYPE_FONT, fontDef);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object
            fontValue(Font font, String name, Integer style, Number size) {

        if (font != null) {
            return new FontUIResource(
                    font.deriveFont(style.intValue(), size.floatValue()));
        }
        String resName = "javax.swing.plaf.FontUIResource";
        Object[] args = new Object[]{name, style, size};
        return new ProxyLazyValue(resName, null, args);
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

