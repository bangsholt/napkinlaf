package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.borders.NapkinBoxBorder;
import net.sourceforge.napkinlaf.borders.NapkinLineBorder;
import net.sourceforge.napkinlaf.borders.NapkinWrappedBorder;
import net.sourceforge.napkinlaf.util.NapkinBackground;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import java.awt.*;
import static java.awt.RenderingHints.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** @author Alex Lam Sze Lok */
@SuppressWarnings(
        {"WeakerAccess", "JavaDoc", "NonSerializableFieldInSerializableClass"})
public class NapkinTitlePane extends JComponent {

    private static final Border EMPTY_BORDER = new NapkinWrappedBorder(
            new EmptyBorder(0, 0, 0, 0));
    private static final int IMAGE_HEIGHT = 18;
    private static final int IMAGE_WIDTH = 18;

    /** PropertyChangeListener added to the JRootPane. */
    private PropertyChangeListener propertyChangeListener;

    /** JMenuBar, typically renders the system menu items. */
    private JMenuBar menuBar;
    /** Action used to close the Window. */
    private Action closeAction;

    /** Action used to iconify the Frame. */
    private Action iconifyAction;

    /** Action to restore the Frame size. */
    private Action restoreAction;

    /** Action to restore the Frame size. */
    private Action maximizeAction;

    /** Button used to maximize or restore the Frame. */
    private JButton toggleButton;

    /** Button used to maximize or restore the Frame. */
    private JButton iconifyButton;

    /** Button used to maximize or restore the Frame. */
    private JButton closeButton;

    /** Icon used for toggleButton when window is normal size. */
    private Icon maximizeIcon;

    /** Icon used for toggleButton when window is maximized. */
    private Icon minimizeIcon;

    /**
     * Listens for changes in the state of the Window listener to update the
     * state of the widgets.
     */
    private WindowListener windowListener;

    /** Window we're currently in. */
    private Window window;

    /** JRootPane rendering for. */
    private JRootPane rootPane;

    /**
     * Buffered Frame.state property. As state isn't bound, this is kept to
     * determine when to avoid updating widgets.
     */
    private int state;

    public NapkinTitlePane(JRootPane root) {
        rootPane = root;

        state = -1;

        installSubcomponents();
        installDefaults();

        setLayout(createLayout());
    }

    /** Installs the necessary listeners. */
    private void installListeners() {
        if (window != null) {
            windowListener = createWindowListener();
            window.addWindowListener(windowListener);
            propertyChangeListener = propertyChangeListener();
            window.addPropertyChangeListener(propertyChangeListener);
        }
    }

    /** Uninstalls the necessary listeners. */
    private void uninstallListeners() {
        if (window != null) {
            window.removeWindowListener(windowListener);
            window.removePropertyChangeListener(propertyChangeListener);
        }
    }

    /** @return The <tt>WindowListener</tt> to add to the <tt>Window</tt>. */
    private WindowListener createWindowListener() {
        return new WindowHandler();
    }

    /**
     * @return The <tt>PropertyChangeListener</tt> to install on the
     *         <tt>Window</tt>.
     */
    private PropertyChangeListener propertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /** @return The <tt>JRootPane</tt> this was created for. */
    @Override
    public JRootPane getRootPane() {
        return rootPane;
    }

    /** @return The decoration style of the <tt>JRootPane</tt>. */
    private int getWindowDecorationStyle() {
        return getRootPane().getWindowDecorationStyle();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        uninstallListeners();

        window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            if (window instanceof Frame) {
                setState(((Frame) window).getExtendedState());
            } else {
                setState(0);
            }
            setActive(window.isActive());
            installListeners();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();

        uninstallListeners();
        window = null;
    }

