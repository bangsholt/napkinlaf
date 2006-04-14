package net.sourceforge.napkinlaf.util;

import java.util.concurrent.atomic.AtomicBoolean;
import net.sourceforge.napkinlaf.NapkinKnownTheme;
import net.sourceforge.napkinlaf.NapkinTheme;
import net.sourceforge.napkinlaf.borders.NapkinBevelBorder;
import net.sourceforge.napkinlaf.borders.NapkinBorder;
import net.sourceforge.napkinlaf.borders.NapkinCompoundBorder;
import net.sourceforge.napkinlaf.borders.NapkinEtchedBorder;
import net.sourceforge.napkinlaf.fonts.MergedFontGraphics2D;
import net.sourceforge.napkinlaf.shapes.AbstractDrawnGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import static net.sourceforge.napkinlaf.util.NapkinConstants.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NapkinUtil {
    private static final Map<Float, Stroke> strokes =
            new WeakHashMap<Float, Stroke>();

    private static final BufferedImage textureImage;

    private static final float FOCUS_MARK_WIDTH = 1.5f;

    private static final String BACKGROUND = "background";
    private static final String BORDER = "border";
    private static final String OPAQUE = "opaque";
    private static final String ROLL_OVER =
            AbstractButton.ROLLOVER_ENABLED_CHANGED_PROPERTY;

    private static final PropertyChangeListener BACKGROUND_LISTENER =
            new PropertyChangeListener() {
                private AtomicBoolean overriding = new AtomicBoolean(false);
                public void propertyChange(PropertyChangeEvent event) {
                    // check if this is an external call
                    if (overriding.compareAndSet(false, true)) {
                        JComponent c = (JComponent) event.getSource();
                        Color color = (Color) event.getNewValue();
                        c.putClientProperty(BACKGROUND_KEY, color);
                        if (replaceBackground(color)) {
                            c.setBackground(CLEAR);
                        }
                        overriding.set(false);
                    }
                }
            };

    private static final PropertyChangeListener BORDER_LISTENER =
            new PropertyChangeListener() {
                private AtomicBoolean overriding = new AtomicBoolean(false);
                public void propertyChange(PropertyChangeEvent event) {
                    // check if this is an external call
                    if (overriding.compareAndSet(false, true)) {
                        JComponent c = (JComponent) event.getSource();
                        Border border = (Border) event.getNewValue();
                        if (!(border instanceof NapkinBorder)) {
                            c.putClientProperty(BORDER_KEY, border);
                            Border newBorder = wrapBorder(border);
                            if (newBorder != border) {
                                c.setBorder(newBorder);
                            }
                        }
                        overriding.set(false);
                    }
                }
            };

    @SuppressWarnings({"ObjectEquality"})
    private static final PropertyChangeListener OPAQUE_LISTENER =
            new PropertyChangeListener() {
                private AtomicBoolean overriding = new AtomicBoolean(false);
                public void propertyChange(PropertyChangeEvent event) {
                    // check if this is an external call
                    if (overriding.compareAndSet(false, true)) {
                        JComponent c = (JComponent) event.getSource();
                        boolean val = (Boolean) event.getNewValue();
                        c.putClientProperty(OPAQUE_KEY, val ? Boolean.TRUE : null);
                        if (val && isTranparent(c.getBackground())) {
                            c.setOpaque(false);
                        }
                        overriding.set(false);
                    }
                }
            };

    private static final PropertyChangeListener ROLL_OVER_LISTENER =
            new PropertyChangeListener() {
                private AtomicBoolean overriding = new AtomicBoolean(false);
                public void propertyChange(PropertyChangeEvent event) {
                    // check if this is an external call
                    if (overriding.compareAndSet(false, true)) {
                        AbstractButton button = (AbstractButton) event.getSource();
                        boolean val = (Boolean) event.getNewValue();
                        button.putClientProperty(ROLL_OVER_ENABLED, val);
                        if (!val) {
                            button.setRolloverEnabled(true);
                        }
                        overriding.set(false);
                    }
                }
            };

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);
    private static final int CLIP_OFFSET = 10;
    private static final int CLIP_INSET = CLIP_OFFSET * 2;

    private static final AlphaComposite ERASURE_COMPOSITE =
            AlphaComposite.getInstance(AlphaComposite.DST_OUT, 0.9f);
    private static final Stack<NapkinTheme> themeStack =
            new Stack<NapkinTheme>();
    private static final Stack<Component> paperStack = new Stack<Component>();

    private static SoftReference<BufferedImage> erasureBuffer =
            new SoftReference<BufferedImage>(null);

    static {
        /*
         * This is a test (but we are actually using it!)
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

    private static final DrawnLineHolder highLightLongLine;
    private static final DrawnLineHolder highLightShortLine;

    static {
        DrawnCubicLineGenerator lineGen = new DrawnCubicLineGenerator();
        RandomValue rVal = lineGen.getLeft().getY();
        rVal.setRange(rVal.getRange() * 2.5d);
        rVal = lineGen.getRight().getY();
        rVal.setRange(rVal.getRange() * 2.5d);
        highLightLongLine = new DrawnLineHolder(lineGen);

        lineGen = new DrawnCubicLineGenerator();
        highLightShortLine = new DrawnLineHolder(lineGen);
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
        Logger paper = Logger.getLogger("net.sourceforge.napkinlaf.paper");
    }

    @SuppressWarnings({"SameParameterValue"})
    public static Object property(ComponentUI ui, String prop) {
        String name = ui.getClass().getName();
        String base = ".Napkin";
        int pos = name.lastIndexOf(base) + base.length();
        String pref = name.substring(pos, name.length() - 2);
        return pref + "." + prop;
    }

    @SuppressWarnings({"ObjectEquality"})
    private static boolean replaceBackground(Color bgColor) {
        return bgColor == null || (!(bgColor instanceof AlphaColorUIResource)
                && bgColor.getRed() == bgColor.getGreen()
                && bgColor.getGreen() == bgColor.getBlue());
    }

    private static boolean isTranparent(Color bgColor) {
        return bgColor == CLEAR || bgColor == HIGHLIGHT_CLEAR;
    }

    public static void installUI(JComponent c) {
        // opaqueness override
        if (c.isOpaque()) {
            c.putClientProperty(OPAQUE_KEY, Boolean.TRUE);
            c.setOpaque(false);
        }
        c.addPropertyChangeListener(OPAQUE, OPAQUE_LISTENER);
        // AbstractButton-specific overrides
        if (c instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) c;
            // roll-over-enabled override
            c.putClientProperty(ROLL_OVER_ENABLED, button.isRolloverEnabled());
            button.setRolloverEnabled(true);
            c.addPropertyChangeListener(ROLL_OVER, ROLL_OVER_LISTENER);
        }
        // background colour override
        Color bgColor = c.getBackground();
        c.putClientProperty(BACKGROUND_KEY, bgColor);
        if (replaceBackground(bgColor)) {
            c.setBackground(CLEAR);
        }
        c.addPropertyChangeListener(BACKGROUND, BACKGROUND_LISTENER);
        // border override
        Border b = c.getBorder();
        c.putClientProperty(BORDER_KEY, b);
        Border nb = wrapBorder(b);
        if (nb != b) {
            c.setBorder(nb);
        }
        c.addPropertyChangeListener(BORDER, BORDER_LISTENER);
    }

    public static void uninstallUI(JComponent c) {
        // restore from border override
        c.removePropertyChangeListener(BORDER, BORDER_LISTENER);
        Border border = (Border) c.getClientProperty(BORDER_KEY);
        if (border != c.getBorder()) {
            c.setBorder(border);
        }
        // restore from background colour override
        c.removePropertyChangeListener(BACKGROUND, BACKGROUND_LISTENER);
        c.setBackground((Color) c.getClientProperty(BACKGROUND_KEY));
        // AbstractButton-specific overrides
        if (c instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) c;
            // restore from roll-over-enabled override
            c.removePropertyChangeListener(ROLL_OVER, ROLL_OVER_LISTENER);
            button.setRolloverEnabled(
                    (Boolean) c.getClientProperty(ROLL_OVER_ENABLED));
        }
        // restore from opaqueness override
        c.removePropertyChangeListener(OPAQUE, OPAQUE_LISTENER);
        c.setOpaque(c.getClientProperty(OPAQUE_KEY) == Boolean.TRUE);
        // remove Napkin-specific client properties
        for (String clientProp : CLIENT_PROPERTIES) {
            c.putClientProperty(clientProp, null);
        }
    }

    @SuppressWarnings({"ObjectEquality"})
    private static boolean isGlassPane(JComponent c) {
        JRootPane rootPane = c.getRootPane();
        return (rootPane != null && rootPane.getGlassPane() == c);
    }

    public static double leftRight(double x, boolean left) {
        return (left ? x : LENGTH - x);
    }

    public static Graphics2D copy(Graphics g) {
        return (Graphics2D) g.create();
    }

    public static Graphics2D lineGraphics(Graphics orig, float w) {
        return lineGraphics((Graphics2D) orig, w);
    }

    public static Graphics2D lineGraphics(
            Graphics orig, float w, int cap, int join) {
        return lineGraphics((Graphics2D) orig, w, cap, join);
    }

    public static Graphics2D lineGraphics(Graphics2D orig, float w) {
        return lineGraphics(
                orig, w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    public static Graphics2D lineGraphics(
            Graphics2D orig, float w, int cap, int join) {
        Graphics2D lineG = copy(orig);

        Stroke stroke = strokes.get(w);
        if (stroke == null) {
            stroke = new BasicStroke(w, cap, join);
            strokes.put(w, stroke);
        }
        lineG.setStroke(stroke);
        lineG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        lineG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return lineG;
    }

    public static Graphics2D defaultGraphics(Graphics g1, Component c) {
        Graphics2D g = (Graphics2D) g1;
        syncWithTheme(g, c);
        boolean enabled = c.isEnabled();
        if (!enabled && c instanceof JComponent) {
            Rectangle r = g.getClipBounds();
            int w = r.width + CLIP_INSET;
            int h = r.height + CLIP_INSET;
            BufferedImage tmp = erasureBuffer.get();
            if (tmp == null || tmp.getWidth() < w || tmp.getHeight() < h) {
                tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                erasureBuffer = new SoftReference<BufferedImage>(tmp);
            }
            Graphics2D tg = tmp.createGraphics();
            Composite origComp = tg.getComposite();
            tg.setComposite(AlphaComposite.Clear);
            tg.fillRect(0, 0, w, h);
            tg.setComposite(origComp);
            int offX = -r.x + CLIP_OFFSET;
            int offY = -r.y + CLIP_OFFSET;
            tg.translate(offX, offY);

            // copy in values from the original (should be a method to do this)
            tg.setBackground(g.getBackground());
            tg.setClip(g.getClip());
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

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    public static void syncWithTheme(Graphics2D g, Component c) {
        if (isPaper(c)) {
            paperStack.push(c);
            themeStack.push(
                    (NapkinTheme) ((JComponent) c).getClientProperty(THEME_KEY)
            );
            dumpStacks();
        }

        NapkinTheme theme = currentTheme(c);
        Color themePen = theme.getPenColor();

        Color fgColor = ifReplace(c.getForeground(), themePen);
        // explicitly check for equality because two things depend on it
        if (!fgColor.equals(c.getForeground())) {
            c.setForeground(fgColor);
            if (g != null) {
                g.setColor(fgColor);
            }
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
        return themeStack.isEmpty() ?
            (NapkinTheme) themeTopFor(c).getClientProperty(THEME_KEY) :
            themeStack.peek();
    }

    public static Component currentPaper(Component c) {
        return paperStack.isEmpty() ? themeTopFor(c) : paperStack.peek();
    }

    @SuppressWarnings({"ObjectEquality"})
    public static void finishGraphics(Graphics g1, Component c) {
        if (c == currentPaper(c)) {
            if (!paperStack.isEmpty()) {
                paperStack.pop();
            }
            if (!themeStack.isEmpty()) {
                themeStack.pop();
            }
            dumpStacks();
        }

        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            DisabledMark mark = (DisabledMark) jc.getClientProperty(
                    DISABLED_MARK_KEY);
            if (mark == null) {
                Color bgColor = (Color) jc.getClientProperty(DISABLED_BACKGROUND_KEY);
                if (bgColor != null) {
                    jc.putClientProperty(DISABLED_BACKGROUND_KEY, null);
                    jc.setBackground(bgColor);
                }
            } else {
                jc.putClientProperty(DISABLED_MARK_KEY, null);
                Color bgColor = jc.getBackground();
                if (jc.getClientProperty(DISABLED_BACKGROUND_KEY) == null) {
                    jc.putClientProperty(DISABLED_BACKGROUND_KEY,
                            bgColor == null ? CLEAR : bgColor);
                    jc.setBackground(new AlphaColorUIResource(
                            jc.getForeground().getRGB() & 0x00FFFFFF));
                }

                Graphics2D tg = (Graphics2D) g1;
                tg.setComposite(ERASURE_COMPOSITE);
                Point start = getStart(jc, null);
                int w = textureImage.getWidth();
                int h = textureImage.getHeight();
                Rectangle anchor = new Rectangle(w - start.x, h - start.y, w, h);
                tg.setPaint(new TexturePaint(textureImage, anchor));
                tg.fillRect(0, 0, mark.image.getWidth(), mark.image.getHeight());

                mark.graphics.drawImage(mark.image, -mark.offX, -mark.offY, jc);
                tg.dispose();
            }
        }
    }

    private static Border wrapBorder(Border b) {
        if (!(b instanceof NapkinBorder)) {
            if (b instanceof BevelBorder) {
                b = new NapkinBevelBorder((BevelBorder) b);
            } else if (b instanceof EtchedBorder) {
                b = new NapkinEtchedBorder((EtchedBorder) b);
            } else if (b instanceof CompoundBorder) {
                CompoundBorder cb = (CompoundBorder) b;
                Border outside = cb.getOutsideBorder();
                Border inside = cb.getInsideBorder();
                Border newOutside = wrapBorder(outside);
                Border newInside = wrapBorder(inside);
                if (outside != newOutside || inside != newInside) {
                    b = new NapkinCompoundBorder(newOutside, newInside);
                }
            }
        }
        return b;
    }

    static AffineTransform scaleMat(double scale) {
        AffineTransform mat = new AffineTransform();
        mat.scale(scale, scale);
        return mat;
    }

    public static JButton createArrowButton(int pointTowards) {
        int size = NapkinIconFactory.ArrowIcon.DEFAULT_SIZE;
        return createArrowButton(pointTowards, size);
    }

    public static JButton createArrowButton(int pointTowards, int size) {
        Icon arrow = NapkinIconFactory.createArrowIcon(pointTowards, size);
        JButton button = new JButton(arrow);
        button.setBorderPainted(false);
        Dimension dim = new Dimension(size + 3, size + 3);
        button.setPreferredSize(dim);
        button.setMinimumSize(dim);
        button.putClientProperty(NO_ROLL_OVER_KEY, Boolean.TRUE);
        return button;
    }

    public static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    public static DrawnLineHolder paintLine(Graphics g, boolean vertical,
            DrawnLineHolder holder, Rectangle bounds) {
        if (holder == null) {
            holder = new DrawnLineHolder(
                    DrawnCubicLineGenerator.INSTANCE, vertical);
        }
        holder.shapeUpToDate(bounds, null);
        Graphics2D lineG = copy(g);
        if (vertical) {
            lineG.translate(bounds.x + bounds.width / 2, 0);
        } else {
            lineG.translate(0, bounds.y + bounds.height / 2);
        }
        holder.draw(lineG);
        return holder;
    }

    @SuppressWarnings({"SameParameterValue"})
    public static void printPair(Logger logger, Level level, String label,
            double x, double y) {

        logger.log(level, label + ": " + x + ", " + y);
    }

    public static void setupPaper(JComponent c, NapkinKnownTheme theme) {
        c.setOpaque(true);
        NapkinTheme baseTheme = NapkinTheme.Manager.getCurrentTheme();
        c.putClientProperty(THEME_KEY, baseTheme.getTheme(theme));
    }

    public static NapkinTheme paintBackground(Graphics g1, Component c) {
        NapkinTheme theme = null;
        if (!(c instanceof JComponent && isGlassPane((JComponent) c))) {
            Graphics2D g = (Graphics2D) g1;
            theme = currentTheme(c);
            NapkinBackground bg = theme.getPaper();

            Rectangle pRect = bounds(currentPaper(c));
            Rectangle cRect = bounds(c);

            bg.paint(c, g, pRect, cRect, insets(c));

            if (c.isEnabled() && c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                paintHighlights(g, theme, jc);
            }

        }
        return theme;
    }

    private static void paintHighlights(final Graphics2D g,
            final NapkinTheme theme, final JComponent jc) {
        Boolean tempBool = (Boolean) jc.getClientProperty(HIGHLIGHT_KEY);
        boolean shouldHighlight = (jc.getBackground() == HIGHLIGHT_CLEAR)
                || (tempBool != null && tempBool);
        tempBool = (Boolean) jc.getClientProperty(ROLL_OVER_KEY);
        boolean isRolledOver = (tempBool != null && tempBool);
        if (shouldHighlight || isRolledOver) {
            Rectangle rect = g.getClipBounds();
            if (rect.width > 20f) {
                rect.x += NapkinRandom.nextDouble(5d);
                rect.width -= NapkinRandom.nextDouble(10d);
            }
            DrawnLineHolder highLightLine = rect.width > 50f ?
                    highLightLongLine : highLightShortLine;
            highLightLine.setCap(BasicStroke.CAP_BUTT);
            float lineWidth = rect.height;
            if (lineWidth > 10f) {
                lineWidth *= 0.8f;
            }
            if (lineWidth >= 0f) {
                Color fColor = g.getColor();
                if (shouldHighlight && isRolledOver) {
                    lineWidth *= 0.5f;
                    highLightLine.setWidth(lineWidth);
                    rect.y += rect.height * 0.3f;
                    highLightLine.shapeUpToDate(rect, null);
                    g.setColor(theme.getHighlightColor());
                    highLightLine.draw(g);
                    rect.y += rect.height * 0.5f;
                    highLightLine.shapeUpToDate(rect, null);
                    g.setColor(theme.getRollOverColor());
                    highLightLine.draw(g);
                } else {
                    highLightLine.setWidth(lineWidth);
                    rect.y += rect.height * 0.6f;
                    highLightLine.shapeUpToDate(rect, null);
                    g.setColor(isRolledOver ? theme.getRollOverColor()
                            : theme.getHighlightColor());
                    highLightLine.draw(g);
                }
                g.setColor(fColor);
            }
        }
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

    private static Insets insets(Component c) {
        return c instanceof Container ? ((Container) c).getInsets() : NO_INSETS;
    }

    /** @noinspection TailRecursion */
    public static JComponent themeTopFor(Component c) {
        JComponent result = null;
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            if (jc.getClientProperty(THEME_KEY) == null) {
                result = themeTopFor(jc.getParent());
                if (result == null) {
                    // This can happen to any entity without a JComponent ancestors.
                    // If so, we nominate ourselves as the relevant background paper.
                    // Unfortunately this is common: JFrame et al are not JComponents
                    // and have no UI classes.  So this is what you get for any regular
                    // top-level window we haven't overridden.  I wonder why JFrame and
                    // friends are like this.
                    setupPaper(jc, NapkinKnownTheme.BASIC_THEME);
                    result = jc;
                }
            } else {
                result = jc;
            }
        } else if (c != null) {
            result = themeTopFor(c.getParent());
        }
        return result;
    }

    @SuppressWarnings({"ObjectEquality"})
    private static Point getStart(Component c, Insets insets) {
        Point start = new Point();
        if (insets != null) {
            start.setLocation(-insets.left, -insets.top);
        }
        Component paper = currentPaper(c);
        while (c != null && c != paper) {
            start.x += c.getX();
            start.y += c.getY();
            c = c.getParent();
        }
        return start;
    }

    private static boolean isPaper(Component c) {
        return c instanceof JComponent ?
            ((JComponent) c).getClientProperty(THEME_KEY) != null : false;
    }

    @SuppressWarnings({"TooBroadScope"})
    public static void
            paintButtonText(Graphics g, JComponent c, Rectangle textRect,
            String text, int textOffset, DrawnLineHolder line,
            boolean isDefault, NapkinTextPainter helper) {

        Graphics2D ulG;
        if (isDefault) {
            if (line == null) {
                line = new DrawnLineHolder(new DrawnCubicLineGenerator());
            }
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
            if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
                textColor = currentTheme(c).getSelectionColor();
            }
        }
        Color oldColor = g.getColor();
        g.setColor(textColor);
        helper.superPaintText(g, c, textRect, text);
        g.setColor(oldColor);
    }

    @SuppressWarnings({"SameParameterValue"})
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
        return current == null ?
            true : !current.equals(candidate) && current instanceof UIResource;
    }

    public static Color ifReplace(Color current, Color candidate) {
        return (replace(current, candidate) ? candidate : current);
    }

    public static void drawStroke(GeneralPath path, AffineTransform matrix,
            double x1, double y1, double x2, double y2,
            double baseAngle, AbstractDrawnGenerator lineGen) {
        if (matrix == null) {
            matrix = new AffineTransform();
        }

        double xDelta = x1 - x2;
        double yDelta = y1 - y2;
        double angle = Math.atan2(xDelta, yDelta);
        AffineTransform mat = (AffineTransform) matrix.clone();
        mat.translate(x1, y1);
        mat.rotate(baseAngle + angle);
        double len = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
        mat.scale(len / LENGTH, 1);
        AbstractDrawnGenerator.addLine(path, mat, lineGen);
    }

    public static void update(Graphics g, JComponent c, NapkinPainter painter) {
        if ((c instanceof JButton || c instanceof JLabel) &&
                !Boolean.TRUE.equals(c.getClientProperty(REVALIDATE_KEY))) {
            c.putClientProperty(REVALIDATE_KEY, true);
            c.revalidate();
        }
        if (c instanceof AbstractButton) {
            if (!Boolean.TRUE.equals(c.getClientProperty(NO_ROLL_OVER_KEY))) {
                AbstractButton button = (AbstractButton) c;
                ButtonModel model = button.getModel();
                button.putClientProperty(ROLL_OVER_KEY,
                        button.isRolloverEnabled() && model.isRollover());
            }
        }
        g = defaultGraphics(g, c);
        NapkinTheme theme = paintBackground(g, c);
        MergedFontGraphics2D mfg = MergedFontGraphics2D.wrap((Graphics2D) g);
        painter.superPaint(mfg, c, theme);
        mfg.dispose();
        finishGraphics(g, c);
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "HardcodedFileSeparator"})
    private static void dumpStacks() {
        if (Logs.paper.isLoggable(Level.FINER)) {
            if (themeStack.size() != paperStack.size()) {
                System.out.println("!!!");
            }
            StringBuilder dump = new StringBuilder(NapkinDebug.count).append(":\t");
            NapkinDebug.count++;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < paperStack.size(); i++) {
                dump.append(". ");
            }
            if (!themeStack.isEmpty()) {
                dump.append(themeStack.peek()).append(" / ").append(
                        NapkinDebug.descFor(paperStack.peek()));
            }
            Logs.paper.log(Level.FINER, dump.toString());
        }
    }

    @SuppressWarnings({"UnusedReturnValue"})
    public static IOException tryClose(Closeable fonts) {
        IOException result = null;
        try {
            fonts.close();
        } catch (IOException e) {
            result = e;
        }
        return result;
    }
}
