// $Id$

package napkin;

import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinToolBarSeparatorUI extends BasicToolBarSeparatorUI
        implements NapkinPainter {
    private final NapkinSeparatorUI.Separator separator =
            new NapkinSeparatorUI.Separator();

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinToolBarSeparatorUI();
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