    /** Adds any sub-Components contained in the <tt>NapkinTitlePane</tt>. */
    private void installSubcomponents() {
        int decorationStyle = getWindowDecorationStyle();
        if (decorationStyle == JRootPane.FRAME) {
            createActions();
            menuBar = createMenuBar();
            add(menuBar);
            createButtons();
            add(iconifyButton);
            add(toggleButton);
            add(closeButton);
        } else if (decorationStyle == JRootPane.PLAIN_DIALOG ||
                decorationStyle == JRootPane.INFORMATION_DIALOG ||
                decorationStyle == JRootPane.ERROR_DIALOG ||
                decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG ||
                decorationStyle == JRootPane.FILE_CHOOSER_DIALOG ||
                decorationStyle == JRootPane.QUESTION_DIALOG ||
                decorationStyle == JRootPane.WARNING_DIALOG) {
            createActions();
            createButtons();
            add(closeButton);
        }
    }

    /** Installs the fonts and necessary properties on the NapkinTitlePane. */
    private void installDefaults() {
        setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
        setBorder(new NapkinLineBorder(false));
    }

    /**
     * @return The <tt>JMenuBar</tt> displaying the appropriate system menu
     *         items.
     */
    protected JMenuBar createMenuBar() {
        menuBar = new SystemMenuBar();
        menuBar.setFocusable(false);
        menuBar.setBorderPainted(true);
        menuBar.add(createMenu());
        return menuBar;
    }

    /** Closes the Window. */
    private void close() {
        if (window != null) {
            window.dispatchEvent(new WindowEvent(window,
                    WindowEvent.WINDOW_CLOSING));
        }
    }

