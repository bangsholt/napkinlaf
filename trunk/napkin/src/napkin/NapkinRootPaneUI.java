// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinRootPaneUI extends BasicRootPaneUI {

    private static final NapkinRootPaneUI napkinRootPaneUI = new NapkinRootPaneUI();

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, napkinRootPaneUI);
    }

    protected void installComponents(JRootPane jRootPane) {
        super.installComponents(jRootPane);
        JComponent content = (JComponent) jRootPane.getContentPane();
        content.setOpaque(false);
        JLayeredPane lp = jRootPane.getLayeredPane();
        NapkinUtil.setPaper(lp, NapkinBackground.NAPKIN_BG);
    }

    protected void uninstallComponents(JRootPane jRootPane) {
        super.uninstallComponents(jRootPane);
        JComponent content = (JComponent) jRootPane.getContentPane();
        content.setOpaque(true);
        JLayeredPane lp = jRootPane.getLayeredPane();
        NapkinUtil.removePaper(lp);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }
}

