// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI
        implements NapkinPainter {

    private LineHolder line;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinRadioButtonMenuItemUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
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

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}

