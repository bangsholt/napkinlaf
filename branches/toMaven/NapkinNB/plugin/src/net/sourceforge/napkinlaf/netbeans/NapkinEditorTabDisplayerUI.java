/*
 * NapkinEditorTabDisplayerUI.java
 *
 * Created on 19 April 2006, 19:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.netbeans;

import net.sourceforge.napkinlaf.shapes.AbstractDrawnGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import org.netbeans.swing.tabcontrol.*;
import org.netbeans.swing.tabcontrol.plaf.*;

import net.sourceforge.napkinlaf.NapkinTheme;
import net.sourceforge.napkinlaf.borders.*;
import net.sourceforge.napkinlaf.shapes.DrawnTabHolder;
import net.sourceforge.napkinlaf.util.*;
import static net.sourceforge.napkinlaf.util.NapkinConstants.*;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinEditorTabDisplayerUI extends BasicScrollingTabDisplayerUI
        implements NapkinPainter {
    
    private static class NapkinTabPainter implements TabPainter {
        
        private final Icon CLOSE_ICON = NapkinIconFactory.createXIcon(15);
        private static final int MAX_CACHE_SIZE = 10;
        private static final Map<Rectangle, DrawnTabHolder> cache =
            new LinkedHashMap<Rectangle, DrawnTabHolder>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<Rectangle, DrawnTabHolder> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            };
        
        public Polygon getInteriorPolygon(Component component) {
            Rectangle bounds = component.getBounds();
            int w = component.getWidth();
            int h = component.getHeight();
            return new Polygon(
                    new int[] {10, w - 10, w, 0},
                    new int[] {0, 0, h, h},
                    4
            );
        }

        public void paintInterior(Graphics g, Component c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Rectangle cb = new Rectangle();
            NapkinTabCellRenderer tcr = (NapkinTabCellRenderer) c;
            Rectangle bounds = c.getBounds();
            bounds.x = 0;
            bounds.y = 0;
            getCloseButtonRectangle(tcr, cb, bounds);
            g.setColor(NapkinUtil.currentTheme(c).getPenColor());
            if (cb.width > 0) {
                CLOSE_ICON.paintIcon(c, g, cb.x, cb.y);
            }
        }

        public void getCloseButtonRectangle(JComponent jc,
                Rectangle rect, Rectangle bounds) {

            if (!((NapkinTabCellRenderer) jc).isShowCloseButton()) {
                rect.setBounds(0, 0, 0, 0);
            } else {
                rect.width = CLOSE_ICON.getIconWidth();
                rect.height = CLOSE_ICON.getIconHeight();
                rect.x = bounds.x + bounds.width - rect.width - 2;
                rect.y = (bounds.y + bounds.height - rect.height) / 2;
            }
        }

        public boolean supportsCloseButton(JComponent jc) {
            return ((NapkinTabCellRenderer) jc).isShowCloseButton();
        }

        public void paintBorder(Component c, Graphics g,
                int x, int y, int width, int height) {

            Rectangle rect = new Rectangle(x, y, width, height);
            DrawnTabHolder holder = cache.get(rect);
            if (holder == null) {
                holder = new DrawnTabHolder(NORTH);
                cache.put(rect, holder);
            }
            holder.shapeUpToDate(NORTH, Math.max(0, x - 3),
                    y + 3, width + 3, height - 3);
            holder.draw(g);
        }

        public Insets getBorderInsets(Component c) {
            Insets result = NapkinBoxBorder.getDefaultInsets(c.getBounds());
            result.left += 5;
            result.right += CLOSE_ICON.getIconWidth() + 5;
            result.bottom = 0;
            return result;
        }

        public boolean isBorderOpaque() {
            return false;
        }
        
    }
    
    private static class NapkinTabCellRenderer extends AbstractTabCellRenderer {
        
        NapkinTabCellRenderer() {
            super(new NapkinTabPainter(), new Dimension(0, 0));
        }
        
        @Override
        public Color getSelectedForeground() {
            return NapkinUtil.currentTheme(this).getPenColor();
        }

        @Override
        public Color getSelectedBackground() {
            return CLEAR;
        }

        @Override
        public Color getSelectedActivatedForeground() {
            return NapkinUtil.currentTheme(this).getPenColor();
        }

        @Override
        public Color getSelectedActivatedBackground() {
            return CLEAR;
        }

        @Override
        protected int getCaptionYAdjustment() {
            return -5;
        }
    }
    
    /** Creates a new instance of NapkinEditorTabDisplayerUI */
    public NapkinEditorTabDisplayerUI(TabDisplayer tabDisplayer) {
        super(tabDisplayer);
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new NapkinEditorTabDisplayerUI((TabDisplayer) c);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        int prefHeight = 28;
        Graphics g = BasicScrollingTabDisplayerUI.getOffscreenGraphics();
        if (g != null) {
            Font font = NapkinUtil.currentTheme(displayer).getBoldTextFont();
            FontMetrics fm = g.getFontMetrics(font);
            Insets ins = getTabAreaInsets();
            prefHeight = fm.getHeight() + ins.top + ins.bottom + 6;
        }
        return new Dimension(displayer.getWidth(), prefHeight);
    }
    
    protected LayoutManager createLayout() {
        return new LayoutManager() {
            public void addLayoutComponent(String name, Component comp) {
            }
            /**
             * This is for laying out control buttons
             */
            public void layoutContainer(Container parent) {
                Insets in = getTabAreaInsets();
                final int y = in.top + 3;
                int x = parent.getWidth() - in.right;
                for (Component c : parent.getComponents()) {
                    Dimension size = c.getPreferredSize();
                    c.setBounds(x, y, size.width, size.height);
                    x += size.width + 5;
                }
            }
            public Dimension minimumLayoutSize(Container parent) {
                return getPreferredSize(null);
            }
            public Dimension preferredLayoutSize(Container parent) {
                return getPreferredSize(null);
            }
            public void removeLayoutComponent(Component comp) {
            }
        };
    }
    
    private void _setupButton(JButton button, int direction) {
        button.setIcon(NapkinIconFactory.createArrowIcon(direction, 13));
        button.setMargin(null);
        button.setText(null);
        button.putClientProperty("hideActionText", Boolean.TRUE);
        button.setBorderPainted(false);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(15, 15));
    }

    protected AbstractButton[] createControlButtons() {
        JButton[] result = new JButton[3];
        Action backward = scroll().getBackwardAction();
        Action forward = scroll().getForwardAction();

        result[0] = new JButton(backward);
        result[1] = new JButton(forward);
        result[2] = new JButton(new TabListPopupAction(displayer));

        _setupButton(result[0], WEST);
        _setupButton(result[1], EAST);
        _setupButton(result[2], SOUTH);

        backward.putValue("control", displayer);
        forward.putValue("control", displayer);
        
        PropertyChangeListener pcl = new PropertyChangeListener() {
            private final NapkinEditorTabDisplayerUI ui =
                    NapkinEditorTabDisplayerUI.this;
            public void propertyChange(PropertyChangeEvent evt) {
                ui.moved = true;
            }
        };
        backward.addPropertyChangeListener(pcl);
        forward.addPropertyChangeListener(pcl);

        return result;
    }

    protected TabCellRenderer createDefaultRenderer() {
        return new NapkinTabCellRenderer();
    }

    public Insets getTabAreaInsets() {
        return new Insets(0, 0, 0, 60);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    private final DrawnLineHolder[] holders =
            new DrawnLineHolder[] {
                new DrawnLineHolder(new DrawnCubicLineGenerator(), false),
                new DrawnLineHolder(new DrawnCubicLineGenerator(), false),
            };
    private Dimension oldSize = new Dimension();
    private int lastIndex = -2;
    private Rectangle[] coordinateCache = new Rectangle[2];
    private boolean moved = false;

    private boolean needsFullUpdate() {
        return moved || lastIndex != selectionModel.getSelectedIndex() ||
                !oldSize.equals(displayer.getSize());
    }
    
    private void updateOldParams() {
        moved = false;
        lastIndex = selectionModel.getSelectedIndex();
        oldSize = displayer.getSize();
    }
    
    @Override
    protected void paintAfterTabs(Graphics g) {
        int index = selectionModel.getSelectedIndex();
        Rectangle clipBounds = g.getClipBounds();
        if (needsFullUpdate()) {
            updateOldParams();
            Rectangle bounds = new Rectangle();
            getTabRect(index, bounds);
            int dx = bounds.x + bounds.width + 3;
            if (index >= 0) {
                coordinateCache[0] =
                    new Rectangle(
                        clipBounds.x,
                        clipBounds.y + clipBounds.height - 2,
                        bounds.x,
                        4
                    );
                coordinateCache[1] =
                    new Rectangle(
                        clipBounds.x + dx,
                        clipBounds.y + clipBounds.height - 2,
                        clipBounds.width - dx,
                        4
                    );
            } else {
                coordinateCache[0] = clipBounds;
            }
        }
        // paint from left to tab / right, depending on the case
        holders[0].shapeUpToDate(coordinateCache[0], null);
        holders[0].draw(g);
        if (index >= 0) {
            // paint from tab to right
            holders[1].shapeUpToDate(coordinateCache[1], null);
            holders[1].draw(g);
        }
        super.paintAfterTabs(g);
    }
}
