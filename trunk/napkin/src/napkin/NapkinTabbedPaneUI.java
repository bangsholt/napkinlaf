// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinTabbedPaneUI extends BasicTabbedPaneUI {
    private TabHolder[] tabs = new TabHolder[0];
    private final BoxHolder contentBorder = new BoxHolder();
    private Insets origInsets;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinTabbedPaneUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        origInsets = contentBorderInsets;
        contentBorderInsets = DrawnBorder.DEFAULT_INSETS;
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        contentBorderInsets = origInsets;
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        int count = ((JTabbedPane) c).getTabCount();
        tabs = (TabHolder[]) NapkinUtil.reallocate(tabs, count);
        super.paint(g, c);
    }

    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
            int x, int y, int w, int h, boolean isSelected) {

        TabHolder tabHolder = tabs[tabIndex];
        if (tabHolder == null)
            tabHolder = tabs[tabIndex] = new TabHolder(tabPlacement);
        tabHolder.shapeUpToDate(tabPlacement, x, y, w, h);
        g.setColor(Color.black);
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
     */
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {

        calcRect.x = x;
        calcRect.y = y;
        calcRect.width = w;
        calcRect.height = h;
        if (selectedIndex < 0)
            contentBorder.shapeUpToDate(calcRect);
        else {
            TabHolder tab = tabs[selectedIndex];
            Point2D beg = tab.getBreakBeg();
            Point2D end = tab.getBreakEnd();
            contentBorder.shapeUpToDate(calcRect, tabPlacement,
                    beg.getX(), beg.getY(), end.getX(), end.getY());
        }
        g.setColor(Color.black);
        g.translate(+calcRect.x, +calcRect.y);
        contentBorder.draw(g);
        g.translate(-calcRect.x, -calcRect.y);
    }

    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {
        return; // see comment for paintContentBorderBottomEdge
    }

    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {
        return; // see comment for paintContentBorderBottomEdge
    }

    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement,
            int selectedIndex, int x, int y, int w, int h) {
        return; // see comment for paintContentBorderBottomEdge
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}