    /** Iconifies the Frame. */
    private void iconify() {
        Frame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(state | Frame.ICONIFIED);
        }
    }

    /** Maximizes the Frame. */
    private void maximize() {
        Frame frame = getFrame();
        if (frame != null) {
            frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
        }
    }

    /** Restores the Frame size. */
    private void restore() {
        Frame frame = getFrame();

        if (frame == null) {
            return;
        }

        if ((state & Frame.ICONIFIED) != 0) {
            frame.setExtendedState(state & ~Frame.ICONIFIED);
        } else {
            frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Create the <tt>Action</tt>s that get associated with the buttons and menu
     * items.
     */
    private void createActions() {
        closeAction = new CloseAction();
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            iconifyAction = new IconifyAction();
            restoreAction = new RestoreAction();
            maximizeAction = new MaximizeAction();
        }
    }

    /**
     * @return The <tt>JMenu</tt> displaying the appropriate menu items for
     *         manipulating the Frame.
     */
    private JMenu createMenu() {
        JMenu menu = new JMenu("");
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            addMenuItems(menu);
        }
        return menu;
    }

    /** Adds the necessary <tt>JMenuItem</tt>s to the passed in menu. */
    @SuppressWarnings({"TypeMayBeWeakened"})
    private static void addMenuItems(JMenu menu) {
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(
                Frame.MAXIMIZED_BOTH)) {
        }
        menu.add(new JSeparator());
    }

    /** @return A <tt>JButton</tt> appropriate for placement on the title pane. */
    private JButton createTitleButton(Action action) {
        JButton button = new JButton(action) {
            @Override
            public void setBorder(Border value) {
                super.setBorder(getIcon() == null ?
                        new NapkinBoxBorder() :
                        EMPTY_BORDER);
            }
        };
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setOpaque(false);
        return button;
    }

    /** Creates the Buttons that will be placed on the TitlePane. */
    private void createButtons() {
        closeButton = createTitleButton(closeAction);
        closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
        closeButton.setText(null);
        closeButton.setBorder(EMPTY_BORDER);
        closeButton.putClientProperty("paintActive", Boolean.TRUE);
        closeButton.getAccessibleContext().setAccessibleName("Close");

        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            maximizeIcon = null;
            minimizeIcon = null;

            iconifyButton = createTitleButton(iconifyAction);
            iconifyButton.setIcon(UIManager.getIcon(
                    "InternalFrame.iconifyIcon"));
            iconifyButton.setText(null);
            iconifyButton.setBorder(EMPTY_BORDER);
            iconifyButton.putClientProperty("paintActive", Boolean.TRUE);
            iconifyButton.getAccessibleContext().setAccessibleName("Iconify");

            toggleButton = createTitleButton(restoreAction);
            toggleButton.setIcon(maximizeIcon);
            toggleButton.setText(null);
            toggleButton.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
            toggleButton.setBorder(new NapkinBoxBorder());
            toggleButton.putClientProperty("paintActive", Boolean.TRUE);
            toggleButton.getAccessibleContext().setAccessibleName("Maximize");
        }
    }

    /**
     * @return The <tt>LayoutManager</tt> that should be installed on the
     *         <tt>NapkinTitlePane</tt>.
     */
    private LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    /** Updates state dependant upon the Window's active state. */
    private void setActive(boolean isActive) {
        Boolean activeB = isActive ? Boolean.TRUE : Boolean.FALSE;

        closeButton.putClientProperty("paintActive", activeB);
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            iconifyButton.putClientProperty("paintActive", activeB);
            toggleButton.putClientProperty("paintActive", activeB);
        }
        // Repaint the whole thing as the Borders that are used have
        // different colors for active vs inactive
        getRootPane().repaint();
    }

    /** Sets the state of the Window. */
    private void setState(int state) {
        setState(state, false);
    }

    /**
     * Sets the state of the window. If <tt>updateRegardless</tt> is true and
     * the state has not changed, this will update anyway.
     */
    private void setState(int state, boolean updateRegardless) {
        Window w = getWindow();

        if (w != null && getWindowDecorationStyle() == JRootPane.FRAME) {
            if (this.state == state && !updateRegardless) {
                return;
            }
            Frame frame = getFrame();

            if (frame != null) {
                if (((state & Frame.MAXIMIZED_BOTH) != 0) && (
                        rootPane.getBorder() == null ||
                                (rootPane.getBorder() instanceof UIResource)) &&
                        frame.isShowing()) {
                    rootPane.setBorder(null);
                } else if ((state & Frame.MAXIMIZED_BOTH) == 0) {
                    // This is a croak, if state becomes bound, this can
                    // be nuked.
                    NapkinRootPaneUI.installBorder(rootPane);
                }
                if (frame.isResizable()) {
                    if ((state & Frame.MAXIMIZED_BOTH) != 0) {
                        updateToggleButton(restoreAction, minimizeIcon);
                        maximizeAction.setEnabled(false);
                        restoreAction.setEnabled(true);
                    } else {
                        updateToggleButton(maximizeAction, maximizeIcon);
                        maximizeAction.setEnabled(true);
                        restoreAction.setEnabled(false);
                    }
                    if (toggleButton.getParent() == null ||
                            iconifyButton.getParent() == null) {
                        add(toggleButton);
                        add(iconifyButton);
                        revalidate();
                        repaint();
                    }
                    toggleButton.setText(null);
                } else {
                    maximizeAction.setEnabled(false);
                    restoreAction.setEnabled(false);
                    if (toggleButton.getParent() != null) {
                        remove(toggleButton);
                        revalidate();
                        repaint();
                    }
                }
            } else {
                // Not contained in a Frame
                maximizeAction.setEnabled(false);
                restoreAction.setEnabled(false);
                iconifyAction.setEnabled(false);
                remove(toggleButton);
                remove(iconifyButton);
                revalidate();
                repaint();
            }
            closeAction.setEnabled(true);
            this.state = state;
        }
    }

    /**
     * Updates the toggle button to contain the Icon <tt>icon</tt>, and Action
     * <tt>action</tt>.
     */
    private void updateToggleButton(Action action, Icon icon) {
        toggleButton.setAction(action);
        toggleButton.setIcon(icon);
        toggleButton.setText(null);
    }

    /**
     * @return The Frame rendering in. This will return null if the
     *         <tt>JRootPane</tt> is not contained in a <tt>Frame</tt>.
     */
    private Frame getFrame() {
        if (window instanceof Frame) {
            return (Frame) window;
        }
        return null;
    }

    /**
     * @return The <tt>Window</tt> the <tt>JRootPane</tt> is contained in. Will
     *         return <tt>null</tt> if there is no parent ancestor of the
     *         <tt>JRootPane</tt>.
     */
    private Window getWindow() {
        return window;
    }

    /** @return The string to display as the title. */
    private String getTitle() {
        Window w = getWindow();

        if (w instanceof Frame) {
            return ((Frame) w).getTitle();
        } else if (w instanceof Dialog) {
            return ((Dialog) w).getTitle();
        }
        return null;
    }

    /** Renders the TitlePane. */
    @Override
    public void paintComponent(Graphics g) {
        // As state isn't bound, we need a convenience place to check
        // if it has changed. Changing the state typically changes the
        if (getFrame() != null) {
            setState(getFrame().getExtendedState());
        }
        boolean leftToRight = window == null ?
                rootPane.getComponentOrientation().isLeftToRight() :
                window.getComponentOrientation().isLeftToRight();
        boolean isSelected = window == null || window.isActive();
        int width = getWidth();
        int height = getHeight();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
        NapkinTheme theme = NapkinTheme.Manager.getCurrentTheme();
        Color foreground =
                isSelected ? theme.getSelectionColor() : theme.getPenColor();
        NapkinBackground bg = theme.getPaper();
        Rectangle bounds = getBounds();
        Insets insets = getInsets();
        bounds.x -= insets.left;
        bounds.width += insets.left + insets.right;
        bounds.y -= insets.top;
        bounds.width += insets.top + insets.bottom;
        bg.paint(this, g, bounds, bounds, insets);

        int xOffset = leftToRight ? 5 : width - 5;

        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            xOffset += leftToRight ? IMAGE_WIDTH + 5 : -IMAGE_WIDTH - 5;
        }

        String theTitle = getTitle();
        if (theTitle != null) {
            FontMetrics fm = g.getFontMetrics();

            g.setColor(foreground);

            int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();

            Rectangle rect = new Rectangle(0, 0, 0, 0);
            if (iconifyButton != null && iconifyButton.getParent() != null) {
                rect = iconifyButton.getBounds();
            }
            int titleW;

            if (leftToRight) {
                if (rect.x == 0 && window != null) {
                    rect.x = window.getWidth() - window.getInsets().right - 2;
                }
                titleW = rect.x - xOffset - 4;
                theTitle = clipStringIfNecessary(fm, theTitle, titleW);
            } else {
                titleW = xOffset - rect.x - rect.width - 4;
                theTitle = clipStringIfNecessary(fm, theTitle, titleW);
                xOffset -= fm.stringWidth(theTitle);
            }
            int titleLength = fm.stringWidth(theTitle);
            g.drawString(theTitle, xOffset, yOffset);
            xOffset += leftToRight ? titleLength + 5 : -5;
        }
    }

    @SuppressWarnings({"StringContatenationInLoop"})
    private static String clipStringIfNecessary(FontMetrics fm, String text,
            int width) {

        int textWidth = fm.stringWidth(text);
        int len = text.length();
        String result = text;
        while (len > 0 && textWidth > width) {
            result = text.substring(0, len) + "...";
            textWidth = fm.stringWidth(result);
            len--;
        }
        return result;
    }

    /** Actions used to <tt>close</tt> the <tt>Window</tt>. */
    @SuppressWarnings({"CloneableClassWithoutClone"})
    private class CloseAction extends AbstractAction {
        CloseAction() {
            super("Close");
        }

        public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    /** Actions used to <tt>iconfiy</tt> the <tt>Frame</tt>. */
    @SuppressWarnings({"CloneableClassWithoutClone"})
    private class IconifyAction extends AbstractAction {
        IconifyAction() {
            super("Minimize");
        }

        public void actionPerformed(ActionEvent e) {
            iconify();
        }
    }

    /** Actions used to <tt>restore</tt> the <tt>Frame</tt>. */
    @SuppressWarnings({"CloneableClassWithoutClone"})
    private class RestoreAction extends AbstractAction {
        RestoreAction() {
            super("Restore");
        }

        public void actionPerformed(ActionEvent e) {
            restore();
        }
    }

    /** Actions used to <tt>restore</tt> the <tt>Frame</tt>. */
    @SuppressWarnings({"CloneableClassWithoutClone"})
    private class MaximizeAction extends AbstractAction {
        MaximizeAction() {
            super("Maximize");
        }

        public void actionPerformed(ActionEvent e) {
            maximize();
        }
    }

    /**
     * Class responsible for drawing the system menu. Looks up the image to draw
     * from the Frame associated with the <tt>JRootPane</tt>.
     */
    private class SystemMenuBar extends JMenuBar {
        @Override
        public void paint(Graphics g) {
            Frame frame = getFrame();

            if (isOpaque()) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            Image image = (frame != null) ? frame.getIconImage() : null;

            if (image != null) {
                g.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            } else {
                Icon icon = UIManager.getIcon("InternalFrame.icon");

                if (icon != null) {
                    icon.paintIcon(this, g, 0, 0);
                }
            }
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();

            return new Dimension(Math.max(IMAGE_WIDTH, size.width), Math.max(
                    size.height, IMAGE_HEIGHT));
        }
    }

    private class TitlePaneLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            int height = computeHeight();
            //noinspection SuspiciousNameCombination
            return new Dimension(height, height);
        }

        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        }

        private int computeHeight() {
            FontMetrics fm = rootPane.getFontMetrics(getFont());
            int fontHeight = fm.getHeight();
            fontHeight += 7;
            int iconHeight = 0;
            if (getWindowDecorationStyle() == JRootPane.FRAME) {
                iconHeight = IMAGE_HEIGHT;
            }

            return Math.max(fontHeight, iconHeight);
        }

        public void layoutContainer(Container c) {
            boolean leftToRight = (window == null) ?
                    getRootPane().getComponentOrientation().isLeftToRight() :
                    window.getComponentOrientation().isLeftToRight();

            int w = getWidth();
            int buttonHeight;
            int buttonWidth;

            if (closeButton != null && closeButton.getIcon() != null) {
                buttonHeight = closeButton.getIcon().getIconHeight();
                buttonWidth = closeButton.getIcon().getIconWidth();
            } else {
                buttonHeight = IMAGE_HEIGHT;
                buttonWidth = IMAGE_WIDTH;
            }

            // assumes all buttons have the same dimensions
            // these dimensions include the borders

            int spacing = 5;
            int x = leftToRight ? spacing : w - buttonWidth - spacing;
            int y = 3;
            if (menuBar != null) {
                menuBar.setBounds(x, y, buttonWidth, buttonHeight);
            }

            x = leftToRight ? w : 0;
            spacing = 4;
            x += leftToRight ? -spacing - buttonWidth : spacing;
            if (closeButton != null) {
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);
            }

            if (!leftToRight) {
                x += buttonWidth;
            }

            if (getWindowDecorationStyle() == JRootPane.FRAME) {
                if (Toolkit.getDefaultToolkit().isFrameStateSupported(
                        Frame.MAXIMIZED_BOTH)) {
                    if (toggleButton.getParent() != null) {
                        spacing = 10;
                        x += leftToRight ? -spacing - buttonWidth : spacing;
                        toggleButton.setBounds(x, y, buttonWidth, buttonHeight);
                        if (!leftToRight) {
                            x += buttonWidth;
                        }
                    }
                }

                if (iconifyButton != null &&
                        iconifyButton.getParent() != null) {
                    spacing = 2;
                    x += leftToRight ? -spacing - buttonWidth : spacing;
                    iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);
                    if (!leftToRight) {
                        x += buttonWidth;
                    }
                }
            }
        }
    }

    /**
     * PropertyChangeListener installed on the Window. Updates the necessary
     * state as the state of the Window changes.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pce) {
            String name = pce.getPropertyName();

            // Frame.state isn't currently bound.
            if ("resizable".equals(name) || "state".equals(name)) {
                Frame frame = getFrame();

                if (frame != null) {
                    setState(frame.getExtendedState(), true);
                }
                if ("resizable".equals(name)) {
                    getRootPane().repaint();
                }
            } else if ("title".equals(name)) {
                repaint();
            } else if ("componentOrientation".equals(name) ||
                    "iconImage".equals(name)) {
                revalidate();
                repaint();
            }
        }
    }

    /** WindowListener installed on the Window, updates the state as necessary. */
    private class WindowHandler extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent ev) {
            setActive(true);
        }

        @Override
        public void windowDeactivated(WindowEvent ev) {
            setActive(false);
        }
    }
}
