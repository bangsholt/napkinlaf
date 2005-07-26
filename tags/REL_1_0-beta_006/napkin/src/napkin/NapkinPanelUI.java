// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinPanelUI extends BasicPanelUI implements NapkinPainter {
    private static final NapkinPanelUI napkinPanelUI = new NapkinPanelUI();

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, napkinPanelUI);
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

