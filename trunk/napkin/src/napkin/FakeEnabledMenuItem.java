package napkin;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.im.*;
import java.awt.image.*;
import java.awt.peer.*;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.EventListener;
import java.util.Locale;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

/**
 * This is required because some parts of the code require a <tt>JMenuItem</tt>
 * instead of an abstract button.
 * <p/>
 * If I could I would multiply inherit from {@link FakeEnabledButton} and
 * <tt>JMenuItem</tt> to just override the ones that are added, but I can't do
 * that so I must delegate (or make this class a modified copy, but that would
 * be worse).
 *
 * @see FakeEnabledButton
 */
class FakeEnabledMenuItem extends JMenuItem implements FakeEnabled {
    private final FakeEnabledButton forced;
    private final JMenuItem orig;

    FakeEnabledMenuItem(JComponent c) {
        if (c == null)
            throw new NullPointerException("c");
        orig = (JMenuItem) c;
        forced = new FakeEnabledButton(c);
    }

    public void addMenuDragMouseListener(MenuDragMouseListener l) {
        orig.addMenuDragMouseListener(l);
    }

    public void addMenuKeyListener(MenuKeyListener l) {
        orig.addMenuKeyListener(l);
    }

    public KeyStroke getAccelerator() {
        return orig.getAccelerator();
    }

    public Component getComponent() {
        return orig.getComponent();
    }

    public MenuDragMouseListener[] getMenuDragMouseListeners() {
        return orig.getMenuDragMouseListeners();
    }

    public MenuKeyListener[] getMenuKeyListeners() {
        return orig.getMenuKeyListeners();
    }

    public MenuElement[] getSubElements() {
        return orig.getSubElements();
    }

    public boolean isArmed() {
        return orig.isArmed();
    }

    public void menuSelectionChanged(boolean isIncluded) {
        orig.menuSelectionChanged(isIncluded);
    }

    public void processKeyEvent(KeyEvent e, MenuElement path[], MenuSelectionManager manager) {
        orig.processKeyEvent(e, path, manager);
    }

    public void processMenuDragMouseEvent(MenuDragMouseEvent e) {
        orig.processMenuDragMouseEvent(e);
    }

    public void processMenuKeyEvent(MenuKeyEvent e) {
        orig.processMenuKeyEvent(e);
    }

    public void processMouseEvent(MouseEvent e, MenuElement path[], MenuSelectionManager manager) {
        orig.processMouseEvent(e, path, manager);
    }

    public void removeMenuDragMouseListener(MenuDragMouseListener l) {
        orig.removeMenuDragMouseListener(l);
    }

    public void removeMenuKeyListener(MenuKeyListener l) {
        orig.removeMenuKeyListener(l);
    }

    public void setAccelerator(KeyStroke keyStroke) {
        if (orig != null)
            orig.setAccelerator(keyStroke);
    }

    public void setArmed(boolean b) {
        if (orig != null)
            orig.setArmed(b);
    }

    public void setUI(MenuItemUI ui) {
        if (orig != null)
            orig.setUI(ui);
    }

    public boolean action(Event evt, Object what) {
        return forced.action(evt, what);
    }

    public Component add(Component comp) {
        return forced.add(comp);
    }

    public void add(Component comp, Object constraints) {
        forced.add(comp, constraints);
    }

    public void add(Component comp, Object constraints, int index) {
        forced.add(comp, constraints, index);
    }

    public Component add(Component comp, int index) {
        return forced.add(comp, index);
    }

    public Component add(String name, Component comp) {
        return forced.add(name, comp);
    }

    public void add(PopupMenu popup) {
        forced.add(popup);
    }

    public void addActionListener(ActionListener l) {
        forced.addActionListener(l);
    }

    public void addAncestorListener(AncestorListener listener) {
        forced.addAncestorListener(listener);
    }

    public void addChangeListener(ChangeListener l) {
        forced.addChangeListener(l);
    }

    public void addComponentListener(ComponentListener l) {
        forced.addComponentListener(l);
    }

    public void addContainerListener(ContainerListener l) {
        forced.addContainerListener(l);
    }

    public void addFocusListener(FocusListener l) {
        if (forced != null)
            forced.addFocusListener(l);
    }

