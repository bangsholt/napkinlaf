package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import static net.sourceforge.napkinlaf.util.NapkinConstants.HIGHLIGHT_KEY;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinTreeUI extends BasicTreeUI implements NapkinPainter {

    // access-order based bounded cache
    public static class LineCache
            extends LinkedHashMap<Rectangle, DrawnLineHolder> {
        private static final int MAX_NUM_OF_LINES = 100;

        public LineCache() {
            super(16, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(
                Map.Entry<Rectangle, DrawnLineHolder> eldest) {
            return size() > MAX_NUM_OF_LINES;
        }
    }

    public static class DefaultNapkinTreeCellRender
            extends DefaultTreeCellRenderer implements NapkinPainter {
        @Override
        public void paint(Graphics g) {
            putClientProperty(HIGHLIGHT_KEY, selected);
            NapkinUtil.update(g, this, this);
        }

        public void superPaint(Graphics g, JComponent c) {
            super.paint(g);
        }

        @Override
        public Icon getOpenIcon() {
            return getDefaultOpenIcon();
        }

        @Override
        public Icon getLeafIcon() {
            return getDefaultLeafIcon();
        }

        @Override
        public Icon getClosedIcon() {
            return getDefaultClosedIcon();
        }
    }

    private final LineCache linesFor = new LineCache();

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinTreeUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }

    @Override
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top,
            int bottom) {
        paintLine(g, x, top, x, bottom);
    }

    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y,
            int left, int right) {
        paintLine(g, left, y, right, y);
    }

    private void paintLine(Graphics g, int x1, int y1, int x2, int y2) {
        int w = x2 - x1;
        int h = y2 - y1;
        Rectangle rect = new Rectangle(x1, y1, w, h);
        DrawnLineHolder holder = linesFor.get(rect);
        if (holder == null) {
            holder = new DrawnLineHolder(Math.max(w, h), w == 0);
            linesFor.put(rect, holder);
            // Unfortunately we can't know when a line is not needed anymore.
            // So we'll hold onto it forever.  We just hope this isn't *too*
            // bad a thing to do -- that things will not grow without bound.
            // Considering that a tree only has a limited range of possible
            // change in a real case this probably OK, but I'd like to do
            // something else if I could only think of it.
        }
        DrawnLineHolder line = holder;
        line.shapeUpToDate(rect, null);
/* !!
 * This line (mysteriously) causes "corruption" to paper background painting
 * when used with NetBean's collapsable tabs.
 */
//        NapkinUtil.syncWithTheme((Graphics2D) g, tree);
        line.draw(g);
    }

    @Override
    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
            Insets insets, TreePath path) {
        // if we restrict to the actual clip bounds then we will be trying to
        // paint subparts of the line.  But if we do that then we have no way
        // of knowing that the overall line size hasn't changed.  This means
        // that paintLine() will keep thinking it is drawing differnet lines and
        // it will look all choppy.  So we just have to live with redrawing more
        // than we have to so that the line painting stuff knows whether the
        // overall line has changed or not.
        Rectangle rect = tree.getBounds();
        rect.x = 0;
        rect.y = 0;
        super.paintVerticalPartOfLeg(g, rect, insets, path);
    }

    @Override
    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
            Insets insets, Rectangle bounds, TreePath path, int row,
            boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf) {

        // see comment for paintVerticalPartOfLeg()
        Rectangle rect = tree.getBounds();
        rect.x = 0;
        rect.y = 0;
        super.paintHorizontalPartOfLeg(g, rect, insets, bounds, path, row,
                isExpanded, hasBeenExpanded, isLeaf);
    }

    @Override
    protected Color getHashColor() {
        return NapkinUtil.currentTheme(tree).getPenColor();
    }

    @Override
    protected TreeCellRenderer createDefaultCellRenderer() {
        return new DefaultNapkinTreeCellRender();
    }
}

