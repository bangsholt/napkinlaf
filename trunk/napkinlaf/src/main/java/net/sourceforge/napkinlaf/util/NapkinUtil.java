package net.sourceforge.napkinlaf.util;

import net.sourceforge.napkinlaf.NapkinKnownTheme;
import net.sourceforge.napkinlaf.NapkinLookAndFeel;
import net.sourceforge.napkinlaf.NapkinTheme;
import net.sourceforge.napkinlaf.borders.NapkinBorder;
import net.sourceforge.napkinlaf.borders.NapkinBoxBorder;
import net.sourceforge.napkinlaf.borders.NapkinCompoundBorder;
import net.sourceforge.napkinlaf.borders.NapkinWrappedBorder;
import net.sourceforge.napkinlaf.fonts.MergedFontGraphics2D;
import net.sourceforge.napkinlaf.shapes.AbstractDrawnGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import static net.sourceforge.napkinlaf.util.NapkinConstants.*;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.BackgroundListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.BorderListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.ButtonIconListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.DisabledIconListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.DisabledSelectedIconListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.OpaqueListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.PressedIconListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.RolloverIconListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.RolloverListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.RolloverSelectedIconListener;
import net.sourceforge.napkinlaf.util.NapkinSmartListeners.SelectedIconListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"WeakerAccess"})
public class NapkinUtil {
    private static final Map<Float, Stroke> strokes =
            new WeakHashMap<Float, Stroke>();

    private static final BufferedImage textureImage;

