// $Id$

package napkin;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.tree.*;

public class NapkinTreeUI extends BasicTreeUI {
    private Map linesFor = new HashMap();

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinTreeUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }

    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top,
            int bottom) {
        paintLine(g, x, top, x, bottom);
    }

    protected void paintHorizontalLine(Graphics g, JComponent c, int y,
            int left, int right) {
        paintLine(g, left, y, right, y);
    }

    private void paintLine(Graphics g, int x1, int y1, int x2, int y2) {
        int w = x2 - x1;
        int h = y2 - y1;
        Rectangle rect = new Rectangle(x1, y1, w, h);
        DrawnLineHolder holder = (DrawnLineHolder) linesFor.get(rect);
        if (holder == null) {
            holder = new DrawnLineHolder(Math.max(w, h), w == 0);
            linesFor.put(rect, holder);
            System.out.println("adding holder for " + rect);
        }
        DrawnLineHolder line = holder;
        line.shapeUpToDate(rect, null);
        NapkinUtil.syncWithTheme((Graphics2D) g, tree);
        line.draw(g);
    }

    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
            Insets insets, TreePath path) {
        // if we restrict to the actual clip bounds then we will be trying to
        // paint subparts of the line.  But if we do that then we have no way
        // of knowing that the overall line size hasn't changed.  This means
        // that paintLine() will keep thinking it is drawing differnet lines and
        // it will look all choppy.  So we just have to live with redrawing more
        // than we have to so that the line painting stuff knows whether the
        // overall line has changed or not.
        super.paintVerticalPartOfLeg(g, tree.getBounds(), insets, path);
    }

    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
            Insets insets, Rectangle bounds, TreePath path, int row,
            boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {

        // see comment for paintVerticalPartOfLeg()
        super.paintHorizontalPartOfLeg(g, clipBounds, insets, tree.getBounds(),
                path, row, isExpanded, hasBeenExpanded, isLeaf);
    }
}

