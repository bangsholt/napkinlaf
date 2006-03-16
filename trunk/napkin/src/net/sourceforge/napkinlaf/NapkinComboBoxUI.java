// $Id$

package net.sourceforge.napkinlaf;

import static net.sourceforge.napkinlaf.util.NapkinConstants.SOUTH;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinComboBoxUI extends BasicComboBoxUI
        implements NapkinPainter {

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinComboBoxUI();
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
    protected JButton createArrowButton() {
        return NapkinUtil.createArrowButton(SOUTH);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
            boolean hasFocus) {

        // we don't want any special background
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

