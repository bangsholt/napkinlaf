// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinRootPaneUI extends BasicRootPaneUI {

    private static final NapkinRootPaneUI napkinRootPaneUI = new NapkinRootPaneUI();

    protected void installComponents(JRootPane jRootPane) {
        super.installComponents(jRootPane);
        JComponent content = (JComponent) jRootPane.getContentPane();
        content.setOpaque(false);
        JLayeredPane lp = jRootPane.getLayeredPane();
        NapkinUtil.setBackground(lp, NapkinBackground.NAPKIN_BG);
    }

    protected void uninstallComponents(JRootPane jRootPane) {
        super.uninstallComponents(jRootPane);
        JLayeredPane lp = jRootPane.getLayeredPane();
        NapkinUtil.uninstallLayeredPane(lp);
    }

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, napkinRootPaneUI);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        super.paint(g, c);
    }
}

