// $Id$

package napkin;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class NapkinUtil implements NapkinConstants {
    private static final Set printed = new HashSet();

    private static final Logger logger = Logger.getLogger("NapkinUtil");
    public static final Random random = new Random();

    private static final Map strokes = new WeakHashMap();
    private static final Map fieldsForType = new WeakHashMap();
    private static final float FOCUS_MARK_WIDTH = 1.5f;

    private static final BufferedImage textureImage;

    private static final HierarchyListener CLEAR_BACKGROUND_LISTENER =
            new HierarchyListener() {
                public void hierarchyChanged(HierarchyEvent e) {
                    Component c = e.getComponent();
                    if (c instanceof JComponent) {
                        JComponent jc = (JComponent) c;
                        if (jc.getClientProperty(IS_THEME_TOP_KEY) == null)
                            jc.putClientProperty(BACKGROUND_KEY, null);
                    }
                }
            };
    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

    private static final AlphaComposite ERASURE_COMPOSITE =
            AlphaComposite.getInstance(AlphaComposite.DST_OUT, 0.8f);

    public static boolean replace(Object current, Object candidate) {
        if (current == null)
            return true;
        return !current.equals(candidate) && current instanceof UIResource;
    }

    static {
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        ImageIcon icon = theme.getErasureMask().getIcon();
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        textureImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gi = textureImage.createGraphics();
        gi.setColor(new Color(0, 0, 0, 0));
        gi.fillRect(0, 0, w, h);
        gi.drawImage(icon.getImage(), 0, 0, icon.getImageObserver());
    }

    public interface PropertyFactory {
        Object createPropertyValue();
    }

    public static class DisabledMark {
        public final BufferedImage image;
        public final int offX;
        public final int offY;
        public final Graphics2D graphics;

        public DisabledMark(Graphics2D graphics, BufferedImage image, int offX,
                int offY) {
            this.graphics = copy(graphics);
            this.image = image;
            this.offX = offX;
            this.offY = offY;
        }
    }

    public static class DumpListener implements FocusListener {
        private Timer timer;

        public void focusGained(final FocusEvent ev) {
            if (timer != null)
                timer.stop();
            int delay = 1000; //milliseconds
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    NapkinLookAndFeel laf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
                    laf.dumpFormality(
                            ((JComponent) ev.getSource()).getTopLevelAncestor(),
                            System.out);
                }
            };
            timer = new Timer(delay, taskPerformer);
            timer.start();
            ev.getComponent().removeFocusListener(this);
        }

        public void focusLost(FocusEvent e) {
            if (timer != null)
                timer.stop();
        }
    }

    public static Object property(ComponentUI ui, String prop) {
        String name = ui.getClass().getName();
        String base = ".Napkin";
        int pos = name.lastIndexOf(base) + base.length();
        String pref = name.substring(pos, name.length() - 2);
        return pref + "." + prop;
    }

    static boolean isFormal(JComponent l) {
        NapkinLookAndFeel nlaf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
        return nlaf.isFormal(l);
    }

    public static boolean isFormal(Component c) {
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf instanceof NapkinLookAndFeel) {
            NapkinLookAndFeel nlaf = (NapkinLookAndFeel) laf;
            return nlaf.isFormal(c);
        }
        return true;
    }

    static ComponentUI uiFor(JComponent c, ComponentUI napkinUI) {
        NapkinLookAndFeel nlaf = (NapkinLookAndFeel) UIManager.getLookAndFeel();
        ComponentUI ui;
        if (nlaf.isFormal(c))
            ui = nlaf.getFormalLAF().getDefaults().getUI(c);
        else
            ui = napkinUI;
        if (logger.isLoggable(Level.FINER) && !printed.contains(c.getClass())) {
            logger.finer(c.getUIClassID() + "\n  " + napkinUI.getClass() +
                    "\n  " + c.getClass());
            printed.add(c.getClass());
        }
        return ui;
    }

    static String prBool(boolean bool, String name) {
        if (bool)
            return name;
        else
            return '!' + name;
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
        else if (c instanceof JTextField)
            desc += ": " + ((JTextField) c).getText();
        else if (c instanceof JPopupMenu)
            desc += ": " + ((JPopupMenu) c).getLabel();
        else if (c instanceof Label)
            desc += ": " + ((Label) c).getText();
        else if (c instanceof TextField)
            desc += ": " + ((TextField) c).getText();
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

    public static void installUI(JComponent c) {
        if (replace(c.getBackground(), CLEAR))
            c.setBackground(CLEAR);
        c.addHierarchyListener(CLEAR_BACKGROUND_LISTENER);
    }

    public static void uninstallUI(JComponent c) {
        c.removeHierarchyListener(CLEAR_BACKGROUND_LISTENER);
        unsetupBorder(c);
        for (int i = 0; i < CLIENT_PROPERTIES.length; i++)
            c.putClientProperty(CLIENT_PROPERTIES[i], null);
    }

    public static double leftRight(double x, boolean left) {
        return (left ? x : DrawnShapeGenerator.LENGTH - x);
    }

    public static AffineTransform copy(AffineTransform matrix) {
        if (matrix == null)
            return new AffineTransform();
        else
            return (AffineTransform) matrix.clone();
    }

    public static Graphics2D copy(Graphics g) {
        return (Graphics2D) g.create();
    }

    public static Graphics2D lineGraphics(Graphics orig, float w) {
        return lineGraphics((Graphics2D) orig, w);
    }

    public static Graphics2D lineGraphics(Graphics2D orig, float w) {
        Graphics2D lineG = copy(orig);

        Float key = new Float(w);
        Stroke stroke = (Stroke) strokes.get(key);
        if (stroke == null) {
            stroke = new BasicStroke(w, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);
            strokes.put(key, stroke);
        }
        lineG.setStroke(stroke);
        lineG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return lineG;
    }

    public static Graphics2D defaultGraphics(Graphics g1, Component c) {
        return defaultGraphics(g1, c, c);
    }

    public static Graphics2D defaultGraphics(Graphics g1, Component c,
            Component themeFrom) {
        Graphics2D g = (Graphics2D) g1;
        syncWithTheme(g, c, themeFrom);
        boolean enabled = c.isEnabled() && !(c instanceof FakeEnabled);
        if (!enabled && c instanceof JComponent) {
            Rectangle r = g.getClipBounds();
            int w = r.width;
            int h = r.height;
            BufferedImage tmp =
                    new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tg = tmp.createGraphics();
            Composite origComp = tg.getComposite();
            tg.setComposite(AlphaComposite.Clear);
            tg.fillRect(0, 0, w, h);
            tg.setComposite(origComp);
            int offX = -r.x;
            int offY = -r.y;
            tg.translate(offX, offY);

            // copy in values from the original (should be a method to do this)
            tg.setBackground(g.getBackground());
            tg.setClip(0, 0, w, h);
            tg.setColor(g.getColor());
            tg.setComposite(g.getComposite());
            tg.setFont(g.getFont());
            tg.setPaint(g.getPaint());
            tg.setRenderingHints(g.getRenderingHints());
            tg.setStroke(g.getStroke());

            JComponent jc = (JComponent) c;
            DisabledMark mark = new DisabledMark(g, tmp, offX, offY);
            jc.putClientProperty(DISABLED_MARK_KEY, mark);
            g = tg;
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        setupBorder(c);
        return g;
    }

    public static void syncWithTheme(Graphics2D g, Component c) {
        syncWithTheme(g, c, c);
    }

    public static void syncWithTheme(Graphics2D g, Component c,
            Component themeFrom) {
        NapkinTheme theme = themeFor(themeFrom);
        Color penColor = theme.getPenColor();
        if (!penColor.equals(c.getForeground())) {
            c.setForeground(penColor);
            if (g != null)
                g.setColor(penColor);
        }
    }

    public static NapkinTheme themeFor(Component c) {
        JComponent themeTop = themeTopFor(c);
        if (themeTop == null)
            return NapkinTheme.Manager.getCurrentTheme();
        else
            return (NapkinTheme) themeTop.getClientProperty(THEME_KEY);
    }

    public static void finishGraphics(Graphics g1, Component c) {
        if (!(c instanceof JComponent))
            return;

        JComponent jc = (JComponent) c;
        DisabledMark mark = (DisabledMark) jc.getClientProperty(
                DISABLED_MARK_KEY);
        if (mark == null)
            return;

        jc.putClientProperty(DISABLED_MARK_KEY, null);
        Graphics2D tg = (Graphics2D) g1;
        tg.setComposite(ERASURE_COMPOSITE);
        Point start = getStart(jc, tg, null, false);
        int w = textureImage.getWidth();
        int h = textureImage.getHeight();
        Rectangle anchor = new Rectangle(w - start.x, h - start.y, w, h);
        tg.setPaint(new TexturePaint(textureImage, anchor));
        tg.fillRect(0, 0, mark.image.getWidth(), mark.image.getHeight());

        Graphics2D g = mark.graphics;
        g.drawImage(mark.image, -mark.offX, -mark.offY, jc);
    }

    private static void setupBorder(Component c) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Border b = jc.getBorder();
            if (b != null && !(b instanceof NapkinBorder))
                jc.setBorder(new NapkinWrappedBorder(b));
        }
    }

    private static void unsetupBorder(JComponent c) {
        Border b = c.getBorder();
        if (b instanceof NapkinWrappedBorder) {
            NapkinWrappedBorder nb = (NapkinWrappedBorder) b;
            c.setBorder(nb.getFormalBorder());
        } else if (b instanceof NapkinBorder) {
            c.setBorder(null);
        }
    }

    static AffineTransform scaleMat(double scale) {
        AffineTransform mat = new AffineTransform();
        mat.scale(scale, scale);
        return mat;
    }

    static void transform(AffineTransform matrix, double[] points) {
        if (matrix != null)
            matrix.transform(points, 0, points, 0, points.length / 2);
    }

    static JButton createArrowButton(int pointTowards, JComponent holder) {
        int size = NapkinIconFactory.ArrowIcon.DEFAULT_SIZE;
        return createArrowButton(pointTowards, size, holder);
    }

    static JButton
            createArrowButton(int pointTowards, int size, JComponent themeTop) {

        Icon arrow = NapkinIconFactory.createArrowIcon(pointTowards, size);
        JButton button = new JButton(arrow);
        button.setBorderPainted(false);
        Dimension dim = new Dimension(size + 3, size + 3);
        button.setPreferredSize(dim);
        button.setMinimumSize(dim);
        button.putClientProperty(THEME_TOP_KEY, themeTop);
        return button;
    }

    static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    public static DrawnLineHolder paintLine(Graphics g, boolean vertical,
            DrawnLineHolder holder, Rectangle bounds) {
        if (holder == null)
            holder = new DrawnLineHolder(DrawnCubicLineGenerator.INSTANCE,
                    vertical);
        holder.shapeUpToDate(bounds, null);
        Graphics2D lineG = copy(g);
        if (vertical)
            lineG.translate(bounds.x + bounds.width / 2, 0);
        else
            lineG.translate(0, bounds.y + bounds.height / 2);
        holder.draw(lineG);
        return holder;
    }

    static void dumpTo(String file, JComponent c) {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(new FileWriter(file)));
            Set dumped = new HashSet();
            dumpTo(out, c, c.getClass(), 0, dumped);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
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

    public static Object[] reallocate(Object[] orig, int count) {
        if (count == orig.length)
            return orig;
        Class componentType = orig.getClass().getComponentType();
        Object[] next = (Object[]) Array.newInstance(componentType, count);
        System.arraycopy(orig, 0, next, 0, Math.min(orig.length, next.length));
        return next;
    }

    public static void printPair(String label, double x, double y) {
        System.out.println(label + ": " + x + ", " + y);
    }

    public static void setupThemeTop(JComponent c, NapkinTheme theme) {
        c.putClientProperty(IS_THEME_TOP_KEY, Boolean.TRUE);
        c.putClientProperty(THEME_TOP_KEY, c);
        c.putClientProperty(THEME_KEY, theme);
    }

    public static NapkinTheme background(Graphics g1, Component c) {
        if (!c.isOpaque())
            return null;

        JComponent themeTop = themeTopFor(c);
        if (themeTop == null)
            return null;

        Graphics2D g = (Graphics2D) g1;
        NapkinTheme theme = (NapkinTheme) themeTop.getClientProperty(THEME_KEY);
        NapkinBackground bg = theme.getPaper();

        Rectangle pRect = bounds(themeTop, g);
        Rectangle cRect = bounds(c, g);

        bg.paint(c, g, pRect, cRect, insets(c));
        return theme;
    }

    private static Rectangle bounds(Component c, Graphics2D g) {
        Insets in = insets(c);
        Point start = getStart(c, g, in, within(c, JViewport.class));
        int x = start.x;
        int y = start.y;
        int width = c.getWidth() + in.left + in.right;
        int height = c.getHeight() + in.top + in.bottom;
        return new Rectangle(x, y, width, height);
    }

    private static boolean within(Component c, Class type) {
        if (c == null)
            return false;
        if (c instanceof JTree)
            return false;   // just a workaround
        if (type.isAssignableFrom(c.getClass())) {
            System.out.println("--");
            return true;
        }
        return within(c.getParent(), type);
    }

    private static Insets insets(Component c) {
        Insets in;
        if (c instanceof JComponent)
            in = ((JComponent) c).getInsets();
        else
            in = NO_INSETS;
        return in;
    }

    public static JComponent themeTopFor(Component c) {
        if (c == null)
            return null;

        if (!(c instanceof JComponent))
            return themeTopFor(c.getParent());

        JComponent jc = (JComponent) c;
        JComponent themeTop = (JComponent) jc.getClientProperty(THEME_TOP_KEY);
        if (themeTop != null && themeTop.getClientProperty(THEME_KEY) != null)
            return themeTop;

        themeTop = themeTopFor(jc.getParent());
        if (themeTop == null) {
            // This can happen to any entity without a JComponent ancestors.
            // If so, we nominate ourselves as the relevant background paper.
            // Unfortunately this is common: JFrame et al are not JComponents
            // and have no UI classes.  So this is what you get for any regular
            // top-level window we haven't overridden.  I wonder why JFrame and
            // friends are like this.
            setupThemeTop(jc, NapkinTheme.Manager.getCurrentTheme());
            return jc;
        }
        jc.putClientProperty(THEME_TOP_KEY, themeTop);
        jc.putClientProperty(THEME_KEY, themeTop.getClientProperty(THEME_KEY));
        return themeTop;
    }

    private static Point getStart(Component c, Graphics2D g, Insets insets,
            boolean print) {
        Point start = new Point();
        if (insets != null)
            start.setLocation(-insets.left, -insets.top);
        while (c != null && !isPaper(c)) {
            if (print)
                System.out.println(
                        "(" + c.getX() + ", " + c.getY() + "): " + descFor(c));

            start.x += c.getX();
            start.y += c.getY();
            if (print)
                System.out.println("start = " + start);
            c = c.getParent();
        }
        return start;
    }

    private static boolean isPaper(Component c) {
        if (c instanceof JComponent)
            return (((JComponent) c).getClientProperty(IS_THEME_TOP_KEY) !=
                    null);
        return false;
    }

    public static void dumpObject(Object obj, String fileName) {
        PrintStream out = null;
        try {
            out =
                    new PrintStream(new BufferedOutputStream(
                            new FileOutputStream(fileName)));
            dumpObject(obj, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
    }

    public static void dumpObject(Object obj, PrintStream out) {
        Map known = new HashMap();
        dumpObject(obj, out, 0, known);
    }

    static final Set skip;

    static {
        skip = new HashSet();
//        skip.add("source");
//        skip.add("mostRecentKeyValue");
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

    /**
     * This is pretty ugly -- to make this a utility method, I need to be able
     * to have some way to invoke the paint method of the button's superclass.
     * To do that, I have to invent a method that will do that.
     * <p/>
     * In principle this code could be shared by inheritence, overriding
     * paintText in BasicButtonUI, but there is no way I could change the actual
     * behavior of that method so that (say) NapkinCheckBoxUI, which must
     * inherit from BasicCheckBoxUI (and thus from BasicButtonUI) would change
     * behavior.  So I need a utility method they share, and thus the hack.
     * Sigh.
     */
    public static void
            paintText(Graphics g, JComponent c, Rectangle textRect,
            String text, int textOffset, DrawnLineHolder line,
            boolean isDefault, NapkinPainter helper) {

        Graphics2D ulG;
        if (isDefault) {
            if (line == null)
                line = new DrawnLineHolder(new DrawnCubicLineGenerator());
            ulG = copy(g);
            FontMetrics fm = ulG.getFontMetrics();
            line.shapeUpToDate(textRect, fm);
            int x = textOffset;
            int y = textOffset;
            ulG.translate(x, y);
            ulG.setColor(NapkinUtil.themeFor(c).getCheckColor());
            line.setWidth(FOCUS_MARK_WIDTH);
            line.draw(ulG);
        }

        Color textColor = c.getForeground();
        if (c instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
                textColor = themeFor(c).getSelectionColor();
        }
        g.setColor(textColor);
        c = wrapIfNeeded(c);
        helper.superPaintText(g, c, textRect, text);
    }

    private static JComponent wrapIfNeeded(JComponent c) {
        if (!(c instanceof AbstractButton))
            return c;

        if (c.isEnabled())
            return c;

        if (c instanceof JMenuItem)
            return new FakeEnabledMenuItem(c);
        else
            return new FakeEnabledButton(c);
    }

    public static Object
            getProperty(JComponent c, String key, PropertyFactory factory) {
        Object value = c.getClientProperty(key);
        if (value == null) {
            value = factory.createPropertyValue();
            c.putClientProperty(key, value);
        }
        return value;
    }

    public static String toString(Color c) {
        return "#" + Integer.toHexString(c.getRGB()) + "/" +
                Integer.toHexString(c.getAlpha());
    }

    public static String getProperty(final String prop,
            final String defaultValue) {
        String themeName;
        try {
            themeName = (String)
                    AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            return System.getProperty(prop,
                                    defaultValue);
                        }
                    });
        } catch (SecurityException e) {
            themeName = null;
        }
        if (themeName == null)
            themeName = defaultValue;
        return themeName;
    }

    public static Object ifReplace(Object current, Object candidate) {
        return (replace(current, candidate) ? candidate : current);
    }

    public static Color ifReplace(Color current, Color candidate) {
        return (replace(current, candidate) ? candidate : current);
    }
}