    public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        forced.addHierarchyBoundsListener(l);
    }

    public void addHierarchyListener(HierarchyListener l) {
        forced.addHierarchyListener(l);
    }

    public void addInputMethodListener(InputMethodListener l) {
        forced.addInputMethodListener(l);
    }

    public void addItemListener(ItemListener l) {
        forced.addItemListener(l);
    }

    public void addKeyListener(KeyListener l) {
        forced.addKeyListener(l);
    }

    public void addMouseListener(MouseListener l) {
        forced.addMouseListener(l);
    }

    public void addMouseMotionListener(MouseMotionListener l) {
        forced.addMouseMotionListener(l);
    }

    public void addMouseWheelListener(MouseWheelListener l) {
        forced.addMouseWheelListener(l);
    }

    public void addNotify() {
        forced.addNotify();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        forced.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        forced.addPropertyChangeListener(propertyName, listener);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        forced.addVetoableChangeListener(listener);
    }

    public void applyComponentOrientation(ComponentOrientation orientation) {
        forced.applyComponentOrientation(orientation);
    }

    public boolean areFocusTraversalKeysSet(int id) {
        return forced.areFocusTraversalKeysSet(id);
    }

    public Rectangle bounds() {
        return forced.bounds();
    }

    public int checkImage(Image image, ImageObserver observer) {
        return forced.checkImage(image, observer);
    }

    public int checkImage(Image image, int width, int height,
            ImageObserver observer) {
        return forced.checkImage(image, width, height, observer);
    }

    public void computeVisibleRect(Rectangle visibleRect) {
        forced.computeVisibleRect(visibleRect);
    }

    public boolean contains(Point p) {
        return forced.contains(p);
    }

    public boolean contains(int x, int y) {
        return forced.contains(x, y);
    }

    public int countComponents() {
        return forced.countComponents();
    }

    public Image createImage(ImageProducer producer) {
        return forced.createImage(producer);
    }

    public Image createImage(int width, int height) {
        return forced.createImage(width, height);
    }

    public JToolTip createToolTip() {
        return forced.createToolTip();
    }

    public VolatileImage createVolatileImage(int width, int height) {
        return forced.createVolatileImage(width, height);
    }

    public VolatileImage createVolatileImage(int width, int height,
            ImageCapabilities caps) throws AWTException {
        return forced.createVolatileImage(width, height, caps);
    }

    public void deliverEvent(Event e) {
        forced.deliverEvent(e);
    }

    public void disable() {
        forced.disable();
    }

    public void doClick() {
        forced.doClick();
    }

    public void doClick(int pressTime) {
        forced.doClick(pressTime);
    }

    public void doLayout() {
        forced.doLayout();
    }

    public void enable() {
        forced.enable();
    }

    public void enable(boolean b) {
        forced.enable(b);
    }

    public void enableInputMethods(boolean enable) {
        forced.enableInputMethods(enable);
    }

    public Component findComponentAt(Point p) {
        return forced.findComponentAt(p);
    }

    public Component findComponentAt(int x, int y) {
        return forced.findComponentAt(x, y);
    }

    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName,
            int oldValue, int newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        forced.firePropertyChange(propertyName, oldValue, newValue);
    }

    public AccessibleContext getAccessibleContext() {
        return forced.getAccessibleContext();
    }

    public Action getAction() {
        return forced.getAction();
    }

    public String getActionCommand() {
        return forced.getActionCommand();
    }

    public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
        return forced.getActionForKeyStroke(aKeyStroke);
    }

    public ActionListener[] getActionListeners() {
        return forced.getActionListeners();
    }

    public float getAlignmentX() {
        return forced.getAlignmentX();
    }

    public float getAlignmentY() {
        return forced.getAlignmentY();
    }

    public AncestorListener[] getAncestorListeners() {
        return forced.getAncestorListeners();
    }

    public boolean getAutoscrolls() {
        return forced.getAutoscrolls();
    }

    public Color getBackground() {
        return forced.getBackground();
    }

    public Border getBorder() {
        return forced.getBorder();
    }

    public Rectangle getBounds() {
        return forced.getBounds();
    }

    public Rectangle getBounds(Rectangle rv) {
        return forced.getBounds(rv);
    }

    public ChangeListener[] getChangeListeners() {
        return forced.getChangeListeners();
    }

    public ColorModel getColorModel() {
        return forced.getColorModel();
    }

    public Component getComponent(int n) {
        return forced.getComponent(n);
    }

    public Component getComponentAt(Point p) {
        return forced.getComponentAt(p);
    }

    public Component getComponentAt(int x, int y) {
        return forced.getComponentAt(x, y);
    }

    public int getComponentCount() {
        return forced.getComponentCount();
    }

    public ComponentListener[] getComponentListeners() {
        return forced.getComponentListeners();
    }

    public ComponentOrientation getComponentOrientation() {
        return forced.getComponentOrientation();
    }

    public Component[] getComponents() {
        return forced.getComponents();
    }

    public int getConditionForKeyStroke(KeyStroke aKeyStroke) {
        return forced.getConditionForKeyStroke(aKeyStroke);
    }

    public ContainerListener[] getContainerListeners() {
        return forced.getContainerListeners();
    }

    public Cursor getCursor() {
        return forced.getCursor();
    }

    public int getDebugGraphicsOptions() {
        return forced.getDebugGraphicsOptions();
    }

    public Icon getDisabledIcon() {
        return forced.getDisabledIcon();
    }

    public Icon getDisabledSelectedIcon() {
        return forced.getDisabledSelectedIcon();
    }

    public int getDisplayedMnemonicIndex() {
        return forced.getDisplayedMnemonicIndex();
    }

    public DropTarget getDropTarget() {
        return forced.getDropTarget();
    }

    public Container getFocusCycleRootAncestor() {
        return forced.getFocusCycleRootAncestor();
    }

    public FocusListener[] getFocusListeners() {
        return forced.getFocusListeners();
    }

    public Set getFocusTraversalKeys(int id) {
        return forced.getFocusTraversalKeys(id);
    }

    public boolean getFocusTraversalKeysEnabled() {
        return forced.getFocusTraversalKeysEnabled();
    }

    public FocusTraversalPolicy getFocusTraversalPolicy() {
        return forced.getFocusTraversalPolicy();
    }

    public Font getFont() {
        return forced.getFont();
    }

    public FontMetrics getFontMetrics(Font font) {
        return forced.getFontMetrics(font);
    }

    public Color getForeground() {
        return forced.getForeground();
    }

    public Graphics getGraphics() {
        return forced.getGraphics();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return forced.getGraphicsConfiguration();
    }

    public int getHeight() {
        return forced.getHeight();
    }

    public HierarchyBoundsListener[] getHierarchyBoundsListeners() {
        return forced.getHierarchyBoundsListeners();
    }

    public HierarchyListener[] getHierarchyListeners() {
        return forced.getHierarchyListeners();
    }

    public int getHorizontalAlignment() {
        return forced.getHorizontalAlignment();
    }

    public int getHorizontalTextPosition() {
        return forced.getHorizontalTextPosition();
    }

    public Icon getIcon() {
        return forced.getIcon();
    }

    public int getIconTextGap() {
        return forced.getIconTextGap();
    }

    public boolean getIgnoreRepaint() {
        return forced.getIgnoreRepaint();
    }

    public InputContext getInputContext() {
        return forced.getInputContext();
    }

    public InputMethodListener[] getInputMethodListeners() {
        return forced.getInputMethodListeners();
    }

    public InputMethodRequests getInputMethodRequests() {
        return forced.getInputMethodRequests();
    }

    public InputVerifier getInputVerifier() {
        return forced.getInputVerifier();
    }

    public Insets getInsets() {
        return forced.getInsets();
    }

    public Insets getInsets(Insets insets) {
        return forced.getInsets(insets);
    }

    public ItemListener[] getItemListeners() {
        return forced.getItemListeners();
    }

    public KeyListener[] getKeyListeners() {
        return forced.getKeyListeners();
    }

    public String getLabel() {
        return forced.getLabel();
    }

    public LayoutManager getLayout() {
        return forced.getLayout();
    }

    public EventListener[] getListeners(Class listenerType) {
        return forced.getListeners(listenerType);
    }

    public Locale getLocale() {
        return forced.getLocale();
    }

    public Point getLocation() {
        return forced.getLocation();
    }

    public Point getLocation(Point rv) {
        return forced.getLocation(rv);
    }

    public Point getLocationOnScreen() {
        return forced.getLocationOnScreen();
    }

    public Insets getMargin() {
        return forced.getMargin();
    }

    public Dimension getMaximumSize() {
        return forced.getMaximumSize();
    }

    public Dimension getMinimumSize() {
        return forced.getMinimumSize();
    }

    public int getMnemonic() {
        return forced.getMnemonic();
    }

    public ButtonModel getModel() {
        return forced.getModel();
    }

    public MouseListener[] getMouseListeners() {
        return forced.getMouseListeners();
    }

    public MouseMotionListener[] getMouseMotionListeners() {
        return forced.getMouseMotionListeners();
    }

    public MouseWheelListener[] getMouseWheelListeners() {
        return forced.getMouseWheelListeners();
    }

    public long getMultiClickThreshhold() {
        return forced.getMultiClickThreshhold();
    }

    public String getName() {
        return forced.getName();
    }

    public Component getNextFocusableComponent() {
        return forced.getNextFocusableComponent();
    }

    public Container getParent() {
        return forced.getParent();
    }

    public ComponentPeer getPeer() {
        return forced.getPeer();
    }

    public Dimension getPreferredSize() {
        return forced.getPreferredSize();
    }

    public Icon getPressedIcon() {
        return forced.getPressedIcon();
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return forced.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return forced.getPropertyChangeListeners(propertyName);
    }

    public KeyStroke[] getRegisteredKeyStrokes() {
        return forced.getRegisteredKeyStrokes();
    }

    public Icon getRolloverIcon() {
        return forced.getRolloverIcon();
    }

    public Icon getRolloverSelectedIcon() {
        return forced.getRolloverSelectedIcon();
    }

    public JRootPane getRootPane() {
        return forced.getRootPane();
    }

    public Icon getSelectedIcon() {
        return forced.getSelectedIcon();
    }

    public Object[] getSelectedObjects() {
        return forced.getSelectedObjects();
    }

    public Dimension getSize() {
        return forced.getSize();
    }

    public Dimension getSize(Dimension rv) {
        return forced.getSize(rv);
    }

    public String getText() {
        return forced.getText();
    }

    public Toolkit getToolkit() {
        return forced.getToolkit();
    }

    public Point getToolTipLocation(MouseEvent event) {
        return forced.getToolTipLocation(event);
    }

    public String getToolTipText() {
        return forced.getToolTipText();
    }

    public String getToolTipText(MouseEvent event) {
        return forced.getToolTipText(event);
    }

    public Container getTopLevelAncestor() {
        return forced.getTopLevelAncestor();
    }

    public TransferHandler getTransferHandler() {
        return forced.getTransferHandler();
    }

    public ButtonUI getUI() {
        return forced.getUI();
    }

    public String getUIClassID() {
        return forced.getUIClassID();
    }

    public boolean getVerifyInputWhenFocusTarget() {
        return forced.getVerifyInputWhenFocusTarget();
    }

    public int getVerticalAlignment() {
        return forced.getVerticalAlignment();
    }

    public int getVerticalTextPosition() {
        return forced.getVerticalTextPosition();
    }

    public VetoableChangeListener[] getVetoableChangeListeners() {
        return forced.getVetoableChangeListeners();
    }

    public Rectangle getVisibleRect() {
        return forced.getVisibleRect();
    }

    public int getWidth() {
        return forced.getWidth();
    }

    public int getX() {
        return forced.getX();
    }

    public int getY() {
        return forced.getY();
    }

    public boolean gotFocus(Event evt, Object what) {
        return forced.gotFocus(evt, what);
    }

    public void grabFocus() {
        forced.grabFocus();
    }

    public boolean handleEvent(Event evt) {
        return forced.handleEvent(evt);
    }

    public boolean hasFocus() {
        return forced.hasFocus();
    }

    public void hide() {
        forced.hide();
    }

    public boolean imageUpdate(Image img, int infoflags,
            int x, int y, int width, int height) {
        return forced.imageUpdate(img, infoflags, x, y, width, height);
    }

    public Insets insets() {
        return forced.insets();
    }

    public boolean inside(int x, int y) {
        return forced.inside(x, y);
    }

    public void invalidate() {
        forced.invalidate();
    }

    public boolean isAncestorOf(Component c) {
        return forced.isAncestorOf(c);
    }

    public boolean isBackgroundSet() {
        return forced.isBackgroundSet();
    }

    public boolean isBorderPainted() {
        return forced.isBorderPainted();
    }

    public boolean isContentAreaFilled() {
        return forced.isContentAreaFilled();
    }

    public boolean isCursorSet() {
        return forced.isCursorSet();
    }

    public boolean isDisplayable() {
        return forced.isDisplayable();
    }

    public boolean isDoubleBuffered() {
        return forced.isDoubleBuffered();
    }

    public boolean isEnabled() {
        return forced.isEnabled();
    }

    public boolean isFocusable() {
        return forced.isFocusable();
    }

    public boolean isFocusCycleRoot() {
        return forced.isFocusCycleRoot();
    }

    public boolean isFocusCycleRoot(Container container) {
        return forced.isFocusCycleRoot(container);
    }

    public boolean isFocusOwner() {
        return forced.isFocusOwner();
    }

    public boolean isFocusPainted() {
        return forced.isFocusPainted();
    }

    public boolean isFocusTraversable() {
        return forced.isFocusTraversable();
    }

    public boolean isFocusTraversalPolicySet() {
        return forced.isFocusTraversalPolicySet();
    }

    public boolean isFontSet() {
        return forced.isFontSet();
    }

    public boolean isForegroundSet() {
        return forced.isForegroundSet();
    }

    public boolean isLightweight() {
        return forced.isLightweight();
    }

    public boolean isManagingFocus() {
        if (forced != null)
            return forced.isManagingFocus();
        else
            return false;
    }

    public boolean isMaximumSizeSet() {
        return forced.isMaximumSizeSet();
    }

    public boolean isMinimumSizeSet() {
        return forced.isMinimumSizeSet();
    }

    public boolean isOpaque() {
        return forced.isOpaque();
    }

    public boolean isOptimizedDrawingEnabled() {
        return forced.isOptimizedDrawingEnabled();
    }

    public boolean isPaintingTile() {
        return forced.isPaintingTile();
    }

    public boolean isPreferredSizeSet() {
        return forced.isPreferredSizeSet();
    }

    public boolean isRequestFocusEnabled() {
        return forced.isRequestFocusEnabled();
    }

    public boolean isRolloverEnabled() {
        return forced.isRolloverEnabled();
    }

    public boolean isSelected() {
        return forced.isSelected();
    }

    public boolean isShowing() {
        return forced.isShowing();
    }

    public boolean isValid() {
        return forced.isValid();
    }

    public boolean isValidateRoot() {
        return forced.isValidateRoot();
    }

    public boolean isVisible() {
        return forced.isVisible();
    }

    public boolean keyDown(Event evt, int key) {
        return forced.keyDown(evt, key);
    }

    public boolean keyUp(Event evt, int key) {
        return forced.keyUp(evt, key);
    }

    public void layout() {
        forced.layout();
    }

    public void list() {
        forced.list();
    }

    public void list(PrintStream out) {
        forced.list(out);
    }

    public void list(PrintStream out, int indent) {
        forced.list(out, indent);
    }

    public void list(PrintWriter out) {
        forced.list(out);
    }

    public void list(PrintWriter out, int indent) {
        forced.list(out, indent);
    }

    public Component locate(int x, int y) {
        return forced.locate(x, y);
    }

    public Point location() {
        return forced.location();
    }

    public boolean lostFocus(Event evt, Object what) {
        return forced.lostFocus(evt, what);
    }

    public Dimension minimumSize() {
        return forced.minimumSize();
    }

    public boolean mouseDown(Event evt, int x, int y) {
        return forced.mouseDown(evt, x, y);
    }

    public boolean mouseDrag(Event evt, int x, int y) {
        return forced.mouseDrag(evt, x, y);
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        return forced.mouseEnter(evt, x, y);
    }

    public boolean mouseExit(Event evt, int x, int y) {
        return forced.mouseExit(evt, x, y);
    }

    public boolean mouseMove(Event evt, int x, int y) {
        return forced.mouseMove(evt, x, y);
    }

    public boolean mouseUp(Event evt, int x, int y) {
        return forced.mouseUp(evt, x, y);
    }

    public void move(int x, int y) {
        forced.move(x, y);
    }

    public void nextFocus() {
        forced.nextFocus();
    }

    public void paint(Graphics g) {
        forced.paint(g);
    }

    public void paintAll(Graphics g) {
        forced.paintAll(g);
    }

    public void paintComponents(Graphics g) {
        forced.paintComponents(g);
    }

    public void paintImmediately(Rectangle r) {
        forced.paintImmediately(r);
    }

    public void paintImmediately(int x, int y, int w, int h) {
        forced.paintImmediately(x, y, w, h);
    }

    public boolean postEvent(Event evt) {
        return forced.postEvent(evt);
    }

    public Dimension preferredSize() {
        return forced.preferredSize();
    }

    public boolean prepareImage(Image image, ImageObserver observer) {
        return forced.prepareImage(image, observer);
    }

    public boolean prepareImage(Image image, int width, int height,
            ImageObserver observer) {
        return forced.prepareImage(image, width, height, observer);
    }

    public void print(Graphics g) {
        forced.print(g);
    }

    public void printAll(Graphics g) {
        forced.printAll(g);
    }

    public void registerKeyboardAction(ActionListener anAction, String aCommand, KeyStroke aKeyStroke, int aCondition) {
        forced.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    }

    public void registerKeyboardAction(ActionListener anAction, KeyStroke aKeyStroke, int aCondition) {
        forced.registerKeyboardAction(anAction, aKeyStroke, aCondition);
    }

    public void remove(Component comp) {
        forced.remove(comp);
    }

    public void remove(int index) {
        forced.remove(index);
    }

    public void removeActionListener(ActionListener l) {
        forced.removeActionListener(l);
    }

    public void removeAll() {
        forced.removeAll();
    }

    public void removeAncestorListener(AncestorListener listener) {
        forced.removeAncestorListener(listener);
    }

    public void removeChangeListener(ChangeListener l) {
        forced.removeChangeListener(l);
    }

    public void removeComponentListener(ComponentListener l) {
        forced.removeComponentListener(l);
    }

    public void removeContainerListener(ContainerListener l) {
        forced.removeContainerListener(l);
    }

    public void removeFocusListener(FocusListener l) {
        forced.removeFocusListener(l);
    }

    public void removeHierarchyBoundsListener(HierarchyBoundsListener l) {
        forced.removeHierarchyBoundsListener(l);
    }

    public void removeHierarchyListener(HierarchyListener l) {
        forced.removeHierarchyListener(l);
    }

    public void removeInputMethodListener(InputMethodListener l) {
        forced.removeInputMethodListener(l);
    }

    public void removeItemListener(ItemListener l) {
        forced.removeItemListener(l);
    }

    public void removeKeyListener(KeyListener l) {
        forced.removeKeyListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        forced.removeMouseListener(l);
    }

    public void removeMouseMotionListener(MouseMotionListener l) {
        forced.removeMouseMotionListener(l);
    }

    public void removeMouseWheelListener(MouseWheelListener l) {
        forced.removeMouseWheelListener(l);
    }

    public void removeNotify() {
        forced.removeNotify();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        forced.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        forced.removePropertyChangeListener(propertyName, listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        forced.removeVetoableChangeListener(listener);
    }

    public void repaint() {
        forced.repaint();
    }

    public void repaint(Rectangle r) {
        forced.repaint(r);
    }

    public void repaint(long tm) {
        forced.repaint(tm);
    }

    public void repaint(long tm, int x, int y, int width, int height) {
        forced.repaint(tm, x, y, width, height);
    }

    public void repaint(int x, int y, int width, int height) {
        forced.repaint(x, y, width, height);
    }

    public boolean requestDefaultFocus() {
        return forced.requestDefaultFocus();
    }

    public void requestFocus() {
        forced.requestFocus();
    }

    public boolean requestFocus(boolean temporary) {
        return forced.requestFocus(temporary);
    }

    public boolean requestFocusInWindow() {
        return forced.requestFocusInWindow();
    }

    public void resetKeyboardActions() {
        forced.resetKeyboardActions();
    }

    public void reshape(int x, int y, int width, int height) {
        forced.reshape(x, y, width, height);
    }

    public void resize(Dimension d) {
        forced.resize(d);
    }

    public void resize(int width, int height) {
        forced.resize(width, height);
    }

    public void revalidate() {
        forced.revalidate();
    }

    public void scrollRectToVisible(Rectangle aRect) {
        forced.scrollRectToVisible(aRect);
    }

    public void setAction(Action a) {
        if (forced != null) forced.setAction(a);
    }

    public void setActionCommand(String actionCommand) {
        if (forced != null) forced.setActionCommand(actionCommand);
    }

    public void setAlignmentX(float alignmentX) {
        if (forced != null) forced.setAlignmentX(alignmentX);
    }

    public void setAlignmentY(float alignmentY) {
        if (forced != null) forced.setAlignmentY(alignmentY);
    }

    public void setAutoscrolls(boolean autoscrolls) {
        if (forced != null) forced.setAutoscrolls(autoscrolls);
    }

    public void setBackground(Color bg) {
        if (forced != null) forced.setBackground(bg);
    }

    public void setBorder(Border border) {
        if (forced != null) forced.setBorder(border);
    }

    public void setBorderPainted(boolean b) {
        if (forced != null) forced.setBorderPainted(b);
    }

    public void setBounds(Rectangle r) {
        if (forced != null) forced.setBounds(r);
    }

    public void setBounds(int x, int y, int width, int height) {
        if (forced != null) forced.setBounds(x, y, width, height);
    }

    public void setComponentOrientation(ComponentOrientation o) {
        if (forced != null) forced.setComponentOrientation(o);
    }

    public void setContentAreaFilled(boolean b) {
        if (forced != null) forced.setContentAreaFilled(b);
    }

    public void setCursor(Cursor cursor) {
        if (forced != null) forced.setCursor(cursor);
    }

    public void setDebugGraphicsOptions(int debugOptions) {
        if (forced != null) forced.setDebugGraphicsOptions(debugOptions);
    }

    public void setDisabledIcon(Icon disabledIcon) {
        if (forced != null) forced.setDisabledIcon(disabledIcon);
    }

    public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
        if (forced != null) forced.setDisabledSelectedIcon(disabledSelectedIcon);
    }

    public void setDisplayedMnemonicIndex(int index)
            throws IllegalArgumentException {
        if (forced != null) forced.setDisplayedMnemonicIndex(index);
    }

    public void setDoubleBuffered(boolean aFlag) {
        if (forced != null) forced.setDoubleBuffered(aFlag);
    }

    public void setDropTarget(DropTarget dt) {
        if (forced != null) forced.setDropTarget(dt);
    }

    public void setEnabled(boolean enabled) {
        if (forced != null) forced.setEnabled(enabled);
    }

    public void setFocusable(boolean focusable) {
        if (forced != null) forced.setFocusable(focusable);
    }

    public void setFocusCycleRoot(boolean focusCycleRoot) {
        if (forced != null) forced.setFocusCycleRoot(focusCycleRoot);
    }

    public void setFocusPainted(boolean b) {
        if (forced != null) forced.setFocusPainted(b);
    }

    public void setFocusTraversalKeys(int id, Set keystrokes) {
        if (forced != null) forced.setFocusTraversalKeys(id, keystrokes);
    }

    public void setFocusTraversalKeysEnabled(boolean
            focusTraversalKeysEnabled) {
        if (forced != null) forced.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
    }

    public void setFocusTraversalPolicy(FocusTraversalPolicy policy) {
        if (forced != null) forced.setFocusTraversalPolicy(policy);
    }

    public void setFont(Font f) {
        if (forced != null) forced.setFont(f);
    }

    public void setForeground(Color fg) {
        if (forced != null) forced.setForeground(fg);
    }

    public void setHorizontalAlignment(int alignment) {
        if (forced != null) forced.setHorizontalAlignment(alignment);
    }

    public void setHorizontalTextPosition(int textPosition) {
        if (forced != null) forced.setHorizontalTextPosition(textPosition);
    }

    public void setIcon(Icon defaultIcon) {
        if (forced != null) forced.setIcon(defaultIcon);
    }

    public void setIconTextGap(int iconTextGap) {
        if (forced != null) forced.setIconTextGap(iconTextGap);
    }

    public void setIgnoreRepaint(boolean ignoreRepaint) {
        if (forced != null) forced.setIgnoreRepaint(ignoreRepaint);
    }

    public void setInputVerifier(InputVerifier inputVerifier) {
        if (forced != null) forced.setInputVerifier(inputVerifier);
    }

    public void setLabel(String label) {
        if (forced != null) forced.setLabel(label);
    }

    public void setLayout(LayoutManager mgr) {
        if (forced != null) forced.setLayout(mgr);
    }

    public void setLocale(Locale l) {
        if (forced != null) forced.setLocale(l);
    }

    public void setLocation(Point p) {
        if (forced != null) forced.setLocation(p);
    }

    public void setLocation(int x, int y) {
        if (forced != null) forced.setLocation(x, y);
    }

    public void setMargin(Insets m) {
        if (forced != null) forced.setMargin(m);
    }

    public void setMaximumSize(Dimension maximumSize) {
        if (forced != null) forced.setMaximumSize(maximumSize);
    }

    public void setMinimumSize(Dimension minimumSize) {
        if (forced != null) forced.setMinimumSize(minimumSize);
    }

    public void setMnemonic(char mnemonic) {
        if (forced != null) forced.setMnemonic(mnemonic);
    }

    public void setMnemonic(int mnemonic) {
        if (forced != null) forced.setMnemonic(mnemonic);
    }

    public void setModel(ButtonModel newModel) {
        if (forced != null) forced.setModel(newModel);
    }

    public void setMultiClickThreshhold(long threshhold) {
        if (forced != null) forced.setMultiClickThreshhold(threshhold);
    }

    public void setName(String name) {
        if (forced != null) forced.setName(name);
    }

    public void setNextFocusableComponent(Component aComponent) {
        if (forced != null) forced.setNextFocusableComponent(aComponent);
    }

    public void setOpaque(boolean isOpaque) {
        if (forced != null) forced.setOpaque(isOpaque);
    }

    public void setPreferredSize(Dimension preferredSize) {
        if (forced != null) forced.setPreferredSize(preferredSize);
    }

    public void setPressedIcon(Icon pressedIcon) {
        if (forced != null) forced.setPressedIcon(pressedIcon);
    }

    public void setRequestFocusEnabled(boolean requestFocusEnabled) {
        if (forced != null) forced.setRequestFocusEnabled(requestFocusEnabled);
    }

    public void setRolloverEnabled(boolean b) {
        if (forced != null) forced.setRolloverEnabled(b);
    }

    public void setRolloverIcon(Icon rolloverIcon) {
        if (forced != null) forced.setRolloverIcon(rolloverIcon);
    }

    public void setRolloverSelectedIcon(Icon rolloverSelectedIcon) {
        if (forced != null) forced.setRolloverSelectedIcon(rolloverSelectedIcon);
    }

    public void setSelected(boolean b) {
        if (forced != null) forced.setSelected(b);
    }

    public void setSelectedIcon(Icon selectedIcon) {
        if (forced != null) forced.setSelectedIcon(selectedIcon);
    }

    public void setSize(Dimension d) {
        if (forced != null) forced.setSize(d);
    }

    public void setSize(int width, int height) {
        if (forced != null) forced.setSize(width, height);
    }

    public void setText(String text) {
        if (forced != null) forced.setText(text);
    }

    public void setToolTipText(String text) {
        if (forced != null) forced.setToolTipText(text);
    }

    public void setTransferHandler(TransferHandler newHandler) {
        if (forced != null) forced.setTransferHandler(newHandler);
    }

    public void setUI(ComponentUI newUI) {
        if (forced != null) forced.setUI(newUI);
    }

    public void setUI(ButtonUI ui) {
        if (forced != null) forced.setUI(ui);
    }

    public void setVerifyInputWhenFocusTarget(boolean
            verifyInputWhenFocusTarget) {
        if (forced != null) forced.setVerifyInputWhenFocusTarget(verifyInputWhenFocusTarget);
    }

    public void setVerticalAlignment(int alignment) {
        if (forced != null) forced.setVerticalAlignment(alignment);
    }

    public void setVerticalTextPosition(int textPosition) {
        if (forced != null) forced.setVerticalTextPosition(textPosition);
    }

    public void setVisible(boolean b) {
        if (forced != null) forced.setVisible(b);
    }

    public void show() {
        forced.show();
    }

    public void show(boolean b) {
        forced.show(b);
    }

    public Dimension size() {
        return forced.size();
    }

    public String toString() {
        return forced.toString();
    }

    public void transferFocus() {
        forced.transferFocus();
    }

    public void transferFocusBackward() {
        forced.transferFocusBackward();
    }

    public void transferFocusDownCycle() {
        forced.transferFocusDownCycle();
    }

    public void transferFocusUpCycle() {
        forced.transferFocusUpCycle();
    }

    public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
        forced.unregisterKeyboardAction(aKeyStroke);
    }

    public void update(Graphics g) {
        forced.update(g);
    }

    public void updateUI() {
        if (forced != null)
            forced.updateUI();
    }

    public void validate() {
        forced.validate();
    }
}