// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinPopupMenuUI extends BasicPopupMenuUI
        implements NapkinConstants {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinPopupMenuUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        c.putClientProperty(PENDING_BG_COMPONENT, NapkinBackground.POSTIT_BG);
        JPopupMenu m = (JPopupMenu) c;
        m.addFocusListener(new NapkinUtil.DumpListener());
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
//        NapkinUtil.removeBackground(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.applyPendingBackground(c);
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }
}

