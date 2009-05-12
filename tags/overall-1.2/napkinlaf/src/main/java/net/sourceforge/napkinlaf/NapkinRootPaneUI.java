package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import static java.awt.Frame.*;
import static java.awt.event.InputEvent.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "JavaDoc"})
public class NapkinRootPaneUI extends BasicRootPaneUI implements NapkinPainter {

    /** Window the <tt>JRootPane</tt> is in. */
    private Window window;

    /**
     * <tt>JComponent</tt> providing window decorations. This will be
     * <tt>null</tt> if not providing window decorations.
     */
    private JComponent titlePane;

    /**
     * <tt>MouseInputListener</tt> that is added to the parent window the root
     * pane is contained in.
     */
    private MouseInputListener mouseInputListener;

    /** The layout manager that is set on the root pane. */
    private LayoutManager layoutManager;

    /** The layout manager of the <tt>JRootPane</tt> before we replaced it. */
    private LayoutManager savedOldLayout;

    /** The <tt>JRootPane</tt> we're providing the look and feel for. */
    private JRootPane root;

    /**
     * The cursor used to track the cursor set by the user. This is initially
     * {@link Cursor#DEFAULT_CURSOR}.
     */
    private Cursor lastCursor = Cursor.getPredefinedCursor(
            Cursor.DEFAULT_CURSOR);

    /** Keys to lookup borders in defaults table. */
    private static final String[] BORDER_KEYS =
            {null, "RootPane.frameBorder", "RootPane.plainDialogBorder",
                    "RootPane.informationDialogBorder",
                    "RootPane.errorDialogBorder",
                    "RootPane.colorChooserDialogBorder",
                    "RootPane.fileChooserDialogBorder",
                    "RootPane.questionDialogBorder",
                    "RootPane.warningDialogBorder"};

    /** The amount of space (in pixels) that the cursor is changed on. */
    private static final int CORNER_DRAG_WIDTH = 16;

    /** Region from edges that dragging is active from. */
    private static final int BORDER_DRAG_THICKNESS = 5;

