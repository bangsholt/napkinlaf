// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import napkin.shapes.DrawnCubicLineGenerator;
import napkin.shapes.DrawnLineHolder;
import napkin.util.NapkinConstants;
import napkin.util.NapkinPainter;
import napkin.util.NapkinTextPainter;
import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

public class NapkinRadioButtonUI extends BasicRadioButtonUI
        implements NapkinTextPainter, NapkinConstants, NapkinPainter {
    private DrawnLineHolder line;
    private boolean defaultsInstalled;

    private NapkinRadioButtonUI() {
    }

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
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
        NapkinUtil
                .paintButtonText(g, c, textRect, text, getTextShiftOffset(),
                        line,
                        c.isFocusOwner(), this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        super.paintText(g, c, textRect, text);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

