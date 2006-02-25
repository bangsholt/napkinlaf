// $Id$

package napkin;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.*;

public class NapkinUtil implements NapkinConstants {
    private static final Map<Float, Stroke> strokes =
            new WeakHashMap<Float, Stroke>();

    private static boolean drawingDisabled;

    private static final BufferedImage textureImage;

    private static final float FOCUS_MARK_WIDTH = 1.5f;

    private static final PropertyChangeListener PROPERTY_LISTENER =
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    String prop = event.getPropertyName();
                    if (!prop.equals("opaque"))
                        return;
                    JComponent c = (JComponent) event.getSource();
                    Boolean val = (Boolean) event.getNewValue();
                    if (!val.booleanValue())
                        val = null;
                    c.putClientProperty(OPAQUE_KEY, val);
                }
            };

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

    private static final AlphaComposite ERASURE_COMPOSITE =
            AlphaComposite.getInstance(AlphaComposite.DST_OUT, 0.8f);
    private static final Set<Class<?>> printed = new HashSet<Class<?>>();
    private static final Stack<Object> themeStack = new Stack<Object>();
    private static final Stack<Component> paperStack = new Stack<Component>();

    static {
        /*
        * This is a test
        */
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
        /** @noinspection PublicField */
        public final BufferedImage image;
        /** @noinspection PublicField */
        public final int offX;
        /** @noinspection PublicField */
        public final int offY;
        /** @noinspection PublicField */
        public final Graphics2D graphics;

        public DisabledMark(Graphics2D graphics, BufferedImage image, int offX,
                int offY) {
            this.graphics = copy(graphics);
            this.image = image;
            this.offX = offX;
            this.offY = offY;
        }
    }

    public interface Logs {
        Logger ui = Logger.getLogger("napkin.util");
        Logger paper = Logger.getLogger("napkin.paper");
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
        if (Logs.ui.isLoggable(Level.FINER) &&
                !printed.contains(c.getClass())) {

            Logs.ui.finer(c.getUIClassID() + "\n  " + napkinUI.getClass() +
                    "\n  " + c.getClass());
            printed.add(c.getClass());
        }
        return ui;
    }

    public static void installUI(JComponent c) {
        if (c.isOpaque()) {
            c.putClientProperty(OPAQUE_KEY, Boolean.TRUE);
            c.setOpaque(false);
        }
        c.addPropertyChangeListener(PROPERTY_LISTENER);
        if (replace(c.getBackground(), CLEAR))
            c.setBackground(CLEAR);
    }

    public static void uninstallUI(JComponent c) {
        c.removePropertyChangeListener(PROPERTY_LISTENER);
        if (shouldMakeOpaque(c))
            c.setOpaque(true);
        unsetupBorder(c);
        for (String clientProp : CLIENT_PROPERTIES)
            c.putClientProperty(clientProp, null);
    }

    private static boolean shouldMakeOpaque(JComponent c) {
        if (isGlassPane(c))
            return false;
        return (c.getClientProperty(OPAQUE_KEY) == Boolean.TRUE &&
                !c.isOpaque());
    }

    private static boolean isGlassPane(Component c) {
        if (c instanceof JComponent)
            return isGlassPane((JComponent) c);
        else
            return false;
    }

    private static boolean isGlassPane(JComponent c) {
        JRootPane rootPane = c.getRootPane();
        return (rootPane != null && rootPane.getGlassPane() == c);
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

        Stroke stroke = strokes.get(w);
        if (stroke == null) {
            stroke = new BasicStroke(w, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND);
            strokes.put((Float) w, stroke);
        }
        lineG.setStroke(stroke);
        lineG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return lineG;
    }

    public static Graphics2D defaultGraphics(Graphics g1, Component c) {
        Graphics2D g = (Graphics2D) g1;
        syncWithTheme(g, c);
        boolean enabled = c.isEnabled();
        if (!enabled && c instanceof JComponent && !drawingDisabled) {
            drawingDisabled = true;
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
        if (isPaper(c)) {
            paperStack.push(c);
            themeStack.push(((JComponent) c).getClientProperty(THEME_KEY));
            dumpStacks();
        }

        NapkinTheme theme = currentTheme(c);
        Color themePen = theme.getPenColor();

        Color fgColor = ifReplace(c.getForeground(), themePen);
        // explicitly check for equality because two things depend on it
        if (!fgColor.equals(c.getForeground())) {
            c.setForeground(fgColor);
            if (g != null)
                g.setColor(fgColor);
        }

        if (c instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent) c;
            Color selColor = ifReplace(tc.getSelectedTextColor(), themePen);
            // just set, as the method does work only if it's different
            tc.setSelectedTextColor(selColor);
        }
    }

    public static void syncWithTheme(Border border, Component c) {
        if (border instanceof TitledBorder) {
            TitledBorder tb = (TitledBorder) border;
            Color pen = currentTheme(c).getPenColor();
            tb.setTitleColor(ifReplace(tb.getTitleColor(), pen));
        } else if (border instanceof CompoundBorder) {
            CompoundBorder cb = (CompoundBorder) border;
            syncWithTheme(cb.getInsideBorder(), c);
            syncWithTheme(cb.getOutsideBorder(), c);
        }
    }

    public static NapkinTheme currentTheme(Component c) {
        if (themeStack.isEmpty())
            return (NapkinTheme) themeTopFor(c).getClientProperty(THEME_KEY);
        return (NapkinTheme) themeStack.peek();
    }

    public static Component currentPaper(Component c) {
        if (paperStack.isEmpty())
            return themeTopFor(c);
        return paperStack.peek();
    }

    public static void finishGraphics(Graphics g1, Component c) {
        if (c == currentPaper(c)) {
            if (!paperStack.isEmpty())
                paperStack.pop();
            if (!themeStack.isEmpty())
                themeStack.pop();
            dumpStacks();
        }

        if (!(c instanceof JComponent))
            return;

        JComponent jc = (JComponent) c;
        DisabledMark mark = (DisabledMark) jc.getClientProperty(
                DISABLED_MARK_KEY);
        if (mark == null)
            return;

        drawingDisabled = false;
        jc.putClientProperty(DISABLED_MARK_KEY, null);
        Graphics2D tg = (Graphics2D) g1;
        tg.setComposite(ERASURE_COMPOSITE);
        Point start = getStart(jc, null);
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
                jc.setBorder(NapkinWrappedBorder.wrap(b));
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

    static JButton createArrowButton(int pointTowards) {
        int size = NapkinIconFactory.ArrowIcon.DEFAULT_SIZE;
        return createArrowButton(pointTowards, size);
    }

    static JButton createArrowButton(int pointTowards, int size) {
        Icon arrow = NapkinIconFactory.createArrowIcon(pointTowards, size);
        JButton button = new JButton(arrow);
        button.setBorderPainted(false);
        Dimension dim = new Dimension(size + 3, size + 3);
        button.setPreferredSize(dim);
        button.setMinimumSize(dim);
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

    public static Object[] reallocate(Object[] orig, int size) {
        if (size == orig.length)
            return orig;
        Class<?> componentType = orig.getClass().getComponentType();
        Object[] next = (Object[]) Array.newInstance(componentType, size);
        System.arraycopy(orig, 0, next, 0, Math.min(orig.length, next.length));
        return next;
    }

    public static void printPair(Logger logger, Level level, String label,
            double x, double y) {
        logger.log(level, label + ": " + x + ", " + y);
    }

    public static void setupPaper(JComponent c, int theme) {
        c.setOpaque(true);
        NapkinTheme baseTheme = NapkinTheme.Manager.getCurrentTheme();
        c.putClientProperty(THEME_KEY, baseTheme.getTheme(theme));
    }

    public static NapkinTheme background(Graphics g1, Component c) {
        if (isGlassPane(c))
            return null;

        Graphics2D g = (Graphics2D) g1;
        NapkinTheme theme = currentTheme(c);
        NapkinBackground bg = theme.getPaper();

        Rectangle pRect = bounds(currentPaper(c));
        Rectangle cRect = bounds(c);

        bg.paint(c, g, pRect, cRect, insets(c));
        return theme;
    }

    private static Rectangle bounds(Component c) {
        Insets in = insets(c);
        Point start = getStart(c, in);
        int x = start.x;
        int y = start.y;
        int width = c.getWidth() + in.left + in.right;
        int height = c.getHeight() + in.top + in.bottom;
        return new Rectangle(x, y, width, height);
    }

    /**
     * This is basically meant to help do debugging only inside a certain type
     * of component.  For example, you might turn on a debugging flag only for
     * components underneath a <tt>JViewport.class</tt> (inclusive; that is, the
     * <tt>JViewport</tt> itself would be included as <tt>true</tt>).
     *
     * @param c    A component that might be under a component of a given type
     * @param type The type of component
     *
     * @return <tt>true</tt> if this component is of the type or has such a
     *         component as an ancestor.
     *
     * @noinspection TailRecursion
     */
    public static boolean within(Component c, Class<?> type) {
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
        if (c instanceof Container)
            in = ((Container) c).getInsets();
        else
            in = NO_INSETS;
        return in;
    }

    /** @noinspection TailRecursion */
    public static JComponent themeTopFor(Component c) {
        if (c == null)
            return null;

        if (!(c instanceof JComponent))
            return themeTopFor(c.getParent());

        JComponent jc = (JComponent) c;
        if (jc.getClientProperty(THEME_KEY) != null)
            return jc;

        JComponent themeTop = themeTopFor(jc.getParent());
        if (themeTop == null) {
            // This can happen to any entity without a JComponent ancestors.
            // If so, we nominate ourselves as the relevant background paper.
            // Unfortunately this is common: JFrame et al are not JComponents
            // and have no UI classes.  So this is what you get for any regular
            // top-level window we haven't overridden.  I wonder why JFrame and
            // friends are like this.
            setupPaper(jc, NapkinTheme.BASIC_THEME);
            return jc;
        }
        return themeTop;
    }

    private static Point getStart(Component c, Insets insets) {
        Point start = new Point();
        if (insets != null)
            start.setLocation(-insets.left, -insets.top);
        Component paper = currentPaper(c);
        while (c != null && c != paper) {
            start.x += c.getX();
            start.y += c.getY();
            c = c.getParent();
        }
        return start;
    }

    private static boolean isPaper(Component c) {
        if (c instanceof JComponent)
            return (((JComponent) c).getClientProperty(THEME_KEY) != null);
        return false;
    }

    public static void
            paintButtonText(Graphics g, JComponent c, Rectangle textRect,
            String text, int textOffset, DrawnLineHolder line,
            boolean isDefault, NapkinTextPainter helper) {

        Graphics2D ulG;
        if (isDefault) {
            if (line == null)
                line = new DrawnLineHolder(new DrawnCubicLineGenerator());
            ulG = copy(g);
            FontMetrics fm = ulG.getFontMetrics();
            line.shapeUpToDate(textRect, fm);
            ulG.translate(textOffset, textOffset);
            ulG.setColor(currentTheme(c).getCheckColor());
            line.setWidth(FOCUS_MARK_WIDTH);
            line.draw(ulG);
        }

        Color textColor = c.getForeground();
        if (c instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) c;
            ButtonModel model = button.getModel();
            if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
                textColor = currentTheme(c).getSelectionColor();
        }
        g.setColor(textColor);
        helper.superPaintText(g, c, textRect, text);
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

    public static boolean replace(Object current, Object candidate) {
        if (current == null)
            return true;
        return !current.equals(candidate) && current instanceof UIResource;
    }

    public static Object ifReplace(Object current, Object candidate) {
        return (replace(current, candidate) ? candidate : current);
    }

    public static Color ifReplace(Color current, Color candidate) {
        return (replace(current, candidate) ? candidate : current);
    }

    public static void drawStroke(GeneralPath path, AffineTransform matrix,
            double x1, double y1, double x2, double y2,
            double baseAngle, DrawnShapeGenerator lineGen) {

        double xDelta = x1 - x2;
        double yDelta = y1 - y2;
        double angle = Math.atan2(xDelta, yDelta);
        AffineTransform mat = copy(matrix);
        mat.translate(x1, y1);
        mat.rotate(baseAngle + angle);
        double len = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
        mat.scale(len / LENGTH, 1);
        DrawnShapeGenerator.addLine(path, mat, lineGen);
    }

    public static void update(Graphics g, JComponent c, NapkinPainter painter) {
        g = defaultGraphics(g, c);
        NapkinTheme theme = background(g, c);
        painter.superPaint(g, c, theme);
        finishGraphics(g, c);
    }

    private static void dumpStacks() {
        if (!Logs.paper.isLoggable(Level.FINER))
            return;

        if (themeStack.size() != paperStack.size())
            System.out.println("!!!");
        StringBuilder dump = new StringBuilder(NapkinDebug.count).append(":\t");
        NapkinDebug.count++;
        for (int i = 0; i < paperStack.size(); i++)
            dump.append(". ");
        if (!themeStack.isEmpty()) {
            dump.append(themeStack.peek()).append(" / ").append(
                    NapkinDebug.descFor(paperStack.peek()));
        }
        Logs.paper.log(Level.FINER, dump.toString());
    }

    public static IOException tryClose(Closeable fonts) {
        try {
            fonts.close();
            return null;
        } catch (IOException e) {
            return e;
        }
    }

    public static void centerBoldText(Component c, Graphics2D g, float x,
            float y, float size, String s) {

        Font f = currentTheme(c).getBoldTextFont().deriveFont(Font.BOLD, size);
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = f.getStringBounds(s, frc);
        LineMetrics metrics = f.getLineMetrics(s, frc);
        float width = (float) bounds.getWidth();     // The width of our text
        float lineheight = metrics.getHeight();      // Total line height
        float ascent = metrics.getAscent();          // Top of text to baseline

        Font orig = g.getFont();
        g.setFont(f);
        g.drawString(s, x - width / 2, y - lineheight / 2 + ascent);
        g.setFont(orig);
    }
}

