// $Id$

package napkin;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinSeparatorUI extends BasicSeparatorUI {
    private final Separator separator = new Separator();

    public static class Separator {
        private LineHolder line;

        public void paint(Graphics g, JComponent c) {
            JSeparator sep = (JSeparator) c;
            boolean isVertical = sep.getOrientation() == JSeparator.VERTICAL;
            doPaint(g, sep, isVertical);
        }

        private void doPaint(Graphics g, JSeparator sep, boolean vertical) {
            NapkinUtil.defaultGraphics(g, sep);
            if (line == null)
                line = new LineHolder(CubicGenerator.INSTANCE, vertical);

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

        public Dimension getPreferredSize(JComponent c) {
            if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL)
                return new Dimension(5, 0);
            else
                return new Dimension(0, 5);
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinSeparatorUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        separator.paint(g, c);
    }

    public Dimension getPreferredSize(JComponent c) {
        return separator.getPreferredSize(c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

