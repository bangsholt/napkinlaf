// $Id$

package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import static javax.swing.JEditorPane.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinEditorPaneUI extends BasicEditorPaneUI
        implements NapkinPainter {
    private Object origHonor;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinEditorPaneUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        origHonor = c.getClientProperty(HONOR_DISPLAY_PROPERTIES);
        c.putClientProperty(HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    }

    @Override
    public void uninstallUI(JComponent c) {
        c.putClientProperty(HONOR_DISPLAY_PROPERTIES, origHonor);
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
}

