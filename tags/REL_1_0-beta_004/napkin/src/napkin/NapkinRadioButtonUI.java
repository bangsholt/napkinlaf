// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinRadioButtonUI extends BasicRadioButtonUI
        implements NapkinPainter, NapkinConstants {

    private DrawnLineHolder line;
    private boolean defaultsInstalled;

    private NapkinRadioButtonUI() {
    }

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinRadioButtonUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        icon = null;
        super.uninstallUI(c);
    }

    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        if (!defaultsInstalled) {
            icon = UIManager.getIcon(NapkinUtil.property(this, "icon"));
            defaultsInstalled = true;
        }
    }

    protected void paintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {

        if (line == null)
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        NapkinUtil.paintText(g, c, textRect, text, getTextShiftOffset(), line, false, this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        super.paintText(g, c, textRect, text);
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}
