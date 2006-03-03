// $Id$

package napkin;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

import napkin.NapkinTheme;
import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

public class NapkinMenuBarUI extends BasicMenuBarUI implements NapkinPainter {

    private Border oldBorder;

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinMenuBarUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        oldBorder = c.getBorder();
        c.setBorder(null);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        c.setBorder(oldBorder);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

