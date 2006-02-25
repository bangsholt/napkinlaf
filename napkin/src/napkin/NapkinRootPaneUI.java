// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import napkin.util.NapkinPainter;
import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

public class NapkinRootPaneUI extends BasicRootPaneUI implements NapkinPainter {
    private static final NapkinRootPaneUI napkinRootPaneUI = new NapkinRootPaneUI();

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, napkinRootPaneUI);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
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

