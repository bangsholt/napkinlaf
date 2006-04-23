package net.sourceforge.napkinlaf;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.swing.border.Border;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

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
    private Cursor lastCursor =
            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    /** Keys to lookup borders in defaults table. */
    private static final String[] BORDER_KEYS = new String[]{
            null, "RootPane.frameBorder", "RootPane.plainDialogBorder",
            "RootPane.informationDialogBorder",
            "RootPane.errorDialogBorder", "RootPane.colorChooserDialogBorder",
            "RootPane.fileChooserDialogBorder", "RootPane.questionDialogBorder",
            "RootPane.warningDialogBorder"
    };

    /** The amount of space (in pixels) that the cursor is changed on. */
    private static final int CORNER_DRAG_WIDTH = 16;

    /** Region from edges that dragging is active from. */
    private static final int BORDER_DRAG_THICKNESS = 5;

    /**
     * Creates a UI for a <tt>JRootPane</tt>.
     *
     * @param c the JRootPane the RootPaneUI will be created for
     *
     * @return the RootPaneUI implementation for the passed in JRootPane
     */
    public static ComponentUI createUI(JComponent c) {
        return new NapkinRootPaneUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        root = (JRootPane) c;
        int style = root.getWindowDecorationStyle();
        if (style != JRootPane.NONE) {
            installClientDecorations(root);
        }
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
        uninstallClientDecorations(root);
    }

    void installBorder(JRootPane root) {
        int style = root.getWindowDecorationStyle();
        if (style != JRootPane.NONE) {
            root.setBorder(
                    (Border) UIManager.getDefaults().get(BORDER_KEYS[style]));
        }
    }

    /** Removes any border that may have been installed. */
    private void uninstallBorder(JRootPane root) {
        LookAndFeel.uninstallBorder(root);
    }

    /**
     * Installs the necessary listeners on the parent window, if there is one.
     * This takes the parent so that cleanup can be done from
     * <tt>removeNotify</tt>, at which point the parent hasn't been reset yet.
     *
     * @param parent The parent of the <tt>JRootPane</tt>.
     */
    private void installWindowListeners(Component parent) {
        if (parent instanceof Window) {
            window = (Window) parent;
        } else {
            window = SwingUtilities.getWindowAncestor(parent);
        }
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
    private void installLayout(JRootPane root) {
        if (layoutManager == null) {
            layoutManager = createLayoutManager();
        }
        savedOldLayout = root.getLayout();
        root.setLayout(layoutManager);
    }

    /** Uninstalls the previously installed <tt>LayoutManager</tt>. */
    private void uninstallLayout(JRootPane root) {
        if (savedOldLayout != null) {
            root.setLayout(savedOldLayout);
        }
    }

    /**
     * Installs the necessary state onto the root pane to render client
     * decorations. This is <em>only</em> invoked if the root pane has a
     * decoration style other than <tt>JRootPane.NONE</tt>.
     */
    private void installClientDecorations(JRootPane root) {
        installBorder(root);

        JComponent titlePane = createTitlePane(root);

        setTitlePane(root, titlePane);
        installWindowListeners(root.getParent());
        installLayout(root);
        if (window != null) {
            root.revalidate();
            root.repaint();
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
    private void uninstallClientDecorations(JRootPane root) {
        uninstallBorder(root);
        uninstallWindowListeners();
        setTitlePane(root, null);
        uninstallLayout(root);
        // We have to revalidate/repaint root if the style is JRootPane.NONE
        // only. When we needs to call revalidate/repaint with other styles
        // the installClientDecorations is always called after this method
        // imediatly and it will cause the revalidate/repaint at the proper
        // time.
        int style = root.getWindowDecorationStyle();
        if (style == JRootPane.NONE) {
            root.repaint();
            root.revalidate();
        }
        // Reset the cursor, as we may have changed it to a resize cursor
        if (window != null) {
            window.setCursor(Cursor.getPredefinedCursor
                    (Cursor.DEFAULT_CURSOR));
        }
        window = null;
    }

    /** Returns the <tt>JComponent</tt> to render the window decoration style. */
    private JComponent createTitlePane(JRootPane root) {
        return new NapkinTitlePane(root, this);
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
    private LayoutManager createLayoutManager() {
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
    public void propertyChange(PropertyChangeEvent ev) {
        super.propertyChange(ev);

        String propertyName = ev.getPropertyName();
        if (propertyName == null) {
            return;
        }

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
            JRootPane pane = ((JRootPane) ev.getSource());
            if (pane.getWindowDecorationStyle() != JRootPane.NONE) {
                installWindowListeners(root.getParent());
            }
        }
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
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
            Dimension cpd, mbd, tpd;
            int cpWidth = 0;
            int cpHeight = 0;
            int mbWidth = 0;
            int mbHeight = 0;
            int tpWidth = 0;
            Insets i = parent.getInsets();
            JRootPane root = (JRootPane) parent;

            if (root.getContentPane() != null) {
                cpd = root.getContentPane().getPreferredSize();
            } else {
                cpd = root.getSize();
            }
            if (cpd != null) {
                cpWidth = cpd.width;
                cpHeight = cpd.height;
            }

            if (root.getJMenuBar() != null) {
                mbd = root.getJMenuBar().getPreferredSize();
                if (mbd != null) {
                    mbWidth = mbd.width;
                    mbHeight = mbd.height;
                }
            }

            if (root.getWindowDecorationStyle() != JRootPane.NONE &&
                    (root.getUI() instanceof NapkinRootPaneUI)) {
                JComponent titlePane = ((NapkinRootPaneUI) root.getUI()).
                        getTitlePane();
                if (titlePane != null) {
                    tpd = titlePane.getPreferredSize();
                    if (tpd != null) {
                        tpWidth = tpd.width;
                    }
                }
            }

            return new Dimension(Math.max(Math.max(cpWidth, mbWidth), tpWidth) +
                    i.left + i.right,
                    cpHeight + mbHeight + tpWidth + i.top + i.bottom);
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
            Dimension cpd, mbd, tpd;
            int cpWidth = 0;
            int cpHeight = 0;
            int mbWidth = 0;
            int mbHeight = 0;
            int tpWidth = 0;
            Insets i = parent.getInsets();
            JRootPane root = (JRootPane) parent;

            if (root.getContentPane() != null) {
                cpd = root.getContentPane().getMinimumSize();
            } else {
                cpd = root.getSize();
            }
            if (cpd != null) {
                cpWidth = cpd.width;
                cpHeight = cpd.height;
            }

            if (root.getJMenuBar() != null) {
                mbd = root.getJMenuBar().getMinimumSize();
                if (mbd != null) {
                    mbWidth = mbd.width;
                    mbHeight = mbd.height;
                }
            }
            if (root.getWindowDecorationStyle() != JRootPane.NONE &&
                    root.getUI() instanceof NapkinRootPaneUI) {
                JComponent titlePane = ((NapkinRootPaneUI) root.getUI()).
                        getTitlePane();
                if (titlePane != null) {
                    tpd = titlePane.getMinimumSize();
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
            Dimension cpd, mbd, tpd;
            int cpWidth = Integer.MAX_VALUE;
            int cpHeight = Integer.MAX_VALUE;
            int mbWidth = Integer.MAX_VALUE;
            int mbHeight = Integer.MAX_VALUE;
            int tpWidth = Integer.MAX_VALUE;
            int tpHeight = Integer.MAX_VALUE;
            Insets i = target.getInsets();
            JRootPane root = (JRootPane) target;

            if (root.getContentPane() != null) {
                cpd = root.getContentPane().getMaximumSize();
                if (cpd != null) {
                    cpWidth = cpd.width;
                    cpHeight = cpd.height;
                }
            }

            if (root.getJMenuBar() != null) {
                mbd = root.getJMenuBar().getMaximumSize();
                if (mbd != null) {
                    mbWidth = mbd.width;
                    mbHeight = mbd.height;
                }
            }

            if (root.getWindowDecorationStyle() != JRootPane.NONE &&
                    root.getUI() instanceof NapkinRootPaneUI) {
                JComponent titlePane = ((NapkinRootPaneUI) root.getUI()).
                        getTitlePane();
                if (titlePane != null) {
                    tpd = titlePane.getMaximumSize();
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
            int nextY = 0;
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
            if (root.getWindowDecorationStyle() != JRootPane.NONE &&
                    (root.getUI() instanceof NapkinRootPaneUI)) {
                JComponent titlePane = ((NapkinRootPaneUI) root.getUI()).
                        getTitlePane();
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
                int height = h < nextY ? 0 : h - nextY;
                root.getContentPane().setBounds(0, nextY, w, height);
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
    private static final int[] CURSOR_MAP = new int[]{
            Cursor.NW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR,
            Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
            Cursor.NE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, 0, 0, 0,
            Cursor.NE_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR, 0, 0, 0,
            Cursor.E_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, 0, 0, 0,
            Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
            Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
            Cursor.SE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR
    };

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
        private final PrivilegedExceptionAction<Point> GET_LOCATION_ACTION =
                new PrivilegedExceptionAction() {
                    public Point run() throws HeadlessException {
                        return MouseInfo.getPointerInfo().getLocation();
                    }
                };

        public void mousePressed(MouseEvent ev) {
            JRootPane rootPane = getRootPane();

            if (rootPane.getWindowDecorationStyle() == JRootPane.NONE) {
                return;
            }
            Point dragWindowOffset = ev.getPoint();
            Window w = (Window) ev.getSource();
            if (w != null) {
                w.toFront();
            }
            Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                    w, dragWindowOffset, getTitlePane());

            Frame f = null;
            Dialog d = null;

            if (w instanceof Frame) {
                f = (Frame) w;
            } else if (w instanceof Dialog) {
                d = (Dialog) w;
            }

            int frameState = f != null ? f.getExtendedState() : 0;

            if (getTitlePane() != null &&
                    getTitlePane().contains(convertedDragWindowOffset)) {
                if ((f != null && (frameState & Frame.MAXIMIZED_BOTH) == 0 ||
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
                    (frameState & Frame.MAXIMIZED_BOTH) == 0 ||
                    d != null && d.isResizable()) {
                dragOffsetX = dragWindowOffset.x;
                dragOffsetY = dragWindowOffset.y;
                dragWidth = w.getWidth();
                dragHeight = w.getHeight();
                dragCursor = getCursor(calculateCorner(
                        w, dragWindowOffset.x, dragWindowOffset.y));
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

            if (root.getWindowDecorationStyle() == JRootPane.NONE) {
                return;
            }

            Window w = (Window) ev.getSource();

            Frame f = null;
            Dialog d = null;

            if (w instanceof Frame) {
                f = (Frame) w;
            } else if (w instanceof Dialog) {
                d = (Dialog) w;
            }

            // Update the cursor
            int cursor = getCursor(calculateCorner(w, ev.getX(), ev.getY()));

            if (cursor != 0 && ((f != null && (f.isResizable() &&
                    (f.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0))
                    || (d != null && d.isResizable()))) {
                w.setCursor(Cursor.getPredefinedCursor(cursor));
            } else {
                w.setCursor(lastCursor);
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
                    int correction = min.width - bounds.width;
                    if (deltaX != 0) {
                        bounds.x -= correction;
                    }
                    bounds.width = min.width;
                }
                if (bounds.height < min.height) {
                    int correction = min.height - bounds.height;
                    if (deltaY != 0) {
                        bounds.y -= correction;
                    }
                    bounds.height = min.height;
                }
            }
        }

        public void mouseDragged(MouseEvent ev) {
            Window w = (Window) ev.getSource();
            Point pt = ev.getPoint();

            if (isMovingWindow) {
                Point windowPt;
                try {
                    windowPt = AccessController.doPrivileged(
                            GET_LOCATION_ACTION);
                    windowPt.x = windowPt.x - dragOffsetX;
                    windowPt.y = windowPt.y - dragOffsetY;
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
            Window w = (Window) ev.getSource();
            lastCursor = w.getCursor();
            mouseMoved(ev);
        }

        public void mouseExited(MouseEvent ev) {
            Window w = (Window) ev.getSource();
            w.setCursor(lastCursor);
        }

        public void mouseClicked(MouseEvent ev) {
            Window w = (Window) ev.getSource();
            Frame f;

            if (w instanceof Frame) {
                f = (Frame) w;
            } else {
                return;
            }

            JComponent titlePane = getTitlePane();

            Point convertedPoint =
                    SwingUtilities.convertPoint(w, ev.getPoint(), titlePane);

            int state = f.getExtendedState();
            if (titlePane != null && titlePane.contains(convertedPoint)) {
                if (ev.getClickCount() % 2 == 0 &&
                        (ev.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                    if (f.isResizable()) {
                        if ((state & Frame.MAXIMIZED_BOTH) != 0) {
                            f.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
                        } else {
                            f.setExtendedState(state | Frame.MAXIMIZED_BOTH);
                        }
                    }
                }
            }
        }

        /**
         * Returns the corner that contains the point <tt>x</tt>, <tt>y</tt>, or
         * -1 if the position doesn't match a corner.
         */
        private int calculateCorner(Window w, int x, int y) {
            Insets insets = w.getInsets();
            int xPosition = calculatePosition(x - insets.left,
                    w.getWidth() - insets.left - insets.right);
            int yPosition = calculatePosition(y - insets.top,
                    w.getHeight() - insets.top - insets.bottom);

            if (xPosition == -1 || yPosition == -1) {
                return -1;
            }
            return yPosition * 5 + xPosition;
        }

        /**
         * Returns the Cursor to render for the specified corner. This returns 0
         * if the corner doesn't map to a valid cursor.
         */
        private int getCursor(int corner) {
            if (corner == -1) {
                return 0;
            }
            return CURSOR_MAP[corner];
        }

        /**
         * Returns an integer indicating the position of <tt>spot</tt> in
         * <tt>width</tt>. The return value will be: 0 if <
         * BORDER_DRAG_THICKNESS 1 if < CORNER_DRAG_WIDTH 2 if >=
         * CORNER_DRAG_WIDTH && < width - BORDER_DRAG_THICKNESS 3 if >= width -
         * CORNER_DRAG_WIDTH 4 if >= width - BORDER_DRAG_THICKNESS 5 otherwise
         */
        private int calculatePosition(int spot, int width) {
            if (spot < BORDER_DRAG_THICKNESS) {
                return 0;
            }
            if (spot < CORNER_DRAG_WIDTH) {
                return 1;
            }
            if (spot >= width - BORDER_DRAG_THICKNESS) {
                return 4;
            }
            if (spot >= width - CORNER_DRAG_WIDTH) {
                return 3;
            }
            return 2;
        }
    }
}