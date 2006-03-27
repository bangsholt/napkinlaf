// $Id$

package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;
import static net.sourceforge.napkinlaf.util.NapkinConstants.HIGHLIGHT_CLEAR;
import static net.sourceforge.napkinlaf.util.NapkinConstants.HIGHLIGHT_KEY;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class NapkinLabelUI extends BasicLabelUI implements NapkinPainter {
    private static final NapkinLabelUI ui = new NapkinLabelUI();

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return ui;
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
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }

    @Override
    protected void paintDisabledText(JLabel l, Graphics g, String s, int textX,
            int textY) {
        paintEnabledText(l, g, s, textX, textY);
    }
}