    private static final float FOCUS_MARK_WIDTH = 1.5f;

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);
    private static final int CLIP_OFFSET = 10;
    private static final int CLIP_INSET = CLIP_OFFSET * 2;
    private static final NapkinBorder NAPKIN_NULL_BORDER =
            new NapkinWrappedBorder(new EmptyBorder(NO_INSETS));

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
    private static final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);

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

    @SuppressWarnings({"RedundantSuppression"})
    public static class DisabledMark {
        /** @noinspection PublicField */
        public final BufferedImage image;
        public final int offX;
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

    public static Object property(ComponentUI ui, String prop) {
        String name = ui.getClass().getName();
        String base = ".Napkin";
        int pos = name.lastIndexOf(base) + base.length();
        String pref = name.substring(pos, name.length() - 2);
        return pref + "." + prop;
    }

    static boolean replaceBackground(Color bgColor) {
        return bgColor == null || (!(bgColor instanceof AlphaColorUIResource) &&
                bgColor.getRed() == bgColor.getGreen() &&
                bgColor.getGreen() == bgColor.getBlue());
    }

    @SuppressWarnings({"ObjectEquality"})
    static boolean isTranparent(Color bgColor) {
        return bgColor == CLEAR || bgColor == HIGHLIGHT_CLEAR;
    }

    @SuppressWarnings({"UnusedCatchParameter", "ObjectEquality"})
    public static void installUI(JComponent c) {
        // prevents double installing
        if (c.getClientProperty(INSTALL_KEY) != Boolean.TRUE) {
            // mark component as installed
            c.putClientProperty(INSTALL_KEY, Boolean.TRUE);
            NapkinLookAndFeel.registerComponent(c);
            // opaqueness override
            if (c.isOpaque()) {
                c.putClientProperty(OPAQUE_KEY, Boolean.TRUE);
                c.setOpaque(false);
            }
            SmartStickyListener.hookListener(c, new OpaqueListener());
            // AbstractButton-specific overrides
            if (c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                // rollover-enabled override
                c.putClientProperty(ROLLOVER_ENABLED,
                        button.isRolloverEnabled());
                button.setRolloverEnabled(true);
                SmartStickyListener.hookListener(c, new RolloverListener());
                // button icon override
                Icon icon = button.getIcon();
                SmartStickyListener<Icon> listener = new ButtonIconListener();
                if (listener.shouldRecord(icon)) {
                    c.putClientProperty(BUTTON_ICON_KEY, icon);
                }
                listener.overrideValue(c, icon);
                SmartStickyListener.hookListener(c, listener);
                // pressed icon override
                icon = button.getPressedIcon();
                listener = new PressedIconListener();
                if (listener.shouldRecord(icon)) {
                    c.putClientProperty(PRESSED_ICON_KEY, icon);
                }
                listener.overrideValue(c, icon);
                SmartStickyListener.hookListener(c, listener);
                // selected icon override
                icon = button.getSelectedIcon();
                listener = new SelectedIconListener();
                if (listener.shouldRecord(icon)) {
                    c.putClientProperty(SELECTED_ICON_KEY, icon);
                }
                listener.overrideValue(c, icon);
                SmartStickyListener.hookListener(c, listener);
                // rollover icon override
                icon = button.getRolloverIcon();
                listener = new RolloverIconListener();
                if (listener.shouldRecord(icon)) {
                    c.putClientProperty(ROLLOVER_ICON_KEY, icon);
                }
                listener.overrideValue(c, icon);
                SmartStickyListener.hookListener(c, listener);
                // rollover selected icon override
                icon = button.getRolloverSelectedIcon();
                listener = new RolloverSelectedIconListener();
                if (listener.shouldRecord(icon)) {
                    c.putClientProperty(ROLLOVER_SELECTED_ICON_KEY, icon);
                }
                listener.overrideValue(c, icon);
                SmartStickyListener.hookListener(c, listener);
                // disabled icon override
                icon = button.getDisabledIcon();
                listener = new DisabledIconListener();
                if (listener.shouldRecord(icon)) {
                    c.putClientProperty(DISABLED_ICON_KEY, icon);
                }
                listener.overrideValue(c, icon);
                SmartStickyListener.hookListener(c, listener);
                // disabled selected icon override
                icon = button.getDisabledSelectedIcon();
                listener = new DisabledSelectedIconListener();
                if (listener.shouldRecord(icon)) {
                    c.putClientProperty(DISABLED_SELECTED_ICON_KEY, icon);
                }
                listener.overrideValue(c, icon);
                SmartStickyListener.hookListener(c, listener);
            }
            // background colour override
            Color bgColor = c.getBackground();
            c.putClientProperty(BACKGROUND_KEY, bgColor);
            if (replaceBackground(bgColor)) {
                c.setBackground(CLEAR);
            }
            SmartStickyListener.hookListener(c, new BackgroundListener());
            // border override
            Border b = c.getBorder();
            Border nb = wrapBorder(b);
            /**
             * If our component gets a NapkinBorder already from UIDefaults,
             * then we won't be modifying it; in fact, we won't register it on
             * the restore key so that the NapkinBorder will be removed upon
             * uninstallUI().
             */
            if (nb != b) {
                c.putClientProperty(BORDER_KEY, b);
                try {
                    c.setBorder(nb);
                } catch (Exception ex) {
                    // setBorder() not supported; do nothing
                }
            }
            SmartStickyListener.hookListener(c, new BorderListener());
        }
    }

    @SuppressWarnings({"ObjectEquality"})
    public static void uninstallUI(JComponent c) {
        // prevents double uninstalling
        if (c.getClientProperty(INSTALL_KEY) == Boolean.TRUE) {
            // remove install mark
            c.putClientProperty(INSTALL_KEY, null);
            // unhook all the smart listeners
            SmartStickyListener.unhookListeners(c);
            // restore from border override
            Border border = (Border) c.getClientProperty(BORDER_KEY);
            if (border != c.getBorder()) {
                // if setBorder() is not supported, we won't be here
                c.setBorder(border);
            }
            // restore from background colour override
            c.setBackground((Color) c.getClientProperty(BACKGROUND_KEY));
            // AbstractButton-specific overrides
            if (c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                // restore from disabled selected icon override
                button.setDisabledSelectedIcon((Icon) c.getClientProperty(
                        DISABLED_SELECTED_ICON_KEY));
                // restore from disabled icon override
                button.setDisabledIcon((Icon) c.getClientProperty(
                        DISABLED_ICON_KEY));
                // restore from rollover selected icon override
                button.setRolloverSelectedIcon((Icon) c.getClientProperty(
                        ROLLOVER_SELECTED_ICON_KEY));
                // restore from rollover icon override
                button.setRolloverIcon((Icon) c.getClientProperty(
                        ROLLOVER_ICON_KEY));
                // restore from selected icon override
                button.setSelectedIcon((Icon) c.getClientProperty(
                        SELECTED_ICON_KEY));
                // restore from pressed icon override
                button.setPressedIcon((Icon) c.getClientProperty(
                        PRESSED_ICON_KEY));
                // restore from button icon override
                button.setIcon((Icon) c.getClientProperty(BUTTON_ICON_KEY));
                // restore from rollover-enabled override
                button.setRolloverEnabled((Boolean) c.getClientProperty(
                        ROLLOVER_ENABLED));
            }
            // restore from opaqueness override
            if (shouldMakeOpaque(c)) {
                c.setOpaque(true);
            }
            // remove Napkin-specific client properties (+ install mark)
            for (String clientProp : CLIENT_PROPERTIES) {
                c.putClientProperty(clientProp, null);
            }
        }
    }

    @SuppressWarnings({"ObjectEquality"})
    private static boolean shouldMakeOpaque(JComponent c) {
        return !isGlassPane(c) && !c.isOpaque() && c.getClientProperty(
                OPAQUE_KEY) == Boolean.TRUE;
    }

    @SuppressWarnings({"ObjectEquality"})
    public static boolean isOpaque(Component c) {
        String reason = "default";
        boolean ret = true;
        if (c.isOpaque()) {
            reason = "isOpaque()";
            ret = true;
        } else if (c instanceof JComponent) {
            reason = "OPAQUE_KEY prop";
            JComponent jc = (JComponent) c;
            ret = jc.getClientProperty(OPAQUE_KEY) == Boolean.TRUE;
        }
//        System.out.println("opaque " + ret + ": " + NapkinDebug.descFor(c) +
//                " {" + reason + "}");
        return ret;
    }

    @SuppressWarnings({"ObjectEquality"})
    private static boolean isGlassPane(JComponent c) {
        JRootPane rootPane = c.getRootPane();
        return (rootPane != null && rootPane.getGlassPane() == c);
    }

    public static double leftRight(double x, boolean left) {
        return (left ? x : BASE_LINE_LENGTH - x);
    }

    public static Graphics2D copy(Graphics g) {
        return (Graphics2D) g.create();
    }

    public static Graphics2D lineGraphics(Graphics orig, float w) {
        return lineGraphics((Graphics2D) orig, w);
    }

    public static Graphics2D lineGraphics(Graphics orig, float w, int cap,
            int join) {
        return lineGraphics((Graphics2D) orig, w, cap, join);
    }

    public static Graphics2D lineGraphics(Graphics2D orig, float w) {
        return lineGraphics(orig, w, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
    }

    public static Graphics2D lineGraphics(Graphics2D orig, float w, int cap,
            int join) {
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

    private static boolean canBeDisabled(Component c) {
        return !(c instanceof JTextComponent);
    }

    public static Graphics2D defaultGraphics(Graphics g1, Component c) {
        Graphics2D g = (Graphics2D) g1;
        syncWithTheme(g, c);
        boolean enabled = c.isEnabled();
        if (!enabled && canBeDisabled(c)) {
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
            themeStack.push((NapkinTheme) ((JComponent) c).getClientProperty(
                    THEME_KEY));
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
        return themeStack.isEmpty() ? (NapkinTheme) themeTopFor(c)
                .getClientProperty(THEME_KEY) : themeStack.peek();
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
                Color bgColor = (Color) jc.getClientProperty(
                        DISABLED_BACKGROUND_KEY);
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
                Rectangle anchor = new Rectangle(w - start.x, h - start.y, w, h)
                        ;
                tg.setPaint(new TexturePaint(textureImage, anchor));
                tg.fillRect(0, 0, mark.image.getWidth(),
                        mark.image.getHeight());

                mark.graphics.drawImage(mark.image, -mark.offX, -mark.offY, jc);
                tg.dispose();
            }
        }
    }

    @SuppressWarnings({"ObjectEquality"})
    static Border wrapBorder(Border b) {
        if (!(b instanceof NapkinBorder)) {
            if (b instanceof BevelBorder) {
                b = new NapkinBoxBorder();
            } else if (b instanceof EtchedBorder) {
                b = new NapkinBoxBorder();
            } else if (b instanceof CompoundBorder) {
                CompoundBorder cb = (CompoundBorder) b;
                Border outside = cb.getOutsideBorder();
                Border inside = cb.getInsideBorder();
                /**
                 * In the case when one of the member Border is a NapkinBorder,
                 * we will just wrap the CompoundBorder as is.
                 *
                 * This is due to components like JButton somehow have a special
                 * CompoundBorder to wrap its original Border, which if we
                 * replace it with a NapkinCompoundBorder things will cease to
                 * work, that is, another border will be painted on top of the
                 * "erased" JButton when the button is disabled.
                 */
                if (inside instanceof NapkinBorder ||
                        outside instanceof NapkinBorder) {

                    b = new NapkinWrappedBorder(b);
                } else {
                    Border newOutside = wrapBorder(outside);
                    Border newInside = wrapBorder(inside);
                    b = (outside == newOutside && inside == newInside) ?
                            new NapkinWrappedBorder(b) :
                            new NapkinCompoundBorder(outside, inside);
                }
            } else {
                b = (b == null) ? NAPKIN_NULL_BORDER : new NapkinWrappedBorder(
                        b);
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
        button.putClientProperty(NO_ROLLOVER_KEY, Boolean.TRUE);
        return button;
    }

    public static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    public static DrawnLineHolder paintLine(Graphics g, boolean vertical,
            DrawnLineHolder holder, Rectangle bounds) {
        if (holder == null) {
            holder = new DrawnLineHolder(DrawnCubicLineGenerator.INSTANCE,
                    vertical);
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

    public static void printPair(Logger logger, Level level, String label,
            double x, double y) {

        logger.log(level, label + ": " + x + ", " + y);
    }

    public static void setupPaper(JComponent c, NapkinKnownTheme theme) {
        c.setOpaque(true);
        NapkinTheme baseTheme = NapkinTheme.Manager.getCurrentTheme();
        c.putClientProperty(THEME_KEY, baseTheme.getTheme(theme));
    }

    public static void paintBackground(Graphics g1, Component c, Rectangle clip) {
        if (!isOpaque(c))
            return;

        if (c instanceof JComponent && isGlassPane((JComponent) c)) {
            return;
        }

        Graphics2D g = (Graphics2D) g1;
        NapkinTheme theme = currentTheme(c);
        NapkinBackground bg = theme.getPaper();

        Rectangle pRect = bounds(currentPaper(c));
        Rectangle cRect = (clip != null ? clip : bounds(c));

        bg.paint(c, g, pRect, cRect, (clip != null ? ZERO_INSETS : insets(c)));

        if (c.isEnabled() && c instanceof JComponent) {
            paintHighlights(g, theme, (JComponent) c);
        }
    }

    @SuppressWarnings({"ObjectEquality"})
    private static void paintHighlights(Graphics2D g, NapkinTheme theme,
            JComponent jc) {

        boolean shouldHighlight =
                (jc.getBackground() == HIGHLIGHT_CLEAR) || getBooleanProprty(jc,
                        HIGHLIGHT_KEY);
        boolean isRolledOver = getBooleanProprty(jc, ROLLOVER_KEY);

        if (shouldHighlight || isRolledOver) {
            Rectangle rect = g.getClipBounds();
            if (rect.width > 20.0f) {
                rect.x += NapkinRandom.nextDouble(5.0);
                rect.width -= NapkinRandom.nextDouble(10.0);
            }
            DrawnLineHolder highLightLine =
                    rect.width > 50 ? highLightLongLine : highLightShortLine;
            highLightLine.setCap(BasicStroke.CAP_BUTT);
            //noinspection SuspiciousNameCombination
            float lineWidth = rect.height;
            if (lineWidth > 10.0f) {
                lineWidth *= 0.8f;
            }
            if (lineWidth >= 0.0f) {
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
                    g.setColor(theme.getRolloverColor());
                    highLightLine.draw(g);
                } else {
                    highLightLine.setWidth(lineWidth);
                    rect.y += rect.height * 0.6f;
                    highLightLine.shapeUpToDate(rect, null);
                    g.setColor(isRolledOver ?
                            theme.getRolloverColor() :
                            theme.getHighlightColor());
                    highLightLine.draw(g);
                }
                g.setColor(fColor);
            }
        }
    }

    private static boolean getBooleanProprty(JComponent jc, String key) {
        Boolean prop = (Boolean) jc.getClientProperty(key);
        return (prop != null && prop);
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
        return c instanceof JComponent && ((JComponent) c).getClientProperty(
                THEME_KEY) != null;
    }

    @SuppressWarnings({"TooBroadScope"})
    public static void paintButtonText(Graphics g, JComponent c,
            Rectangle textRect, String text, int textOffset,
            DrawnLineHolder line, boolean isDefault, NapkinTextPainter helper) {

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

    public static Object getProperty(JComponent c, String key,
            PropertyFactory factory) {
        Object value = c.getClientProperty(key);
        if (value == null) {
            value = factory.createPropertyValue();
            c.putClientProperty(key, value);
        }
        return value;
    }

    public static boolean replace(Object current, Object candidate) {
        return current == null || !current.equals(candidate) &&
                current instanceof UIResource;
    }

    public static Color ifReplace(Color current, Color candidate) {
        return (replace(current, candidate) ? candidate : current);
    }

    public static void drawStroke(GeneralPath path, AffineTransform matrix,
            double x1, double y1, double x2, double y2, double baseAngle,
            AbstractDrawnGenerator lineGen) {
        if (matrix == null) {
            matrix = new AffineTransform();
        }

        double xDelta = x1 - x2;
        double yDelta = y1 - y2;
        //noinspection SuspiciousNameCombination
        double angle = Math.atan2(xDelta, yDelta);
        AffineTransform mat = (AffineTransform) matrix.clone();
        mat.translate(x1, y1);
        mat.rotate(baseAngle + angle);
        double len = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
        mat.scale(len / BASE_LINE_LENGTH, 1);
        AbstractDrawnGenerator.addLine(path, mat, lineGen);
    }

    public static void update(Graphics g, JComponent c, NapkinPainter painter) {
        if ((c instanceof JButton || c instanceof JLabel) &&
                !Boolean.TRUE.equals(c.getClientProperty(REVALIDATE_KEY))) {
            c.putClientProperty(REVALIDATE_KEY, true);
            c.revalidate();
        }
        if (c instanceof AbstractButton) {
            if (!Boolean.TRUE.equals(c.getClientProperty(NO_ROLLOVER_KEY))) {
                AbstractButton button = (AbstractButton) c;
                ButtonModel model = button.getModel();
                button.putClientProperty(ROLLOVER_KEY,
                        button.isRolloverEnabled() && model.isRollover());
            }
        }
        g = defaultGraphics(g, c);
        paintBackground(g, c, null);
        MergedFontGraphics2D mfg = MergedFontGraphics2D.wrap((Graphics2D) g);
        painter.superPaint(mfg, c);
        mfg.dispose();
        finishGraphics(g, c);
    }

    @SuppressWarnings({"ObjectEquality"})
    public static boolean isNapkinInstalled(Component c) {
        return c instanceof JComponent && ((JComponent) c).getClientProperty(
                INSTALL_KEY) == Boolean.TRUE;
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr", "HardcodedFileSeparator"})
    private static void dumpStacks() {
        if (Logs.paper.isLoggable(Level.FINER)) {
            if (themeStack.size() != paperStack.size()) {
                System.out.println("!!!");
            }
            StringBuilder dump = new StringBuilder(NapkinDebug.count).append(
                    ":\t");
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
