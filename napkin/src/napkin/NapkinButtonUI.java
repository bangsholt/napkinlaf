// $Id$

package napkin;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinButtonUI extends BasicButtonUI implements NapkinPainter {
    private LineHolder line;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinButtonUI());
    }

    public NapkinButtonUI() {
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

    protected void paintText(Graphics g, JComponent c, Rectangle textRect,
                             String text) {

        if (line == null)
            line = new LineHolder(new CubicGenerator());
        boolean isDefault = ((JButton) c).isDefaultButton();
        int offset = getTextShiftOffset();
        NapkinUtil.paintText(g, c, textRect, text, offset, line, isDefault, this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        super.paintText(g, c, textRect, text);
    }

    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect,
                             String text) {

        paintText(g, (JComponent) b, textRect, text);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

