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
 * This is pretty ugly.  The idea is that disabled buttons should be drawn just
 * like enabled ones, and then crossed out.  We want to reuse the code that
 * draws the enabled buttons rather than copy it into our button painting
 * method(s).
 * <p/>
 * The obvious thing to do (for me, anyway) is when painting a disabled button,
 * set it to be enabled, paint the button, and then set it back to disabled (and
 * then draw the line).  There are several problems with this, the primary being
 * that changing whether a button is enabled has many side effects within its
 * button group and the overall UI.  Not to mention that it fires a "repaint" of
 * the button, causing an infinte sequence of repaints for any disabled button.
 * <p/>
 * This is the only way I could figure out to reuse that code: I create a
 * wrapper object that, where possible, forwards all calls to the underlying
 * button.  Except that it says the button is enabled.  This also requires
 * returning a <tt>ButtonModel</tt> wrapper because the model can also be asked
 * for the enabled status.  This works, but although this class looks like a
 * complete wrapper it is NOT.  I've wrapped all methods because it was easy,
 * but had to not wrap the methods that were impossible to wrap (final and
 * protected methods).  Also a few methods are invoked during construction of an
 * <tt>AbstractButton</tt>, and so were being invoked before the original button
 * field was set, causing a <tt>NullPointerException</tt>.  These have been
 * trivally modified because they don't matter right now in the way that we're
 * using the class, and for future safety, all "set" methods are modified as
 * well (rather than do only the "set" methods used during construction in this
 * version and having it break in a future JDK). but if you try to use this in a
 * sophisticated context this wrapping may well fail.  (Of course, it's almost
 * certainly wrong to use this in any other context because it is a hack, but
 * I'm warning you in case you are also forced into a nasty ugliness vs.
 * goodness tradeoff.)
 * <p/>
 * NOTE: To make it EVEN UGLIER, some parts of the code need a
 * <tt>JMenuItem</tt> rather than an abstract button, so there is a
 * not-officially-related {@link FakeEnabledMenuItem} class that delegates to
 * this, but has the same problems during construction.
 */
class FakeEnabledButton extends AbstractButton implements FakeEnabled {
    private final AbstractButton orig;

    FakeEnabledButton(JComponent c) {
        if (c == null)
            throw new NullPointerException("c");
        orig = (AbstractButton) c;
    }

    public boolean isEnabled() {
        return true;
    }

    public ButtonModel getModel() {
        return new FakeEnabledModel(orig.getModel());
    }

    public boolean action(Event evt, Object what) {
        return orig.action(evt, what);
    }

    public Component add(Component comp) {
        return orig.add(comp);
    }

    public void add(Component comp, Object constraints) {
        orig.add(comp, constraints);
    }

    public void add(Component comp, Object constraints, int index) {
        orig.add(comp, constraints, index);
    }

    public Component add(Component comp, int index) {
        return orig.add(comp, index);
    }

    public Component add(String name, Component comp) {
        return orig.add(name, comp);
    }

    public void add(PopupMenu popup) {
        orig.add(popup);
    }

    public void addActionListener(ActionListener l) {
        orig.addActionListener(l);
    }

    public void addAncestorListener(AncestorListener listener) {
        orig.addAncestorListener(listener);
    }

    public void addChangeListener(ChangeListener l) {
        orig.addChangeListener(l);
    }

    public void addComponentListener(ComponentListener l) {
        orig.addComponentListener(l);
    }

    public void addContainerListener(ContainerListener l) {
        orig.addContainerListener(l);
    }

    public void addFocusListener(FocusListener l) {
        if (orig != null)   // can be null during construction, and we don't care
            orig.addFocusListener(l);
    }

