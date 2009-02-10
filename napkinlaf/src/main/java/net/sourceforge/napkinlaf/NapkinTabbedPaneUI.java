package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.borders.NapkinBoxBorder;
import net.sourceforge.napkinlaf.shapes.DrawnBoxHolder;
import net.sourceforge.napkinlaf.shapes.DrawnTabHolder;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.geom.*;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinTabbedPaneUI extends BasicTabbedPaneUI
        implements NapkinPainter {
    private DrawnTabHolder[] tabs = new DrawnTabHolder[0];
    private final DrawnBoxHolder contentBorder = new DrawnBoxHolder();
    private Insets origInsets;

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinTabbedPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        origInsets = contentBorderInsets;
        contentBorderInsets = NapkinBoxBorder.LARGE_DEFAULT_INSETS;
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        contentBorderInsets = origInsets;
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        int count = ((JTabbedPane) c).getTabCount();
        DrawnTabHolder[] newTabs = new DrawnTabHolder[count];
        System.arraycopy(tabs, 0, newTabs, 0, Math.min(tabs.length,
                newTabs.length));
        tabs = newTabs;
        super.paint(g, c);
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
            int x, int y, int w, int h, boolean isSelected) {

        DrawnTabHolder tabHolder = tabs[tabIndex];
        if (tabHolder == null) {
            tabHolder = tabs[tabIndex] = new DrawnTabHolder(tabPlacement);
        }
        tabHolder.shapeUpToDate(tabPlacement, x, y, w, h);
        g.setColor(tabPane.getForeground());
        tabHolder.draw(g);
    }

    /**
     * BasicTabbedPaneUI.paintContentBorder calculates the content rectangle but
     * gives no way to get that data directly.  It just uses it to call a
     * side-specific border painting (I'm working with JDK 1.4.2).  So to get
     * that data, I have to override each side-specific border paiting method
     * and have one actually draw the entire border with the others doing
     * nothing. (It's just a lot easier for me to do my work in one fell swoop
     * than part-by-part).
     * <p/>
     * {@inheritDoc}
     */
    @Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {

        calcRect.x = x;
        calcRect.y = y;
        calcRect.width = w;
        calcRect.height = h;
        if (selectedIndex < 0) {
            contentBorder.shapeUpToDate(calcRect);
        } else {
            DrawnTabHolder tab = tabs[selectedIndex];
            if (tab == null) {
                contentBorder.shapeUpToDate(calcRect);
            } else {
                Point2D beg = tab.getBreakBeg();
                Point2D end = tab.getBreakEnd();
                contentBorder.shapeUpToDate(calcRect, tabPlacement, beg.getX(),
                        beg.getY(), end.getX(), end.getY());
            }
        }
        g.setColor(tabPane.getForeground());
        g.translate(+calcRect.x, +calcRect.y);
        contentBorder.draw(g);
        g.translate(-calcRect.x, -calcRect.y);
    }

    @Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {
        // see comment for paintContentBorderBottomEdge
    }

    @Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {
        // see comment for paintContentBorderBottomEdge
    }

    @Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {
        // see comment for paintContentBorderBottomEdge
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement,
            int tabIndex, int x, int y, int w, int h, boolean isSelected) {

        NapkinUtil.paintBackground(g, tabPane, new Rectangle(x, y, w, h));
    }
}
