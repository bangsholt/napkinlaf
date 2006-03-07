// $Id$

package napkin;

import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinMenuBarUI extends BasicMenuBarUI implements NapkinPainter {
    private Border oldBorder;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinMenuBarUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        oldBorder = c.getBorder();
        c.setBorder(null);
    }

    public void uninstallUI(JComponent c) {
        c.setBorder(oldBorder);
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

