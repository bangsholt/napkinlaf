// $Id$

package napkin;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinMenuUI extends BasicMenuUI implements NapkinPainter {

    private LineHolder line;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinMenuUI());
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
        NapkinUtil.defaultGraphics(g, c);
        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }

    protected void paintText(Graphics g, JMenuItem item, Rectangle textRect,
                             String text) {

        if (line == null)
            line = new LineHolder(new CubicGenerator());
        NapkinUtil.paintText(g, item, textRect, text, 0, line, false, this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        super.paintText(g, (JMenuItem) c, textRect, text);
    }
}