    /**
     * Creates a UI for a <tt>JRootPane</tt>.
     *
     * @param c The JRootPane the RootPaneUI will be created for.
     *
     * @return The <tt>RootPaneUI</tt> implementation for the passed-in
     *         <tt>JRootPane</tt>.
     */
    @SuppressWarnings({"UnusedDeclaration", "TypeMayBeWeakened"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinRootPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        root = (JRootPane) c;
        if (root.getWindowDecorationStyle() != JRootPane.NONE) {
            installClientDecorations(root);
        }
        NapkinUtil.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
        uninstallClientDecorations(root);
    }

    static void installBorder(JRootPane rootPane) {
        int style = rootPane.getWindowDecorationStyle();
        if (style != JRootPane.NONE) {
            rootPane.setBorder((Border) UIManager.getDefaults().get(
                    BORDER_KEYS[style]));
        }
    }

    /** Removes any border that may have been installed. */
    private static void uninstallBorder(JComponent rootPane) {
        LookAndFeel.uninstallBorder(rootPane);
    }

    /**
     * Installs the necessary listeners on the parent window, if there is one.
     * This takes the parent so that cleanup can be done from
     * <tt>removeNotify</tt>, at which point the parent hasn't been reset yet.
     *
     * @param parent The parent of the <tt>JRootPane</tt>.
     */
    private void installWindowListeners(Component parent) {
        window = parent instanceof Window ?
                (Window) parent :
                SwingUtilities.getWindowAncestor(parent);
        if (window != null) {
            if (mouseInputListener == null) {
                mouseInputListener = createWindowMouseInputListener();
            }
            window.addMouseListener(mouseInputListener);
            window.addMouseMotionListener(mouseInputListener);
        }
    }

    /**
     * Uninstalls the necessary Listeners on the window that the Listeners were
     * last installed on.
     */
    private void uninstallWindowListeners() {
        if (window != null) {
            window.removeMouseListener(mouseInputListener);
            window.removeMouseMotionListener(mouseInputListener);
        }
    }

    /**
     * Installs the appropriate LayoutManager on the <tt>JRootPane</tt> to
     * render the window decorations.
     */
    private void installLayout(Container rootPane) {
        if (layoutManager == null) {
            layoutManager = createLayoutManager();
        }
        savedOldLayout = rootPane.getLayout();
        rootPane.setLayout(layoutManager);
    }

    /** Uninstalls the previously installed <tt>LayoutManager</tt>. */
    private void uninstallLayout(Container rootPane) {
        if (savedOldLayout != null) {
            rootPane.setLayout(savedOldLayout);
        }
    }

    /**
     * Installs the necessary state onto the root pane to render client
     * decorations. This is <em>only</em> invoked if the root pane has a
     * decoration style other than <tt>JRootPane.NONE</tt>.
     */
    private void installClientDecorations(JRootPane rootPane) {
        installBorder(rootPane);

        setTitlePane(rootPane, createTitlePane(rootPane));
        installWindowListeners(rootPane.getParent());
        installLayout(rootPane);
        if (window != null) {
            rootPane.revalidate();
            rootPane.repaint();
        }
    }

    /**
     * Uninstalls any state that {@link #installClientDecorations(JRootPane)}
     * has installed.
     * <p/>
     * <b>NOTE:</b> This might be called even if you haven't installed client
     * decorations yet (that is, before {@link #installClientDecorations(JRootPane)}
     * has been invoked).
     */
    private void uninstallClientDecorations(JRootPane rootPane) {
        uninstallBorder(rootPane);
        uninstallWindowListeners();
        setTitlePane(rootPane, null);
        uninstallLayout(rootPane);
        // We have to revalidate/repaint root if the style is JRootPane.NONE
        // only. When we need to call revalidate/repaint with other styles
        // the installClientDecorations is always called after this method
        // imediatly and it will cause the revalidate/repaint at the proper
        // time.
        int style = rootPane.getWindowDecorationStyle();
        if (style == JRootPane.NONE) {
            rootPane.repaint();
            rootPane.revalidate();
        }
        // Reset the cursor, as we may have changed it to a resize cursor
        if (window != null) {
            window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        window = null;
    }

    /** Returns the <tt>JComponent</tt> to render the window decoration style. */
    private static JComponent createTitlePane(JRootPane rootPane) {
        return new NapkinTitlePane(rootPane);
    }

    /**
     * Returns a <tt>MouseListener</tt> that will be added to the window
     * containing the <tt>JRootPane</tt>.
     */
    private MouseInputListener createWindowMouseInputListener() {
        return new MouseInputHandler();
    }

    /**
     * Returns a <tt>LayoutManager</tt> that will be set on the
     * <tt>JRootPane</tt>.
     */
    private static LayoutManager createLayoutManager() {
        return new NapkinRootLayout();
    }

    /**
     * Sets the window title pane &mdash; the <tt>JComponent</tt> used to
     * provide a plaf a way to override the native operating system's window
     * title pane with one whose look and feel are controlled by the plaf.  The
     * plaf creates and sets this value; the default is <tt>null</tt>, implying
     * a native operating system window title pane.
     *
     * @param root      The root pane to operate on.
     * @param titlePane The <tt>JComponent</tt> to use for the window title
     *                  pane.
     */
    private void setTitlePane(JRootPane root, JComponent titlePane) {
        JLayeredPane layeredPane = root.getLayeredPane();
        JComponent oldTitlePane = getTitlePane();

        if (oldTitlePane != null) {
            oldTitlePane.setVisible(false);
            layeredPane.remove(oldTitlePane);
        }
        if (titlePane != null) {
            layeredPane.add(titlePane, JLayeredPane.FRAME_CONTENT_LAYER);
            titlePane.setVisible(true);
        }
        this.titlePane = titlePane;
    }

    /**
     * Returns the <tt>JComponent</tt> rendering the title pane. If this returns
     * <tt>null</tt>, it implies there is no need to render window decorations.
     *
     * @return The current window title pane, or <tt>null</tt>.
     *
     * @see #setTitlePane
     */
    private JComponent getTitlePane() {
        return titlePane;
    }

    /** Returns the <tt>JRootPane</tt> we're providing the look and feel for. */
    private JRootPane getRootPane() {
        return root;
    }

    /**
     * Invoked when a property changes. We are primarily interested in events
     * originating from the root pane this has been installed on, identifying
     * the property <tt>windowDecorationStyle</tt>. If the
     * <tt>windowDecorationStyle</tt> has changed to a value other than {@link
     * JRootPane#NONE}, this will add a component to the <tt>JRootPane</tt> to
     * render the window decorations, as well as installing a border on the root
     * pane. On the other hand, if the <tt>windowDecorationStyle</tt> has
     * changed to {@link JRootPane#NONE}, this will remove the component that
     * has been added to the root pane as well resetting the Border to what it
     * was before {@link #installUI(JComponent)} was invoked.
     *
     * @param ev A <tt>PropertyChangeEvent</tt> object describing the event
     *           source and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent ev) {
        super.propertyChange(ev);

        String propertyName = ev.getPropertyName();
        if (propertyName != null) {
            if (propertyName.equals("windowDecorationStyle")) {
                JRootPane pane = (JRootPane) ev.getSource();
                int style = pane.getWindowDecorationStyle();

                // This is potentially more than needs to be done,
                // but it rarely happens and makes the install/uninstall process
                // simpler. NapkinTitlePane also assumes it will be recreated if
                // the decoration style changes.
                uninstallClientDecorations(pane);
                if (style != JRootPane.NONE) {
                    installClientDecorations(pane);
                }
            } else if (propertyName.equals("ancestor")) {
                uninstallWindowListeners();
                if (((JRootPane) ev.getSource()).getWindowDecorationStyle() !=
                        JRootPane.NONE) {

                    installWindowListeners(root.getParent());
                }
            }
        }
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }

    /**
     * A custom layout manager that is responsible for the layout of
     * layeredPane, glassPane, menuBar and titlePane, if one has been
     * installed.
     */
    private static class NapkinRootLayout implements LayoutManager2 {
        // NOTE: Ideally this would extends JRootPane.RootLayout, but that
        //       would force this to be non-static.

        interface SizeAccessor {
            Dimension size(Component c);
        }

        /**
         * Returns the amount of space the layout would like to have.
         *
         * @param parent The container for which this layout manager is being
         *               used.
         *
         * @return A <tt>Dimension</tt> object containing the layout's preferred
         *         size.
         */
        public Dimension preferredLayoutSize(Container parent) {
            return layoutSize(parent, new SizeAccessor() {
                public Dimension size(Component c) {
                    return c.getPreferredSize();
                }
            });
        }

        /**
         * Returns the minimum amount of space the layout needs.
         *
         * @param parent The container for which this layout manager is being
         *               used.
         *
         * @return A <tt>Dimension</tt> object containing the layout's minimum
         *         size.
         */
        public Dimension minimumLayoutSize(Container parent) {
            return layoutSize(parent, new SizeAccessor() {
                public Dimension size(Component c) {
                    return c.getMinimumSize();
                }
            });
        }

        /**
         * Returns the minimum amount of space the layout needs.
         *
         * @param parent The container for which this layout manager is being
         *               used.
         *
         * @return A <tt>Dimension</tt> object containing the layout's minimum
         *         size.
         */
        private static Dimension layoutSize(Container parent,
                SizeAccessor sizer) {
            Dimension cpd;
            Insets i = parent.getInsets();
            JRootPane root = (JRootPane) parent;

            if (root.getContentPane() == null) {
                cpd = root.getSize();
            } else {
                cpd = sizer.size(root.getContentPane());
            }
            int cpWidth = 0;
            int cpHeight = 0;
            if (cpd != null) {
                cpWidth = cpd.width;
                cpHeight = cpd.height;
            }

            int mbWidth = 0;
            int mbHeight = 0;
            if (root.getJMenuBar() != null) {
                Dimension mbd = sizer.size(root.getJMenuBar());
                if (mbd != null) {
                    mbWidth = mbd.width;
                    mbHeight = mbd.height;
                }
            }

            int tpWidth = 0;
            if (root.getWindowDecorationStyle() != JRootPane.NONE &&
                    root.getUI() instanceof NapkinRootPaneUI) {
                JComponent titlePane =
                        ((NapkinRootPaneUI) root.getUI()).getTitlePane();
                if (titlePane != null) {
                    Dimension tpd = sizer.size(titlePane);
                    if (tpd != null) {
                        tpWidth = tpd.width;
                    }
                }
            }

            int maxWidth = Math.max(Math.max(cpWidth, mbWidth), tpWidth);
            int width = maxWidth + i.left + i.right;
            int height = cpHeight + mbHeight + tpWidth + i.top + i.bottom;

            return new Dimension(width, height);
        }

        /**
         * Returns the maximum amount of space the layout can use.
         *
         * @param target The container for which this layout manager is being
         *               used.
         *
         * @return A <tt>Dimension</tt> object containing the layout's maximum
         *         size.
         */
        public Dimension maximumLayoutSize(Container target) {
            int cpWidth = Integer.MAX_VALUE;
            int cpHeight = Integer.MAX_VALUE;
            Insets i = target.getInsets();
            JRootPane root = (JRootPane) target;

            if (root.getContentPane() != null) {
                Dimension cpd = root.getContentPane().getMaximumSize();
                if (cpd != null) {
                    cpWidth = cpd.width;
                    cpHeight = cpd.height;
                }
            }

            int mbWidth = Integer.MAX_VALUE;
            int mbHeight = Integer.MAX_VALUE;
            if (root.getJMenuBar() != null) {
                Dimension mbd = root.getJMenuBar().getMaximumSize();
                if (mbd != null) {
                    mbWidth = mbd.width;
                    mbHeight = mbd.height;
                }
            }

            int tpWidth = Integer.MAX_VALUE;
            int tpHeight = Integer.MAX_VALUE;
            if (root.getWindowDecorationStyle() != JRootPane.NONE &&
                    root.getUI() instanceof NapkinRootPaneUI) {
                JComponent titlePane =
                        ((NapkinRootPaneUI) root.getUI()).getTitlePane();
                if (titlePane != null) {
                    Dimension tpd = titlePane.getMaximumSize();
                    if (tpd != null) {
                        tpWidth = tpd.width;
                        tpHeight = tpd.height;
                    }
                }
            }

            int maxHeight = Math.max(Math.max(cpHeight, mbHeight), tpHeight);
            // Only overflows if 3 real non-MAX_VALUE heights, sum to > MAX_VALUE
            // Only will happen if sums to more than 2 billion units.  Not likely.
            if (maxHeight != Integer.MAX_VALUE) {
                maxHeight = cpHeight + mbHeight + tpHeight + i.top + i.bottom;
            }

            int maxWidth = Math.max(Math.max(cpWidth, mbWidth), tpWidth);
            // Similar overflow comment as above
            if (maxWidth != Integer.MAX_VALUE) {
                maxWidth += i.left + i.right;
            }

            return new Dimension(maxWidth, maxHeight);
        }

        /**
         * Instructs the layout manager to perform the layout for the specified
         * container.
         *
         * @param parent The container for which this layout manager is being
         *               used.
         */
        public void layoutContainer(Container parent) {
            JRootPane root = (JRootPane) parent;
            Rectangle b = root.getBounds();
            Insets i = root.getInsets();
            int w = b.width - i.right - i.left;
            int h = b.height - i.top - i.bottom;

            if (root.getLayeredPane() != null) {
                root.getLayeredPane().setBounds(i.left, i.top, w, h);
            }
            if (root.getGlassPane() != null) {
                root.getGlassPane().setBounds(i.left, i.top, w, h);
            }
            // Note: This is laying out the children in the layeredPane,
            // technically, these are not our children.
            int nextY = 0;
            if (root.getWindowDecorationStyle() != JRootPane.NONE &&
                    root.getUI() instanceof NapkinRootPaneUI) {
                JComponent titlePane =
                        ((NapkinRootPaneUI) root.getUI()).getTitlePane();
                if (titlePane != null) {
                    Dimension tpd = titlePane.getPreferredSize();
                    if (tpd != null) {
                        int tpHeight = tpd.height;
                        titlePane.setBounds(0, 0, w, tpHeight);
                        nextY += tpHeight;
                    }
                }
            }
            if (root.getJMenuBar() != null) {
                Dimension mbd = root.getJMenuBar().getPreferredSize();
                root.getJMenuBar().setBounds(0, nextY, w, mbd.height);
                nextY += mbd.height;
            }
            if (root.getContentPane() != null) {
                root.getContentPane().setBounds(0, nextY, w,
                        h < nextY ? 0 : h - nextY);
            }
        }

        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        public void addLayoutComponent(Component comp, Object constraints) {
        }

        public float getLayoutAlignmentX(Container target) {
            return 0.0f;
        }

        public float getLayoutAlignmentY(Container target) {
            return 0.0f;
        }

        public void invalidateLayout(Container target) {
        }
    }

    /**
     * Maps from positions to cursor type. Refer to calculateCorner and
     * calculatePosition for details of this.
     */
    private static final int[] CURSOR_MAP =
            {Cursor.NW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR,
                    Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
                    Cursor.NE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, 0, 0, 0,
                    Cursor.NE_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR, 0, 0, 0,
                    Cursor.E_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, 0, 0, 0,
                    Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
                    Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
                    Cursor.SE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR};

    /**
     * This class is responsible for handling resize/moving of the window. It
     * sets the cursor directly on the window when then mouse moves over a hot
     * spot.
     */
    private class MouseInputHandler implements MouseInputListener {
        /** Set to true if the drag operation is moving the window. */
        private boolean isMovingWindow;

        /** Used to determine the corner the resize is occuring from. */
        private int dragCursor;

        /** X location the mouse went down on for a drag operation. */
        private int dragOffsetX;

        /** Y location the mouse went down on for a drag operation. */
        private int dragOffsetY;

        /** Width of the window when the drag started. */
        private int dragWidth;

        /** Height of the window when the drag started. */
        private int dragHeight;

        /**
         * PrivilegedExceptionAction needed by mouseDragged method to obtain new
         * location of window on screen during the drag.
         */
        private final PrivilegedExceptionAction<Point> getLocationAction =
                new PrivilegedExceptionAction<Point>() {
                    public Point run() throws HeadlessException {
                        return MouseInfo.getPointerInfo().getLocation();
                    }
                };

        @SuppressWarnings({"LocalVariableNamingConvention"})
        public void mousePressed(MouseEvent ev) {
            JRootPane rootPane = getRootPane();

            if (rootPane.getWindowDecorationStyle() != JRootPane.NONE) {
                Point dragWindowOffset = ev.getPoint();
                Window w = (Window) ev.getSource();
                if (w != null) {
                    w.toFront();
                }
                Point convertedDragWindowOffset = SwingUtilities.convertPoint(w,
                        dragWindowOffset, getTitlePane());

                Frame f = null;
                Dialog d = null;

                if (w instanceof Frame) {
                    f = (Frame) w;
                } else if (w instanceof Dialog) {
                    d = (Dialog) w;
                }

                int frameState = f != null ? f.getExtendedState() : 0;

                if (getTitlePane() != null && getTitlePane().contains(
                        convertedDragWindowOffset)) {

                    if ((f != null && (frameState & MAXIMIZED_BOTH) == 0 ||
                            d != null) &&
                            dragWindowOffset.y >= BORDER_DRAG_THICKNESS &&
                            dragWindowOffset.x >= BORDER_DRAG_THICKNESS &&
                            dragWindowOffset.x <
                                    w.getWidth() - BORDER_DRAG_THICKNESS) {

                        isMovingWindow = true;
                        dragOffsetX = dragWindowOffset.x;
                        dragOffsetY = dragWindowOffset.y;
                    }
                } else if (f != null && f.isResizable() &&
                        (frameState & MAXIMIZED_BOTH) == 0 ||
                        d != null && d.isResizable()) {

                    dragOffsetX = dragWindowOffset.x;
                    dragOffsetY = dragWindowOffset.y;
                    dragWidth = w.getWidth();
                    dragHeight = w.getHeight();
                    dragCursor = getCursor(calculateCorner(w,
                            dragWindowOffset.x, dragWindowOffset.y));
                }
            }
        }

        public void mouseReleased(MouseEvent ev) {
            if (dragCursor != 0 && window != null && !window.isValid()) {
                // Some Window systems validate as you resize, others won't,
                // thus the check for validity before repainting.
                window.validate();
                getRootPane().repaint();
            }
            isMovingWindow = false;
            dragCursor = 0;
        }

        public void mouseMoved(MouseEvent ev) {
            JRootPane root = getRootPane();

            if (root.getWindowDecorationStyle() != JRootPane.NONE) {
                Container w = (Window) ev.getSource();

                Frame f = null;
                Dialog d = null;

                if (w instanceof Frame) {
                    f = (Frame) w;
                } else if (w instanceof Dialog) {
                    d = (Dialog) w;
                }

                // Update the cursor
                int cursor = getCursor(calculateCorner(w, ev.getX(), ev.getY()))
                        ;

                w.setCursor(cursor != 0 && (f != null && f.isResizable() &&
                        (f.getExtendedState() & MAXIMIZED_BOTH) == 0 ||
                        d != null && d.isResizable()) ?
                        Cursor.getPredefinedCursor(cursor) :
                        lastCursor);
            }
        }

        private void adjust(Rectangle bounds, Dimension min, int deltaX,
                int deltaY, int deltaWidth, int deltaHeight) {

            bounds.x += deltaX;
            bounds.y += deltaY;
            bounds.width += deltaWidth;
            bounds.height += deltaHeight;
            if (min != null) {
                if (bounds.width < min.width) {
                    if (deltaX != 0) {
                        bounds.x -= min.width - bounds.width;
                    }
                    bounds.width = min.width;
                }
                if (bounds.height < min.height) {
                    if (deltaY != 0) {
                        bounds.y -= min.height - bounds.height;
                    }
                    bounds.height = min.height;
                }
            }
        }

        @SuppressWarnings({"UnusedCatchParameter", "TypeMayBeWeakened"})
        public void mouseDragged(MouseEvent ev) {
            Component w = (Window) ev.getSource();
            Point pt = ev.getPoint();

            if (isMovingWindow) {
                try {
                    Point windowPt = AccessController.doPrivileged(
                            getLocationAction);
                    windowPt.x -= dragOffsetX;
                    windowPt.y -= dragOffsetY;
                    w.setLocation(windowPt);
                } catch (PrivilegedActionException e) {
                    // getting the location is helpful, but not required
                }
            } else if (dragCursor != 0) {
                Rectangle r = w.getBounds();
                Rectangle startBounds = new Rectangle(r);
                Dimension min = w.getMinimumSize();

                int dragY = dragWidth - dragOffsetX;
                int dragX = dragHeight - dragOffsetY;
                int offX = pt.x - dragOffsetX;
                int offY = pt.y - dragOffsetY;
                switch (dragCursor) {
                case Cursor.E_RESIZE_CURSOR:
                    adjust(r, min, 0, 0, pt.x + dragY - r.width, 0);
                    break;
                case Cursor.S_RESIZE_CURSOR:
                    adjust(r, min, 0, 0, 0, pt.y + dragX - r.height);
                    break;
                case Cursor.N_RESIZE_CURSOR:
                    adjust(r, min, 0, offY, 0, -offY);
                    break;
                case Cursor.W_RESIZE_CURSOR:
                    adjust(r, min, offX, 0, -offX, 0);
                    break;
                case Cursor.NE_RESIZE_CURSOR:
                    adjust(r, min, 0, offY, pt.x + dragY - r.width, -offY);
                    break;
                case Cursor.SE_RESIZE_CURSOR:
                    adjust(r, min, 0, 0, pt.x + dragY - r.width,
                            pt.y + dragX - r.height);
                    break;
                case Cursor.NW_RESIZE_CURSOR:
                    adjust(r, min, offX, offY, -offX, -offY);
                    break;
                case Cursor.SW_RESIZE_CURSOR:
                    adjust(r, min, offX, 0, -offX, pt.y + dragX - r.height);
                    break;
                default:
                    break;
                }
                if (!r.equals(startBounds)) {
                    w.setBounds(r);
                    // Defer repaint/validate on mouseReleased unless dynamic
                    // layout is active.
                    if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
                        w.validate();
                        getRootPane().repaint();
                    }
                }
            }
        }

        public void mouseEntered(MouseEvent ev) {
            lastCursor = ((Component) ev.getSource()).getCursor();
            mouseMoved(ev);
        }

        public void mouseExited(MouseEvent ev) {
            ((Component) ev.getSource()).setCursor(lastCursor);
        }

        public void mouseClicked(MouseEvent ev) {
            Component w = (Window) ev.getSource();
            if (w instanceof Frame) {
                Frame f = (Frame) w;
                JComponent titlePane = getTitlePane();

                Point convertedPoint = SwingUtilities.convertPoint(w,
                        ev.getPoint(), titlePane);

                int state = f.getExtendedState();
                if (titlePane != null && titlePane.contains(convertedPoint) &&
                        ev.getClickCount() % 2 == 0 &&
                        (ev.getModifiers() & BUTTON1_MASK) != 0 &&
                        f.isResizable()) {

                    f.setExtendedState((state & MAXIMIZED_BOTH) != 0 ?
                            state & ~MAXIMIZED_BOTH :
                            state | MAXIMIZED_BOTH);
                }
            }
        }

        /**
         * Returns the corner that contains the point <tt>x</tt>, <tt>y</tt>, or
         * -1 if the position doesn't match a corner.
         */
        private int calculateCorner(Container w, int x, int y) {
            Insets insets = w.getInsets();
            int xPosition = calculatePosition(x - insets.left,
                    w.getWidth() - insets.left - insets.right);
            int yPosition = calculatePosition(y - insets.top,
                    w.getHeight() - insets.top - insets.bottom);

            return xPosition == -1 || yPosition == -1 ?
                    -1 :
                    yPosition * 5 + xPosition;
        }

        /**
         * Returns the Cursor to render for the specified corner. This returns 0
         * if the corner doesn't map to a valid cursor.
         */
        private int getCursor(int corner) {
            return corner == -1 ? 0 : CURSOR_MAP[corner];
        }

        /**
         * Returns an integer indicating the position of <tt>spot</tt> in
         * <tt>width</tt>. The return value will be: 0 if <
         * BORDER_DRAG_THICKNESS 1 if < CORNER_DRAG_WIDTH 2 if >=
         * CORNER_DRAG_WIDTH && < width - BORDER_DRAG_THICKNESS 3 if >= width -
         * CORNER_DRAG_WIDTH 4 if >= width - BORDER_DRAG_THICKNESS 5 otherwise
         */
        private int calculatePosition(int spot, int width) {
            return spot < BORDER_DRAG_THICKNESS ?
                    0 :
                    spot < CORNER_DRAG_WIDTH ?
                            1 :
                            spot >= width - BORDER_DRAG_THICKNESS ?
                                    4 :
                                    spot >= width - CORNER_DRAG_WIDTH ? 3 : 2;
        }
    }
}