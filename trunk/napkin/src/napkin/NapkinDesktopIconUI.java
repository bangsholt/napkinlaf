// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinDesktopIconUI extends BasicDesktopIconUI {
    // I cannot override the desktop icon, which is package
    // protected.  This means that I cannot change the BasicDesktopIconUI to
    // use a NapkinInternalFrameTitlePane, which is how I handle this stuff in
    // NapkinInternalFrameUI.  I have filed a bug

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinDesktopIconUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        NapkinUtil.setupPaper(c, NapkinTheme.Manager.getCurrentTheme().popup());
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