    public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
        orig.addHierarchyBoundsListener(l);
    }

    public void addHierarchyListener(HierarchyListener l) {
        orig.addHierarchyListener(l);
    }

    public void addInputMethodListener(InputMethodListener l) {
        orig.addInputMethodListener(l);
    }

    public void addItemListener(ItemListener l) {
        orig.addItemListener(l);
    }

    public void addKeyListener(KeyListener l) {
        orig.addKeyListener(l);
    }

    public void addMouseListener(MouseListener l) {
        orig.addMouseListener(l);
    }

    public void addMouseMotionListener(MouseMotionListener l) {
        orig.addMouseMotionListener(l);
    }

    public void addMouseWheelListener(MouseWheelListener l) {
        orig.addMouseWheelListener(l);
    }

    public void addNotify() {
        orig.addNotify();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        orig.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        orig.addPropertyChangeListener(propertyName, listener);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        orig.addVetoableChangeListener(listener);
    }

    public void applyComponentOrientation(ComponentOrientation orientation) {
        orig.applyComponentOrientation(orientation);
    }

    public boolean areFocusTraversalKeysSet(int id) {
        return orig.areFocusTraversalKeysSet(id);
    }

    public Rectangle bounds() {
        return orig.bounds();
    }

    public int checkImage(Image image, ImageObserver observer) {
        return orig.checkImage(image, observer);
    }

    public int checkImage(Image image, int width, int height,
            ImageObserver observer) {
        return orig.checkImage(image, width, height, observer);
    }

    public void computeVisibleRect(Rectangle visibleRect) {
        orig.computeVisibleRect(visibleRect);
    }

    public boolean contains(Point p) {
        return orig.contains(p);
    }

    public boolean contains(int x, int y) {
        return orig.contains(x, y);
    }

    public int countComponents() {
        return orig.countComponents();
    }

    public Image createImage(ImageProducer producer) {
        return orig.createImage(producer);
    }

    public Image createImage(int width, int height) {
        return orig.createImage(width, height);
    }

    public JToolTip createToolTip() {
        return orig.createToolTip();
    }

    public VolatileImage createVolatileImage(int width, int height) {
        return orig.createVolatileImage(width, height);
    }

    public VolatileImage createVolatileImage(int width, int height,
            ImageCapabilities caps) throws AWTException {
        return orig.createVolatileImage(width, height, caps);
    }

    public void deliverEvent(Event e) {
        orig.deliverEvent(e);
    }

    public void disable() {
        orig.disable();
    }

    public void doClick() {
        orig.doClick();
    }

    public void doClick(int pressTime) {
        orig.doClick(pressTime);
    }

    public void doLayout() {
        orig.doLayout();
    }

    public void enable() {
        orig.enable();
    }

    public void enable(boolean b) {
        orig.enable(b);
    }

    public void enableInputMethods(boolean enable) {
        orig.enableInputMethods(enable);
    }

    public Component findComponentAt(Point p) {
        return orig.findComponentAt(p);
    }

    public Component findComponentAt(int x, int y) {
        return orig.findComponentAt(x, y);
    }

    public void firePropertyChange(String propertyName,
            boolean oldValue, boolean newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName,
            int oldValue, int newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        orig.firePropertyChange(propertyName, oldValue, newValue);
    }

    public AccessibleContext getAccessibleContext() {
        return orig.getAccessibleContext();
    }

    public Action getAction() {
        return orig.getAction();
    }

    public String getActionCommand() {
        return orig.getActionCommand();
    }

    public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) {
        return orig.getActionForKeyStroke(aKeyStroke);
    }

    public ActionListener[] getActionListeners() {
        return orig.getActionListeners();
    }

    public float getAlignmentX() {
        return orig.getAlignmentX();
    }

    public float getAlignmentY() {
        return orig.getAlignmentY();
    }

    public AncestorListener[] getAncestorListeners() {
        return orig.getAncestorListeners();
    }

    public boolean getAutoscrolls() {
        return orig.getAutoscrolls();
    }

    public Color getBackground() {
        return orig.getBackground();
    }

    public Border getBorder() {
        return orig.getBorder();
    }

    public Rectangle getBounds() {
        return orig.getBounds();
    }

    public Rectangle getBounds(Rectangle rv) {
        return orig.getBounds(rv);
    }

    public ChangeListener[] getChangeListeners() {
        return orig.getChangeListeners();
    }

    public ColorModel getColorModel() {
        return orig.getColorModel();
    }

    public Component getComponent(int n) {
        return orig.getComponent(n);
    }

    public Component getComponentAt(Point p) {
        return orig.getComponentAt(p);
    }

    public Component getComponentAt(int x, int y) {
        return orig.getComponentAt(x, y);
    }

    public int getComponentCount() {
        return orig.getComponentCount();
    }

    public ComponentListener[] getComponentListeners() {
        return orig.getComponentListeners();
    }

    public ComponentOrientation getComponentOrientation() {
        return orig.getComponentOrientation();
    }

    public Component[] getComponents() {
        return orig.getComponents();
    }

    public int getConditionForKeyStroke(KeyStroke aKeyStroke) {
        return orig.getConditionForKeyStroke(aKeyStroke);
    }

    public ContainerListener[] getContainerListeners() {
        return orig.getContainerListeners();
    }

    public Cursor getCursor() {
        return orig.getCursor();
    }

    public int getDebugGraphicsOptions() {
        return orig.getDebugGraphicsOptions();
    }

    public Icon getDisabledIcon() {
        return orig.getDisabledIcon();
    }

    public Icon getDisabledSelectedIcon() {
        return orig.getDisabledSelectedIcon();
    }

    public int getDisplayedMnemonicIndex() {
        return orig.getDisplayedMnemonicIndex();
    }

    public DropTarget getDropTarget() {
        return orig.getDropTarget();
    }

    public Container getFocusCycleRootAncestor() {
        return orig.getFocusCycleRootAncestor();
    }

    public FocusListener[] getFocusListeners() {
        return orig.getFocusListeners();
    }

    public Set getFocusTraversalKeys(int id) {
        return orig.getFocusTraversalKeys(id);
    }

    public boolean getFocusTraversalKeysEnabled() {
        return orig.getFocusTraversalKeysEnabled();
    }

    public FocusTraversalPolicy getFocusTraversalPolicy() {
        return orig.getFocusTraversalPolicy();
    }

    public Font getFont() {
        return orig.getFont();
    }

    public FontMetrics getFontMetrics(Font font) {
        return orig.getFontMetrics(font);
    }

    public Color getForeground() {
        return orig.getForeground();
    }

    public Graphics getGraphics() {
        return orig.getGraphics();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return orig.getGraphicsConfiguration();
    }

    public int getHeight() {
        return orig.getHeight();
    }

    public HierarchyBoundsListener[] getHierarchyBoundsListeners() {
        return orig.getHierarchyBoundsListeners();
    }

    public HierarchyListener[] getHierarchyListeners() {
        return orig.getHierarchyListeners();
    }

    public int getHorizontalAlignment() {
        return orig.getHorizontalAlignment();
    }

    public int getHorizontalTextPosition() {
        return orig.getHorizontalTextPosition();
    }

    public Icon getIcon() {
        return orig.getIcon();
    }

    public int getIconTextGap() {
        return orig.getIconTextGap();
    }

    public boolean getIgnoreRepaint() {
        return orig.getIgnoreRepaint();
    }

    public InputContext getInputContext() {
        return orig.getInputContext();
    }

    public InputMethodListener[] getInputMethodListeners() {
        return orig.getInputMethodListeners();
    }

    public InputMethodRequests getInputMethodRequests() {
        return orig.getInputMethodRequests();
    }

    public InputVerifier getInputVerifier() {
        return orig.getInputVerifier();
    }

    public Insets getInsets() {
        return orig.getInsets();
    }

    public Insets getInsets(Insets insets) {
        return orig.getInsets(insets);
    }

    public ItemListener[] getItemListeners() {
        return orig.getItemListeners();
    }

    public KeyListener[] getKeyListeners() {
        return orig.getKeyListeners();
    }

    public String getLabel() {
        return orig.getLabel();
    }

    public LayoutManager getLayout() {
        return orig.getLayout();
    }

    public EventListener[] getListeners(Class listenerType) {
        return orig.getListeners(listenerType);
    }

    public Locale getLocale() {
        return orig.getLocale();
    }

    public Point getLocation() {
        return orig.getLocation();
    }

    public Point getLocation(Point rv) {
        return orig.getLocation(rv);
    }

    public Point getLocationOnScreen() {
        return orig.getLocationOnScreen();
    }

    public Insets getMargin() {
        return orig.getMargin();
    }

    public Dimension getMaximumSize() {
        return orig.getMaximumSize();
    }

    public Dimension getMinimumSize() {
        return orig.getMinimumSize();
    }

    public int getMnemonic() {
        return orig.getMnemonic();
    }

    public MouseListener[] getMouseListeners() {
        return orig.getMouseListeners();
    }

    public MouseMotionListener[] getMouseMotionListeners() {
        return orig.getMouseMotionListeners();
    }

    public MouseWheelListener[] getMouseWheelListeners() {
        return orig.getMouseWheelListeners();
    }

    public long getMultiClickThreshhold() {
        return orig.getMultiClickThreshhold();
    }

    public String getName() {
        return orig.getName();
    }

    public Component getNextFocusableComponent() {
        return orig.getNextFocusableComponent();
    }

    public Container getParent() {
        return orig.getParent();
    }

    public ComponentPeer getPeer() {
        return orig.getPeer();
    }

    public Dimension getPreferredSize() {
        return orig.getPreferredSize();
    }

    public Icon getPressedIcon() {
        return orig.getPressedIcon();
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return orig.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return orig.getPropertyChangeListeners(propertyName);
    }

    public KeyStroke[] getRegisteredKeyStrokes() {
        return orig.getRegisteredKeyStrokes();
    }

    public Icon getRolloverIcon() {
        return orig.getRolloverIcon();
    }

    public Icon getRolloverSelectedIcon() {
        return orig.getRolloverSelectedIcon();
    }

    public JRootPane getRootPane() {
        return orig.getRootPane();
    }

    public Icon getSelectedIcon() {
        return orig.getSelectedIcon();
    }

    public Object[] getSelectedObjects() {
        return orig.getSelectedObjects();
    }

    public Dimension getSize() {
        return orig.getSize();
    }

    public Dimension getSize(Dimension rv) {
        return orig.getSize(rv);
    }

    public String getText() {
        return orig.getText();
    }

    public Toolkit getToolkit() {
        return orig.getToolkit();
    }

    public Point getToolTipLocation(MouseEvent event) {
        return orig.getToolTipLocation(event);
    }

    public String getToolTipText() {
        return orig.getToolTipText();
    }

    public String getToolTipText(MouseEvent event) {
        return orig.getToolTipText(event);
    }

    public Container getTopLevelAncestor() {
        return orig.getTopLevelAncestor();
    }

    public TransferHandler getTransferHandler() {
        return orig.getTransferHandler();
    }

    public ButtonUI getUI() {
        return orig.getUI();
    }

    public String getUIClassID() {
        return orig.getUIClassID();
    }

    public boolean getVerifyInputWhenFocusTarget() {
        return orig.getVerifyInputWhenFocusTarget();
    }

    public int getVerticalAlignment() {
        return orig.getVerticalAlignment();
    }

    public int getVerticalTextPosition() {
        return orig.getVerticalTextPosition();
    }

    public VetoableChangeListener[] getVetoableChangeListeners() {
        return orig.getVetoableChangeListeners();
    }

    public Rectangle getVisibleRect() {
        return orig.getVisibleRect();
    }

    public int getWidth() {
        return orig.getWidth();
    }

    public int getX() {
        return orig.getX();
    }

    public int getY() {
        return orig.getY();
    }

    public boolean gotFocus(Event evt, Object what) {
        return orig.gotFocus(evt, what);
    }

    public void grabFocus() {
        orig.grabFocus();
    }

    public boolean handleEvent(Event evt) {
        return orig.handleEvent(evt);
    }

    public boolean hasFocus() {
        return orig.hasFocus();
    }

    public void hide() {
        orig.hide();
    }

    public boolean imageUpdate(Image img, int infoflags,
            int x, int y, int width, int height) {
        return orig.imageUpdate(img, infoflags, x, y, width, height);
    }

    public Insets insets() {
        return orig.insets();
    }

    public boolean inside(int x, int y) {
        return orig.inside(x, y);
    }

    public void invalidate() {
        orig.invalidate();
    }

    public boolean isAncestorOf(Component c) {
        return orig.isAncestorOf(c);
    }

    public boolean isBackgroundSet() {
        return orig.isBackgroundSet();
    }

    public boolean isBorderPainted() {
        return orig.isBorderPainted();
    }

    public boolean isContentAreaFilled() {
        return orig.isContentAreaFilled();
    }

    public boolean isCursorSet() {
        return orig.isCursorSet();
    }

    public boolean isDisplayable() {
        return orig.isDisplayable();
    }

    public boolean isDoubleBuffered() {
        return orig.isDoubleBuffered();
    }

    public boolean isFocusable() {
        return orig.isFocusable();
    }

    public boolean isFocusCycleRoot() {
        return orig.isFocusCycleRoot();
    }

    public boolean isFocusCycleRoot(Container container) {
        return orig.isFocusCycleRoot(container);
    }

    public boolean isFocusOwner() {
        return orig.isFocusOwner();
    }

    public boolean isFocusPainted() {
        return orig.isFocusPainted();
    }

    public boolean isFocusTraversable() {
        return orig.isFocusTraversable();
    }

    public boolean isFocusTraversalPolicySet() {
        return orig.isFocusTraversalPolicySet();
    }

    public boolean isFontSet() {
        return orig.isFontSet();
    }

    public boolean isForegroundSet() {
        return orig.isForegroundSet();
    }

    public boolean isLightweight() {
        return orig.isLightweight();
    }

    public boolean isManagingFocus() {
        if (orig != null)   // can be null during construction, and we don't care
            return orig.isManagingFocus();
        else
            return false;
    }

    public boolean isMaximumSizeSet() {
        return orig.isMaximumSizeSet();
    }

    public boolean isMinimumSizeSet() {
        return orig.isMinimumSizeSet();
    }

    public boolean isOpaque() {
        return orig.isOpaque();
    }

    public boolean isOptimizedDrawingEnabled() {
        return orig.isOptimizedDrawingEnabled();
    }

    public boolean isPaintingTile() {
        return orig.isPaintingTile();
    }

    public boolean isPreferredSizeSet() {
        return orig.isPreferredSizeSet();
    }

    public boolean isRequestFocusEnabled() {
        return orig.isRequestFocusEnabled();
    }

    public boolean isRolloverEnabled() {
        return orig.isRolloverEnabled();
    }

    public boolean isSelected() {
        return orig.isSelected();
    }

    public boolean isShowing() {
        return orig.isShowing();
    }

    public boolean isValid() {
        return orig.isValid();
    }

    public boolean isValidateRoot() {
        return orig.isValidateRoot();
    }

    public boolean isVisible() {
        return orig.isVisible();
    }

    public boolean keyDown(Event evt, int key) {
        return orig.keyDown(evt, key);
    }

    public boolean keyUp(Event evt, int key) {
        return orig.keyUp(evt, key);
    }

    public void layout() {
        orig.layout();
    }

    public void list() {
        orig.list();
    }

    public void list(PrintStream out) {
        orig.list(out);
    }

    public void list(PrintStream out, int indent) {
        orig.list(out, indent);
    }

    public void list(PrintWriter out) {
        orig.list(out);
    }

    public void list(PrintWriter out, int indent) {
        orig.list(out, indent);
    }

    public Component locate(int x, int y) {
        return orig.locate(x, y);
    }

    public Point location() {
        return orig.location();
    }

    public boolean lostFocus(Event evt, Object what) {
        return orig.lostFocus(evt, what);
    }

    public Dimension minimumSize() {
        return orig.minimumSize();
    }

    public boolean mouseDown(Event evt, int x, int y) {
        return orig.mouseDown(evt, x, y);
    }

    public boolean mouseDrag(Event evt, int x, int y) {
        return orig.mouseDrag(evt, x, y);
    }

    public boolean mouseEnter(Event evt, int x, int y) {
        return orig.mouseEnter(evt, x, y);
    }

    public boolean mouseExit(Event evt, int x, int y) {
        return orig.mouseExit(evt, x, y);
    }

    public boolean mouseMove(Event evt, int x, int y) {
        return orig.mouseMove(evt, x, y);
    }

    public boolean mouseUp(Event evt, int x, int y) {
        return orig.mouseUp(evt, x, y);
    }

    public void move(int x, int y) {
        orig.move(x, y);
    }

    public void nextFocus() {
        orig.nextFocus();
    }

    public void paint(Graphics g) {
        orig.paint(g);
    }

    public void paintAll(Graphics g) {
        orig.paintAll(g);
    }

    public void paintComponents(Graphics g) {
        orig.paintComponents(g);
    }

    public void paintImmediately(Rectangle r) {
        orig.paintImmediately(r);
    }

    public void paintImmediately(int x, int y, int w, int h) {
        orig.paintImmediately(x, y, w, h);
    }

    public boolean postEvent(Event evt) {
        return orig.postEvent(evt);
    }

    public Dimension preferredSize() {
        return orig.preferredSize();
    }

    public boolean prepareImage(Image image, ImageObserver observer) {
        return orig.prepareImage(image, observer);
    }

    public boolean prepareImage(Image image, int width, int height,
            ImageObserver observer) {
        return orig.prepareImage(image, width, height, observer);
    }

    public void print(Graphics g) {
        orig.print(g);
    }

    public void printAll(Graphics g) {
        orig.printAll(g);
    }

    public void registerKeyboardAction(ActionListener anAction, String aCommand, KeyStroke aKeyStroke, int aCondition) {
        orig.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    }

    public void registerKeyboardAction(ActionListener anAction, KeyStroke aKeyStroke, int aCondition) {
        orig.registerKeyboardAction(anAction, aKeyStroke, aCondition);
    }

    public void remove(Component comp) {
        orig.remove(comp);
    }

    public void remove(int index) {
        orig.remove(index);
    }

    public void removeActionListener(ActionListener l) {
        orig.removeActionListener(l);
    }

    public void removeAll() {
        orig.removeAll();
    }

    public void removeAncestorListener(AncestorListener listener) {
        orig.removeAncestorListener(listener);
    }

    public void removeChangeListener(ChangeListener l) {
        orig.removeChangeListener(l);
    }

    public void removeComponentListener(ComponentListener l) {
        orig.removeComponentListener(l);
    }

    public void removeContainerListener(ContainerListener l) {
        orig.removeContainerListener(l);
    }

    public void removeFocusListener(FocusListener l) {
        orig.removeFocusListener(l);
    }

    public void removeHierarchyBoundsListener(HierarchyBoundsListener l) {
        orig.removeHierarchyBoundsListener(l);
    }

    public void removeHierarchyListener(HierarchyListener l) {
        orig.removeHierarchyListener(l);
    }

    public void removeInputMethodListener(InputMethodListener l) {
        orig.removeInputMethodListener(l);
    }

    public void removeItemListener(ItemListener l) {
        orig.removeItemListener(l);
    }

    public void removeKeyListener(KeyListener l) {
        orig.removeKeyListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        orig.removeMouseListener(l);
    }

    public void removeMouseMotionListener(MouseMotionListener l) {
        orig.removeMouseMotionListener(l);
    }

    public void removeMouseWheelListener(MouseWheelListener l) {
        orig.removeMouseWheelListener(l);
    }

    public void removeNotify() {
        orig.removeNotify();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        orig.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        orig.removePropertyChangeListener(propertyName, listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        orig.removeVetoableChangeListener(listener);
    }

    public void repaint() {
        orig.repaint();
    }

    public void repaint(Rectangle r) {
        orig.repaint(r);
    }

    public void repaint(long tm) {
        orig.repaint(tm);
    }

    public void repaint(long tm, int x, int y, int width, int height) {
        orig.repaint(tm, x, y, width, height);
    }

    public void repaint(int x, int y, int width, int height) {
        orig.repaint(x, y, width, height);
    }

    public boolean requestDefaultFocus() {
        return orig.requestDefaultFocus();
    }

    public void requestFocus() {
        orig.requestFocus();
    }

    public boolean requestFocus(boolean temporary) {
        return orig.requestFocus(temporary);
    }

    public boolean requestFocusInWindow() {
        return orig.requestFocusInWindow();
    }

    public void resetKeyboardActions() {
        orig.resetKeyboardActions();
    }

    public void reshape(int x, int y, int width, int height) {
        orig.reshape(x, y, width, height);
    }

    public void resize(Dimension d) {
        orig.resize(d);
    }

    public void resize(int width, int height) {
        orig.resize(width, height);
    }

    public void revalidate() {
        orig.revalidate();
    }

    public void scrollRectToVisible(Rectangle aRect) {
        orig.scrollRectToVisible(aRect);
    }

    public void setAction(Action a) {
        if (orig != null)
            orig.setAction(a);
    }

    public void setActionCommand(String actionCommand) {
        if (orig != null)
            orig.setActionCommand(actionCommand);
    }

    public void setAlignmentX(float alignmentX) {
        if (orig != null)
            orig.setAlignmentX(alignmentX);
    }

    public void setAlignmentY(float alignmentY) {
        if (orig != null)
            orig.setAlignmentY(alignmentY);
    }

    public void setAutoscrolls(boolean autoscrolls) {
        if (orig != null)
            orig.setAutoscrolls(autoscrolls);
    }

    public void setBackground(Color bg) {
        if (orig != null)
            orig.setBackground(bg);
    }

    public void setBorder(Border border) {
        if (orig != null)
            orig.setBorder(border);
    }

    public void setBorderPainted(boolean b) {
        if (orig != null)
            orig.setBorderPainted(b);
    }

    public void setBounds(Rectangle r) {
        if (orig != null)
            orig.setBounds(r);
    }

    public void setBounds(int x, int y, int width, int height) {
        if (orig != null)
            orig.setBounds(x, y, width, height);
    }

    public void setComponentOrientation(ComponentOrientation o) {
        if (orig != null)
            orig.setComponentOrientation(o);
    }

    public void setContentAreaFilled(boolean b) {
        if (orig != null)
            orig.setContentAreaFilled(b);
    }

    public void setCursor(Cursor cursor) {
        if (orig != null)
            orig.setCursor(cursor);
    }

    public void setDebugGraphicsOptions(int debugOptions) {
        if (orig != null)
            orig.setDebugGraphicsOptions(debugOptions);
    }

    public void setDisabledIcon(Icon disabledIcon) {
        if (orig != null)
            orig.setDisabledIcon(disabledIcon);
    }

    public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
        if (orig != null)
            orig.setDisabledSelectedIcon(disabledSelectedIcon);
    }

    public void setDisplayedMnemonicIndex(int index)
            throws IllegalArgumentException {
        if (orig != null)
            orig.setDisplayedMnemonicIndex(index);
    }

    public void setDoubleBuffered(boolean aFlag) {
        if (orig != null)
            orig.setDoubleBuffered(aFlag);
    }

    public void setDropTarget(DropTarget dt) {
        if (orig != null)
            orig.setDropTarget(dt);
    }

    public void setEnabled(boolean enabled) {
        if (orig != null)
            orig.setEnabled(enabled);
    }

    public void setFocusable(boolean focusable) {
        if (orig != null)
            orig.setFocusable(focusable);
    }

    public void setFocusCycleRoot(boolean focusCycleRoot) {
        if (orig != null)
            orig.setFocusCycleRoot(focusCycleRoot);
    }

    public void setFocusPainted(boolean b) {
        if (orig != null)
            orig.setFocusPainted(b);
    }

    public void setFocusTraversalKeys(int id, Set keystrokes) {
        if (orig != null)
            orig.setFocusTraversalKeys(id, keystrokes);
    }

    public void setFocusTraversalKeysEnabled(boolean
            focusTraversalKeysEnabled) {
        if (orig != null)
            orig.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
    }

    public void setFocusTraversalPolicy(FocusTraversalPolicy policy) {
        if (orig != null)
            orig.setFocusTraversalPolicy(policy);
    }

    public void setFont(Font f) {
        if (orig != null)
            orig.setFont(f);
    }

    public void setForeground(Color fg) {
        if (orig != null)
            orig.setForeground(fg);
    }

    public void setHorizontalAlignment(int alignment) {
        if (orig != null)
            orig.setHorizontalAlignment(alignment);
    }

    public void setHorizontalTextPosition(int textPosition) {
        if (orig != null)
            orig.setHorizontalTextPosition(textPosition);
    }

    public void setIcon(Icon defaultIcon) {
        if (orig != null)
            orig.setIcon(defaultIcon);
    }

    public void setIconTextGap(int iconTextGap) {
        if (orig != null)
            orig.setIconTextGap(iconTextGap);
    }

    public void setIgnoreRepaint(boolean ignoreRepaint) {
        if (orig != null)
            orig.setIgnoreRepaint(ignoreRepaint);
    }

    public void setInputVerifier(InputVerifier inputVerifier) {
        if (orig != null)
            orig.setInputVerifier(inputVerifier);
    }

    public void setLabel(String label) {
        if (orig != null)
            orig.setLabel(label);
    }

    public void setLayout(LayoutManager mgr) {
        if (orig != null)
            orig.setLayout(mgr);
    }

    public void setLocale(Locale l) {
        if (orig != null)
            orig.setLocale(l);
    }

    public void setLocation(Point p) {
        if (orig != null)
            orig.setLocation(p);
    }

    public void setLocation(int x, int y) {
        if (orig != null)
            orig.setLocation(x, y);
    }

    public void setMargin(Insets m) {
        if (orig != null)
            orig.setMargin(m);
    }

    public void setMaximumSize(Dimension maximumSize) {
        if (orig != null)
            orig.setMaximumSize(maximumSize);
    }

    public void setMinimumSize(Dimension minimumSize) {
        if (orig != null)
            orig.setMinimumSize(minimumSize);
    }

    public void setMnemonic(char mnemonic) {
        if (orig != null)
            orig.setMnemonic(mnemonic);
    }

    public void setMnemonic(int mnemonic) {
        if (orig != null)
            orig.setMnemonic(mnemonic);
    }

    public void setModel(ButtonModel newModel) {
        if (orig != null)
            orig.setModel(newModel);
    }

    public void setMultiClickThreshhold(long threshhold) {
        if (orig != null)
            orig.setMultiClickThreshhold(threshhold);
    }

    public void setName(String name) {
        if (orig != null)
            orig.setName(name);
    }

    public void setNextFocusableComponent(Component aComponent) {
        if (orig != null)
            orig.setNextFocusableComponent(aComponent);
    }

    public void setOpaque(boolean isOpaque) {
        if (orig != null)
            orig.setOpaque(isOpaque);
    }

    public void setPreferredSize(Dimension preferredSize) {
        if (orig != null)
            orig.setPreferredSize(preferredSize);
    }

    public void setPressedIcon(Icon pressedIcon) {
        if (orig != null)
            orig.setPressedIcon(pressedIcon);
    }

    public void setRequestFocusEnabled(boolean requestFocusEnabled) {
        if (orig != null)
            orig.setRequestFocusEnabled(requestFocusEnabled);
    }

    public void setRolloverEnabled(boolean b) {
        if (orig != null)
            orig.setRolloverEnabled(b);
    }

    public void setRolloverIcon(Icon rolloverIcon) {
        if (orig != null)
            orig.setRolloverIcon(rolloverIcon);
    }

    public void setRolloverSelectedIcon(Icon rolloverSelectedIcon) {
        if (orig != null)
            orig.setRolloverSelectedIcon(rolloverSelectedIcon);
    }

    public void setSelected(boolean b) {
        if (orig != null)
            orig.setSelected(b);
    }

    public void setSelectedIcon(Icon selectedIcon) {
        if (orig != null)
            orig.setSelectedIcon(selectedIcon);
    }

    public void setSize(Dimension d) {
        if (orig != null)
            orig.setSize(d);
    }

    public void setSize(int width, int height) {
        if (orig != null)
            orig.setSize(width, height);
    }

    public void setText(String text) {
        if (orig != null)
            orig.setText(text);
    }

    public void setToolTipText(String text) {
        if (orig != null)
            orig.setToolTipText(text);
    }

    public void setTransferHandler(TransferHandler newHandler) {
        if (orig != null)
            orig.setTransferHandler(newHandler);
    }

    public void setUI(ComponentUI newUI) {
        if (orig != null)
            orig.setUI((ButtonUI) newUI);
    }

    public void setUI(ButtonUI ui) {
        if (orig != null)
            orig.setUI(ui);
    }

    public void setVerifyInputWhenFocusTarget(boolean
            verifyInputWhenFocusTarget) {
        if (orig != null)
            orig.setVerifyInputWhenFocusTarget(verifyInputWhenFocusTarget);
    }

    public void setVerticalAlignment(int alignment) {
        if (orig != null)
            orig.setVerticalAlignment(alignment);
    }

    public void setVerticalTextPosition(int textPosition) {
        if (orig != null)
            orig.setVerticalTextPosition(textPosition);
    }

    public void setVisible(boolean b) {
        if (orig != null)
            orig.setVisible(b);
    }

    public void show() {
        orig.show();
    }

    public void show(boolean b) {
        orig.show(b);
    }

    public Dimension size() {
        return orig.size();
    }

    public String toString() {
        return orig.toString();
    }

    public void transferFocus() {
        orig.transferFocus();
    }

    public void transferFocusBackward() {
        orig.transferFocusBackward();
    }

    public void transferFocusDownCycle() {
        orig.transferFocusDownCycle();
    }

    public void transferFocusUpCycle() {
        orig.transferFocusUpCycle();
    }

    public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
        orig.unregisterKeyboardAction(aKeyStroke);
    }

    public void update(Graphics g) {
        orig.update(g);
    }

    public void updateUI() {
        orig.updateUI();
    }

    public void validate() {
        orig.validate();
    }
}
