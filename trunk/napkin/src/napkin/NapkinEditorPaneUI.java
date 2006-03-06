// $Id$

package napkin;

import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

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

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        origHonor = c.getClientProperty(HONOR_DISPLAY_PROPERTIES);
        c.putClientProperty(HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    }

    public void uninstallUI(JComponent c) {
        c.putClientProperty(HONOR_DISPLAY_PROPERTIES, origHonor);
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

