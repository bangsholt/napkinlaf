// $Id$

package napkin;

import napkin.shapes.DrawnCubicLineGenerator;
import napkin.shapes.DrawnLineHolder;
import napkin.util.NapkinPainter;
import napkin.util.NapkinTextPainter;
import napkin.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI
        implements NapkinPainter, NapkinTextPainter {
    private DrawnLineHolder line;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinCheckBoxMenuItemUI();
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
        NapkinUtil
                .paintButtonText(g, item, textRect, text, 0, line, false, this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        super.paintText(g, (JMenuItem) c, textRect, text);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}
