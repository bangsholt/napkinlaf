package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinSeparatorUI extends BasicSeparatorUI
        implements NapkinPainter {
    private final Separator separator = new Separator();

    public static class Separator {
        private DrawnLineHolder line;

        public void paint(Graphics g, JComponent c) {
            JSeparator sep = (JSeparator) c;
            boolean isVertical =
                    sep.getOrientation() == SwingConstants.VERTICAL;
            doPaint(g, sep, isVertical);
        }

        private void doPaint(Graphics g, JSeparator sep, boolean vertical) {
            if (line == null)
                line =
                        new DrawnLineHolder(DrawnCubicLineGenerator.INSTANCE,
                                vertical);

            Rectangle bounds = sep.getBounds();
            bounds.x = bounds.y = 0;
            if (vertical)
                bounds.x = sep.getWidth() / 2;
            else
                bounds.y = sep.getHeight() / 2;
            line.shapeUpToDate(bounds, null);
            g.setColor(sep.getForeground());
            line.draw(g);
        }

        @SuppressWarnings({"MethodMayBeStatic"})
        public Dimension getPreferredSize(JComponent c) {
            if (((JSeparator) c).getOrientation() == SwingConstants.VERTICAL)
                return new Dimension(5, 0);
            else
                return new Dimension(0, 5);
        }
    }

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinSeparatorUI();
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
    public void paint(Graphics g, JComponent c) {
        separator.paint(g, c);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return separator.getPreferredSize(c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

