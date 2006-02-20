// $Id$

package napkin;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.im.*;
import java.awt.image.*;
import java.awt.peer.*;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.io.*;
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
    private final FakeEnabledButton faked;
    private final JMenuItem orig;

    FakeEnabledMenuItem(JComponent c) {
        if (c == null)
            throw new NullPointerException("c");
        orig = (JMenuItem) c;
        faked = new FakeEnabledButton(c);
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

    public void processKeyEvent(KeyEvent e, MenuElement[] path,
            MenuSelectionManager manager) {
        orig.processKeyEvent(e, path, manager);
    }

    public void processMenuDragMouseEvent(MenuDragMouseEvent e) {
        orig.processMenuDragMouseEvent(e);
    }

    public void processMenuKeyEvent(MenuKeyEvent e) {
        orig.processMenuKeyEvent(e);
    }

    public void processMouseEvent(MouseEvent e, MenuElement[] path,
            MenuSelectionManager manager) {
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

    @Deprecated
    public boolean action(Event evt, Object what) {
        return faked.action(evt, what);
    }

    public Component add(Component comp) {
        return faked.add(comp);
    }

    public void add(Component comp, Object constraints) {
        faked.add(comp, constraints);
    }

    public void add(Component comp, Object constraints, int index) {
        faked.add(comp, constraints, index);
    }

    public Component add(Component comp, int index) {
        return faked.add(comp, index);
    }

    public Component add(String name, Component comp) {
        return faked.add(name, comp);
    }

    public void add(PopupMenu popup) {
        faked.add(popup);
    }

    public void addActionListener(ActionListener l) {
        faked.addActionListener(l);
    }

    public void addAncestorListener(AncestorListener listener) {
        faked.addAncestorListener(listener);
    }

    public void addChangeListener(ChangeListener l) {
        faked.addChangeListener(l);
    }

    public void addComponentListener(ComponentListener l) {
        faked.addComponentListener(l);
    }

    public void addContainerListener(ContainerListener l) {
        faked.addContainerListener(l);
    }

    public void addFocusListener(FocusListener l) {
        if (faked != null)
            faked.addFocusListener(l);
    }

    public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        faked.addHierarchyBoundsListener(l);
    }

    public void addHierarchyListener(HierarchyListener l) {
        faked.addHierarchyListener(l);
    }

    public void addInputMethodListener(InputMethodListener l) {
        faked.addInputMethodListener(l);
    }

    public void addItemListener(ItemListener l) {
        faked.addItemListener(l);
    }

    public void addKeyListener(KeyListener l) {
        faked.addKeyListener(l);
    }

    public void addMouseListener(MouseListener l) {
        faked.addMouseListener(l);
    }

    public void addMouseMotionListener(MouseMotionListener l) {
        faked.addMouseMotionListener(l);
    }

    public void addMouseWheelListener(MouseWheelListener l) {
        faked.addMouseWheelListener(l);
    }

    public void addNotify() {
        faked.addNotify();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        faked.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        faked.addPropertyChangeListener(propertyName, listener);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        faked.addVetoableChangeListener(listener);
    }

    public void applyComponentOrientation(ComponentOrientation orientation) {
        faked.applyComponentOrientation(orientation);
    }

    public boolean areFocusTraversalKeysSet(int id) {
        return faked.areFocusTraversalKeysSet(id);
    }

    @Deprecated
    public Rectangle bounds() {
        return faked.bounds();
    }

    public int checkImage(Image image, ImageObserver observer) {
        return faked.checkImage(image, observer);
    }

    public int checkImage(Image image, int width, int height,
            ImageObserver observer) {
        return faked.checkImage(image, width, height, observer);
    }

    public void computeVisibleRect(Rectangle visibleRect) {
        faked.computeVisibleRect(visibleRect);
    }

    public boolean contains(Point p) {
        return faked.contains(p);
    }

    public boolean contains(int x, int y) {
        return faked.contains(x, y);
    }

    @Deprecated
    public int countComponents() {
        return faked.countComponents();
    }

    public Image createImage(ImageProducer producer) {
        return faked.createImage(producer);
    }

    public Image createImage(int width, int height) {
        return faked.createImage(width, height);
    }

    public JToolTip createToolTip() {
        return faked.createToolTip();
    }

    public VolatileImage createVolatileImage(int width, int height) {
        return faked.createVolatileImage(width, height);
    }

    public VolatileImage createVolatileImage(int width, int height,
            ImageCapabilities caps) throws AWTException {
        return faked.createVolatileImage(width, height, caps);
    }

    @Deprecated
    public void deliverEvent(Event e) {
        faked.deliverEvent(e);
    }

    @Deprecated
    public void disable() {
        faked.disable();
    }

    public void doClick() {
        faked.doClick();
    }

    public void doClick(int pressTime) {
        faked.doClick(pressTime);
    }

    public void doLayout() {
        faked.doLayout();
    }

    @Deprecated
    public void enable() {
        faked.enable();
    }

    @Deprecated
    public void enable(boolean b) {
        faked.enable(b);
    }

    public void enableInputMethods(boolean enable) {
        faked.enableInputMethods(enable);
    }

    public Component findComponentAt(Point p) {
        return faked.findComponentAt(p);
    }

    public Component findComponentAt(int x, int y) {
        return faked.findComponentAt(x, y);
    }

    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue,
            byte newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, char oldValue,
            char newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, double oldValue,
            double newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, float oldValue,
            float newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName,
            int oldValue, int newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, long oldValue,
            long newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, short oldValue,
            short newValue) {
        faked.firePropertyChange(propertyName, oldValue, newValue);
    }

    public AccessibleContext getAccessibleContext() {
        return faked.getAccessibleContext();
    }

    public Action getAction() {
        return faked.getAction();
    }

    public String getActionCommand() {
        return faked.getActionCommand();
    }

    public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
        return faked.getActionForKeyStroke(aKeyStroke);
    }

    public ActionListener[] getActionListeners() {
        return faked.getActionListeners();
    }

    public float getAlignmentX() {
        return faked.getAlignmentX();
    }

    public float getAlignmentY() {
        return faked.getAlignmentY();
    }

    public AncestorListener[] getAncestorListeners() {
        return faked.getAncestorListeners();
    }

    public boolean getAutoscrolls() {
        return faked.getAutoscrolls();
    }

    public Color getBackground() {
        return faked.getBackground();
    }

    public Border getBorder() {
        return faked.getBorder();
    }

    public Rectangle getBounds() {
        return faked.getBounds();
    }

    public Rectangle getBounds(Rectangle rv) {
        return faked.getBounds(rv);
    }

    public ChangeListener[] getChangeListeners() {
        return faked.getChangeListeners();
    }

    public ColorModel getColorModel() {
        return faked.getColorModel();
    }

    public Component getComponent(int n) {
        return faked.getComponent(n);
    }

    public Component getComponentAt(Point p) {
        return faked.getComponentAt(p);
    }

    public Component getComponentAt(int x, int y) {
        return faked.getComponentAt(x, y);
    }

    public int getComponentCount() {
        return faked.getComponentCount();
    }

    public ComponentListener[] getComponentListeners() {
        return faked.getComponentListeners();
    }

    public ComponentOrientation getComponentOrientation() {
        return faked.getComponentOrientation();
    }

    public Component[] getComponents() {
        return faked.getComponents();
    }

    public int getConditionForKeyStroke(KeyStroke aKeyStroke) {
        return faked.getConditionForKeyStroke(aKeyStroke);
    }

    public ContainerListener[] getContainerListeners() {
        return faked.getContainerListeners();
    }

    public Cursor getCursor() {
        return faked.getCursor();
    }

    public int getDebugGraphicsOptions() {
        return faked.getDebugGraphicsOptions();
    }

    public Icon getDisabledIcon() {
        return faked.getDisabledIcon();
    }

    public Icon getDisabledSelectedIcon() {
        return faked.getDisabledSelectedIcon();
    }

    public int getDisplayedMnemonicIndex() {
        return faked.getDisplayedMnemonicIndex();
    }

    public DropTarget getDropTarget() {
        return faked.getDropTarget();
    }

    public Container getFocusCycleRootAncestor() {
        return faked.getFocusCycleRootAncestor();
    }

    public FocusListener[] getFocusListeners() {
        return faked.getFocusListeners();
    }

    public Set<AWTKeyStroke> getFocusTraversalKeys(int id) {
        return faked.getFocusTraversalKeys(id);
    }

    public boolean getFocusTraversalKeysEnabled() {
        return faked.getFocusTraversalKeysEnabled();
    }

    public FocusTraversalPolicy getFocusTraversalPolicy() {
        return faked.getFocusTraversalPolicy();
    }

    public Font getFont() {
        return faked.getFont();
    }

    public FontMetrics getFontMetrics(Font font) {
        return faked.getFontMetrics(font);
    }

    public Color getForeground() {
        return faked.getForeground();
    }

    public Graphics getGraphics() {
        return faked.getGraphics();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return faked.getGraphicsConfiguration();
    }

    public int getHeight() {
        return faked.getHeight();
    }

    public HierarchyBoundsListener[] getHierarchyBoundsListeners() {
        return faked.getHierarchyBoundsListeners();
    }

    public HierarchyListener[] getHierarchyListeners() {
        return faked.getHierarchyListeners();
    }

    public int getHorizontalAlignment() {
        return faked.getHorizontalAlignment();
    }

    public int getHorizontalTextPosition() {
        return faked.getHorizontalTextPosition();
    }

    public Icon getIcon() {
        return faked.getIcon();
    }

    public int getIconTextGap() {
        return faked.getIconTextGap();
    }

    public boolean getIgnoreRepaint() {
        return faked.getIgnoreRepaint();
    }

    public InputContext getInputContext() {
        return faked.getInputContext();
    }

    public InputMethodListener[] getInputMethodListeners() {
        return faked.getInputMethodListeners();
    }

    public InputMethodRequests getInputMethodRequests() {
        return faked.getInputMethodRequests();
    }

    public InputVerifier getInputVerifier() {
        return faked.getInputVerifier();
    }

    public Insets getInsets() {
        return faked.getInsets();
    }

    public Insets getInsets(Insets insets) {
        return faked.getInsets(insets);
    }

    public ItemListener[] getItemListeners() {
        return faked.getItemListeners();
    }

    public KeyListener[] getKeyListeners() {
        return faked.getKeyListeners();
    }

    @Deprecated
    public String getLabel() {
        return faked.getLabel();
    }

    public LayoutManager getLayout() {
        return faked.getLayout();
    }

    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return faked.getListeners(listenerType);
    }

    public Locale getLocale() {
        return faked.getLocale();
    }

    public Point getLocation() {
        return faked.getLocation();
    }

    public Point getLocation(Point rv) {
        return faked.getLocation(rv);
    }

    public Point getLocationOnScreen() {
        return faked.getLocationOnScreen();
    }

    public Insets getMargin() {
        return faked.getMargin();
    }

    public Dimension getMaximumSize() {
        return faked.getMaximumSize();
    }

    public Dimension getMinimumSize() {
        return faked.getMinimumSize();
    }

    public int getMnemonic() {
        return faked.getMnemonic();
    }

    public ButtonModel getModel() {
        return faked.getModel();
    }

    public MouseListener[] getMouseListeners() {
        return faked.getMouseListeners();
    }

    public MouseMotionListener[] getMouseMotionListeners() {
        return faked.getMouseMotionListeners();
    }

    public MouseWheelListener[] getMouseWheelListeners() {
        return faked.getMouseWheelListeners();
    }

    public long getMultiClickThreshhold() {
        return faked.getMultiClickThreshhold();
    }

    public String getName() {
        return faked.getName();
    }

    @Deprecated
    public Component getNextFocusableComponent() {
        return faked.getNextFocusableComponent();
    }

    public Container getParent() {
        return faked.getParent();
    }

    @Deprecated
    public ComponentPeer getPeer() {
        return faked.getPeer();
    }

    public Dimension getPreferredSize() {
        return faked.getPreferredSize();
    }

    public Icon getPressedIcon() {
        return faked.getPressedIcon();
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return faked.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(
            String propertyName) {
        return faked.getPropertyChangeListeners(propertyName);
    }

    public KeyStroke[] getRegisteredKeyStrokes() {
        return faked.getRegisteredKeyStrokes();
    }

    public Icon getRolloverIcon() {
        return faked.getRolloverIcon();
    }

    public Icon getRolloverSelectedIcon() {
        return faked.getRolloverSelectedIcon();
    }

    public JRootPane getRootPane() {
        return faked.getRootPane();
    }

    public Icon getSelectedIcon() {
        return faked.getSelectedIcon();
    }

    public Object[] getSelectedObjects() {
        return faked.getSelectedObjects();
    }

    public Dimension getSize() {
        return faked.getSize();
    }

    public Dimension getSize(Dimension rv) {
        return faked.getSize(rv);
    }

    public String getText() {
        return faked.getText();
    }

    public Toolkit getToolkit() {
        return faked.getToolkit();
    }

    public Point getToolTipLocation(MouseEvent event) {
        return faked.getToolTipLocation(event);
    }

    public String getToolTipText() {
        return faked.getToolTipText();
    }

    public String getToolTipText(MouseEvent event) {
        return faked.getToolTipText(event);
    }

    public Container getTopLevelAncestor() {
        return faked.getTopLevelAncestor();
    }

    public TransferHandler getTransferHandler() {
        return faked.getTransferHandler();
    }

    public ButtonUI getUI() {
        return faked.getUI();
    }

    public String getUIClassID() {
        return faked.getUIClassID();
    }

    public boolean getVerifyInputWhenFocusTarget() {
        return faked.getVerifyInputWhenFocusTarget();
    }

    public int getVerticalAlignment() {
        return faked.getVerticalAlignment();
    }

    public int getVerticalTextPosition() {
        return faked.getVerticalTextPosition();
    }

    public VetoableChangeListener[] getVetoableChangeListeners() {
        return faked.getVetoableChangeListeners();
    }

    public Rectangle getVisibleRect() {
        return faked.getVisibleRect();
    }

    public int getWidth() {
        return faked.getWidth();
    }

    public int getX() {
        return faked.getX();
    }

    public int getY() {
        return faked.getY();
    }

    @Deprecated
    public boolean gotFocus(Event evt, Object what) {
        return faked.gotFocus(evt, what);
    }

    public void grabFocus() {
        faked.grabFocus();
    }

    @Deprecated
    public boolean handleEvent(Event evt) {
        return faked.handleEvent(evt);
    }

    public boolean hasFocus() {
        return faked.hasFocus();
    }

    @Deprecated
    public void hide() {
        faked.hide();
    }

    public boolean imageUpdate(Image img, int infoflags,
            int x, int y, int width, int height) {
        return faked.imageUpdate(img, infoflags, x, y, width, height);
    }

    @Deprecated
    public Insets insets() {
        return faked.insets();
    }

    @Deprecated
    public boolean inside(int x, int y) {
        return faked.inside(x, y);
    }

    public void invalidate() {
        faked.invalidate();
    }

    public boolean isAncestorOf(Component c) {
        return faked.isAncestorOf(c);
    }

    public boolean isBackgroundSet() {
        return faked.isBackgroundSet();
    }

    public boolean isBorderPainted() {
        return faked.isBorderPainted();
    }

    public boolean isContentAreaFilled() {
        return faked.isContentAreaFilled();
    }

    public boolean isCursorSet() {
        return faked.isCursorSet();
    }

    public boolean isDisplayable() {
        return faked.isDisplayable();
    }

    public boolean isDoubleBuffered() {
        return faked.isDoubleBuffered();
    }

    public boolean isEnabled() {
        return faked.isEnabled();
    }

    public boolean isFocusable() {
        return faked.isFocusable();
    }

    public boolean isFocusCycleRoot() {
        return faked.isFocusCycleRoot();
    }

    public boolean isFocusCycleRoot(Container container) {
        return faked.isFocusCycleRoot(container);
    }

    public boolean isFocusOwner() {
        return faked.isFocusOwner();
    }

    public boolean isFocusPainted() {
        return faked.isFocusPainted();
    }

    @Deprecated
    public boolean isFocusTraversable() {
        return faked.isFocusTraversable();
    }

    public boolean isFocusTraversalPolicySet() {
        return faked.isFocusTraversalPolicySet();
    }

    public boolean isFontSet() {
        return faked.isFontSet();
    }

    public boolean isForegroundSet() {
        return faked.isForegroundSet();
    }

    public boolean isLightweight() {
        return faked.isLightweight();
    }

    @Deprecated
    public boolean isManagingFocus() {
        if (faked != null)
            return faked.isManagingFocus();
        else
            return false;
    }

    public boolean isMaximumSizeSet() {
        return faked.isMaximumSizeSet();
    }

    public boolean isMinimumSizeSet() {
        return faked.isMinimumSizeSet();
    }

    public boolean isOpaque() {
        return faked.isOpaque();
    }

    public boolean isOptimizedDrawingEnabled() {
        return faked.isOptimizedDrawingEnabled();
    }

    public boolean isPaintingTile() {
        return faked.isPaintingTile();
    }

    public boolean isPreferredSizeSet() {
        return faked.isPreferredSizeSet();
    }

    public boolean isRequestFocusEnabled() {
        return faked.isRequestFocusEnabled();
    }

    public boolean isRolloverEnabled() {
        return faked.isRolloverEnabled();
    }

    public boolean isSelected() {
        return faked.isSelected();
    }

    public boolean isShowing() {
        return faked.isShowing();
    }

    public boolean isValid() {
        return faked.isValid();
    }

    public boolean isValidateRoot() {
        return faked.isValidateRoot();
    }

    public boolean isVisible() {
        return faked.isVisible();
    }

    @Deprecated
    public boolean keyDown(Event evt, int key) {
        return faked.keyDown(evt, key);
    }

    @Deprecated
    public boolean keyUp(Event evt, int key) {
        return faked.keyUp(evt, key);
    }

    @Deprecated
    public void layout() {
        faked.layout();
    }

    public void list() {
        faked.list();
    }

    public void list(PrintStream out) {
        faked.list(out);
    }

    public void list(PrintStream out, int indent) {
        faked.list(out, indent);
    }

    public void list(PrintWriter out) {
        faked.list(out);
    }

    public void list(PrintWriter out, int indent) {
        faked.list(out, indent);
    }

    @Deprecated
    public Component locate(int x, int y) {
        return faked.locate(x, y);
    }

    @Deprecated
    public Point location() {
        return faked.location();
    }

    @Deprecated
    public boolean lostFocus(Event evt, Object what) {
        return faked.lostFocus(evt, what);
    }

    @Deprecated
    public Dimension minimumSize() {
        return faked.minimumSize();
    }

    @Deprecated
    public boolean mouseDown(Event evt, int x, int y) {
        return faked.mouseDown(evt, x, y);
    }

    @Deprecated
    public boolean mouseDrag(Event evt, int x, int y) {
        return faked.mouseDrag(evt, x, y);
    }

    @Deprecated
    public boolean mouseEnter(Event evt, int x, int y) {
        return faked.mouseEnter(evt, x, y);
    }

    @Deprecated
    public boolean mouseExit(Event evt, int x, int y) {
        return faked.mouseExit(evt, x, y);
    }

    @Deprecated
    public boolean mouseMove(Event evt, int x, int y) {
        return faked.mouseMove(evt, x, y);
    }

    @Deprecated
    public boolean mouseUp(Event evt, int x, int y) {
        return faked.mouseUp(evt, x, y);
    }

    @Deprecated
    public void move(int x, int y) {
        faked.move(x, y);
    }

    @Deprecated
    public void nextFocus() {
        faked.nextFocus();
    }

    public void paint(Graphics g) {
        faked.paint(g);
    }

    public void paintAll(Graphics g) {
        faked.paintAll(g);
    }

    public void paintComponents(Graphics g) {
        faked.paintComponents(g);
    }

    public void paintImmediately(Rectangle r) {
        faked.paintImmediately(r);
    }

    public void paintImmediately(int x, int y, int w, int h) {
        faked.paintImmediately(x, y, w, h);
    }

    @Deprecated
    public boolean postEvent(Event evt) {
        return faked.postEvent(evt);
    }

    @Deprecated
    public Dimension preferredSize() {
        return faked.preferredSize();
    }

    public boolean prepareImage(Image image, ImageObserver observer) {
        return faked.prepareImage(image, observer);
    }

    public boolean prepareImage(Image image, int width, int height,
            ImageObserver observer) {
        return faked.prepareImage(image, width, height, observer);
    }

    public void print(Graphics g) {
        faked.print(g);
    }

    public void printAll(Graphics g) {
        faked.printAll(g);
    }

    public void registerKeyboardAction(ActionListener anAction,
            String aCommand, KeyStroke aKeyStroke, int aCondition) {
        faked.registerKeyboardAction(anAction, aCommand, aKeyStroke,
                aCondition);
    }

    public void registerKeyboardAction(ActionListener anAction,
            KeyStroke aKeyStroke, int aCondition) {
        faked.registerKeyboardAction(anAction, aKeyStroke, aCondition);
    }

    public void remove(Component comp) {
        faked.remove(comp);
    }

    public void remove(int index) {
        faked.remove(index);
    }

    public void removeActionListener(ActionListener l) {
        faked.removeActionListener(l);
    }

    public void removeAll() {
        faked.removeAll();
    }

    public void removeAncestorListener(AncestorListener listener) {
        faked.removeAncestorListener(listener);
    }

    public void removeChangeListener(ChangeListener l) {
        faked.removeChangeListener(l);
    }

    public void removeComponentListener(ComponentListener l) {
        faked.removeComponentListener(l);
    }

    public void removeContainerListener(ContainerListener l) {
        faked.removeContainerListener(l);
    }

    public void removeFocusListener(FocusListener l) {
        faked.removeFocusListener(l);
    }

    public void removeHierarchyBoundsListener(HierarchyBoundsListener l) {
        faked.removeHierarchyBoundsListener(l);
    }

    public void removeHierarchyListener(HierarchyListener l) {
        faked.removeHierarchyListener(l);
    }

    public void removeInputMethodListener(InputMethodListener l) {
        faked.removeInputMethodListener(l);
    }

    public void removeItemListener(ItemListener l) {
        faked.removeItemListener(l);
    }

    public void removeKeyListener(KeyListener l) {
        faked.removeKeyListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        faked.removeMouseListener(l);
    }

    public void removeMouseMotionListener(MouseMotionListener l) {
        faked.removeMouseMotionListener(l);
    }

    public void removeMouseWheelListener(MouseWheelListener l) {
        faked.removeMouseWheelListener(l);
    }

    public void removeNotify() {
        faked.removeNotify();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        faked.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        faked.removePropertyChangeListener(propertyName, listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        faked.removeVetoableChangeListener(listener);
    }

    public void repaint() {
        faked.repaint();
    }

    public void repaint(Rectangle r) {
        faked.repaint(r);
    }

    public void repaint(long tm) {
        faked.repaint(tm);
    }

    public void repaint(long tm, int x, int y, int width, int height) {
        faked.repaint(tm, x, y, width, height);
    }

    public void repaint(int x, int y, int width, int height) {
        faked.repaint(x, y, width, height);
    }

    @Deprecated
    public boolean requestDefaultFocus() {
        return faked.requestDefaultFocus();
    }

    public void requestFocus() {
        faked.requestFocus();
    }

    public boolean requestFocus(boolean temporary) {
        return faked.requestFocus(temporary);
    }

    public boolean requestFocusInWindow() {
        return faked.requestFocusInWindow();
    }

    public void resetKeyboardActions() {
        faked.resetKeyboardActions();
    }

    @Deprecated
    public void reshape(int x, int y, int width, int height) {
        faked.reshape(x, y, width, height);
    }

    @Deprecated
    public void resize(Dimension d) {
        faked.resize(d);
    }

    @Deprecated
    public void resize(int width, int height) {
        faked.resize(width, height);
    }

    public void revalidate() {
        faked.revalidate();
    }

    public void scrollRectToVisible(Rectangle aRect) {
        faked.scrollRectToVisible(aRect);
    }

    public void setAction(Action a) {
        if (faked != null) faked.setAction(a);
    }

    public void setActionCommand(String actionCommand) {
        if (faked != null) faked.setActionCommand(actionCommand);
    }

    public void setAlignmentX(float alignmentX) {
        if (faked != null) faked.setAlignmentX(alignmentX);
    }

    public void setAlignmentY(float alignmentY) {
        if (faked != null) faked.setAlignmentY(alignmentY);
    }

    public void setAutoscrolls(boolean autoscrolls) {
        if (faked != null) faked.setAutoscrolls(autoscrolls);
    }

    public void setBackground(Color bg) {
        if (faked != null) faked.setBackground(bg);
    }

    public void setBorder(Border border) {
        if (faked != null) faked.setBorder(border);
    }

    public void setBorderPainted(boolean b) {
        if (faked != null) faked.setBorderPainted(b);
    }

    public void setBounds(Rectangle r) {
        if (faked != null) faked.setBounds(r);
    }

    public void setBounds(int x, int y, int width, int height) {
        if (faked != null) faked.setBounds(x, y, width, height);
    }

    public void setComponentOrientation(ComponentOrientation o) {
        if (faked != null) faked.setComponentOrientation(o);
    }

    public void setContentAreaFilled(boolean b) {
        if (faked != null) faked.setContentAreaFilled(b);
    }

    public void setCursor(Cursor cursor) {
        if (faked != null) faked.setCursor(cursor);
    }

    public void setDebugGraphicsOptions(int debugOptions) {
        if (faked != null) faked.setDebugGraphicsOptions(debugOptions);
    }

    public void setDisabledIcon(Icon disabledIcon) {
        if (faked != null) faked.setDisabledIcon(disabledIcon);
    }

    public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
        if (faked != null) faked.setDisabledSelectedIcon(disabledSelectedIcon);
    }

    public void setDisplayedMnemonicIndex(int index)
            throws IllegalArgumentException {
        if (faked != null) faked.setDisplayedMnemonicIndex(index);
    }

    public void setDoubleBuffered(boolean aFlag) {
        if (faked != null) faked.setDoubleBuffered(aFlag);
    }

    public void setDropTarget(DropTarget dt) {
        if (faked != null) faked.setDropTarget(dt);
    }

    public void setEnabled(boolean enabled) {
        if (faked != null) faked.setEnabled(enabled);
    }

    public void setFocusable(boolean focusable) {
        if (faked != null) faked.setFocusable(focusable);
    }

    public void setFocusCycleRoot(boolean focusCycleRoot) {
        if (faked != null) faked.setFocusCycleRoot(focusCycleRoot);
    }

    public void setFocusPainted(boolean b) {
        if (faked != null) faked.setFocusPainted(b);
    }

    public void setFocusTraversalKeys(int id,
            Set<? extends AWTKeyStroke> keystrokes) {
        if (faked != null) faked.setFocusTraversalKeys(id, keystrokes);
    }

    public void setFocusTraversalKeysEnabled(boolean
            focusTraversalKeysEnabled) {
        if (faked != null) faked.setFocusTraversalKeysEnabled(
                focusTraversalKeysEnabled);
    }

    public void setFocusTraversalPolicy(FocusTraversalPolicy policy) {
        if (faked != null) faked.setFocusTraversalPolicy(policy);
    }

    public void setFont(Font f) {
        if (faked != null) faked.setFont(f);
    }

    public void setForeground(Color fg) {
        if (faked != null) faked.setForeground(fg);
    }

    public void setHorizontalAlignment(int alignment) {
        if (faked != null) faked.setHorizontalAlignment(alignment);
    }

    public void setHorizontalTextPosition(int textPosition) {
        if (faked != null) faked.setHorizontalTextPosition(textPosition);
    }

    public void setIcon(Icon defaultIcon) {
        if (faked != null) faked.setIcon(defaultIcon);
    }

    public void setIconTextGap(int iconTextGap) {
        if (faked != null) faked.setIconTextGap(iconTextGap);
    }

    public void setIgnoreRepaint(boolean ignoreRepaint) {
        if (faked != null) faked.setIgnoreRepaint(ignoreRepaint);
    }

    public void setInputVerifier(InputVerifier inputVerifier) {
        if (faked != null) faked.setInputVerifier(inputVerifier);
    }

    @Deprecated
    public void setLabel(String label) {
        if (faked != null) faked.setLabel(label);
    }

    public void setLayout(LayoutManager mgr) {
        if (faked != null) faked.setLayout(mgr);
    }

    public void setLocale(Locale l) {
        if (faked != null) faked.setLocale(l);
    }

    public void setLocation(Point p) {
        if (faked != null) faked.setLocation(p);
    }

    public void setLocation(int x, int y) {
        if (faked != null) faked.setLocation(x, y);
    }

    public void setMargin(Insets m) {
        if (faked != null) faked.setMargin(m);
    }

    public void setMaximumSize(Dimension maximumSize) {
        if (faked != null) faked.setMaximumSize(maximumSize);
    }

    public void setMinimumSize(Dimension minimumSize) {
        if (faked != null) faked.setMinimumSize(minimumSize);
    }

    public void setMnemonic(char mnemonic) {
        if (faked != null) faked.setMnemonic(mnemonic);
    }

    public void setMnemonic(int mnemonic) {
        if (faked != null) faked.setMnemonic(mnemonic);
    }

    public void setModel(ButtonModel newModel) {
        if (faked != null) faked.setModel(newModel);
    }

    public void setMultiClickThreshhold(long threshhold) {
        if (faked != null) faked.setMultiClickThreshhold(threshhold);
    }

    public void setName(String name) {
        if (faked != null) faked.setName(name);
    }

    @Deprecated
    public void setNextFocusableComponent(Component aComponent) {
        if (faked != null) faked.setNextFocusableComponent(aComponent);
    }

    public void setOpaque(boolean isOpaque) {
        if (faked != null) faked.setOpaque(isOpaque);
    }

    public void setPreferredSize(Dimension preferredSize) {
        if (faked != null) faked.setPreferredSize(preferredSize);
    }

    public void setPressedIcon(Icon pressedIcon) {
        if (faked != null) faked.setPressedIcon(pressedIcon);
    }

    public void setRequestFocusEnabled(boolean requestFocusEnabled) {
        if (faked != null) faked.setRequestFocusEnabled(requestFocusEnabled);
    }

    public void setRolloverEnabled(boolean b) {
        if (faked != null) faked.setRolloverEnabled(b);
    }

    public void setRolloverIcon(Icon rolloverIcon) {
        if (faked != null) faked.setRolloverIcon(rolloverIcon);
    }

    public void setRolloverSelectedIcon(Icon rolloverSelectedIcon) {
        if (faked != null) faked.setRolloverSelectedIcon(rolloverSelectedIcon);
    }

    public void setSelected(boolean b) {
        if (faked != null) faked.setSelected(b);
    }

    public void setSelectedIcon(Icon selectedIcon) {
        if (faked != null) faked.setSelectedIcon(selectedIcon);
    }

    public void setSize(Dimension d) {
        if (faked != null) faked.setSize(d);
    }

    public void setSize(int width, int height) {
        if (faked != null) faked.setSize(width, height);
    }

    public void setText(String text) {
        if (faked != null) faked.setText(text);
    }

    public void setToolTipText(String text) {
        if (faked != null) faked.setToolTipText(text);
    }

    public void setTransferHandler(TransferHandler newHandler) {
        if (faked != null) faked.setTransferHandler(newHandler);
    }

    public void setUI(ComponentUI newUI) {
        if (faked != null) faked.setUI(newUI);
    }

    public void setUI(ButtonUI ui) {
        if (faked != null) faked.setUI(ui);
    }

    public void setVerifyInputWhenFocusTarget(boolean
            verifyInputWhenFocusTarget) {
        if (faked != null) faked.setVerifyInputWhenFocusTarget(
                verifyInputWhenFocusTarget);
    }

    public void setVerticalAlignment(int alignment) {
        if (faked != null) faked.setVerticalAlignment(alignment);
    }

    public void setVerticalTextPosition(int textPosition) {
        if (faked != null) faked.setVerticalTextPosition(textPosition);
    }

    public void setVisible(boolean b) {
        if (faked != null) faked.setVisible(b);
    }
    @Deprecated

    public void show() {
        faked.show();
    }

    @Deprecated
    public void show(boolean b) {
        faked.show(b);
    }

    @Deprecated
    public Dimension size() {
        return faked.size();
    }

    public String toString() {
        return faked.toString();
    }

    public void transferFocus() {
        faked.transferFocus();
    }

    public void transferFocusBackward() {
        faked.transferFocusBackward();
    }

    public void transferFocusDownCycle() {
        faked.transferFocusDownCycle();
    }

    public void transferFocusUpCycle() {
        faked.transferFocusUpCycle();
    }

    public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
        faked.unregisterKeyboardAction(aKeyStroke);
    }

    public void update(Graphics g) {
        faked.update(g);
    }

    public void updateUI() {
        if (faked != null)
            faked.updateUI();
    }

    public void validate() {
        faked.validate();
    }
}
