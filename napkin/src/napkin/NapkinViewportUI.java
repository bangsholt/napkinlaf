// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinViewportUI extends BasicViewportUI
        implements NapkinConstants {

    private static final NapkinViewportUI napkinViewportUI =
            new NapkinViewportUI();

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, napkinViewportUI);
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
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}

