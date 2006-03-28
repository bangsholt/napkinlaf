// $Id$

package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinTextPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinCheckBoxUI extends BasicCheckBoxUI
        implements NapkinPainter, NapkinTextPainter {
    private DrawnLineHolder line;
    private boolean defaultsInstalled = false;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinCheckBoxUI();
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
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        if (!defaultsInstalled) {
            icon = UIManager.getIcon(NapkinUtil.property(this, "icon"));
            defaultsInstalled = true;
        }
    }

    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {

        if (line == null) {
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        }
        NapkinUtil.paintButtonText(g, c, textRect, text, getTextShiftOffset(),
                        line, c.isFocusOwner(), this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        super.paintText(g, c, textRect, text);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

