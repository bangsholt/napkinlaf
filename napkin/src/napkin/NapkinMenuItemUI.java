// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinMenuItemUI extends BasicMenuItemUI implements NapkinPainter {

    private DrawnLineHolder line;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinMenuItemUI());
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
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        NapkinUtil.paintText(g, item, textRect, text, 0, line, false, this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        super.paintText(g, (JMenuItem) c, textRect, text);
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinTheme theme = NapkinUtil.background(g, c);
        Color penColor = theme.getPenColor();
        if (NapkinUtil.replace(selectionForeground, penColor))
            selectionForeground = penColor;
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}

