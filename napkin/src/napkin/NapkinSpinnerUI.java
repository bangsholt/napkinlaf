// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinSpinnerUI extends BasicSpinnerUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinSpinnerUI());
    }

    public void installUI(JComponent c) {
        NapkinUtil.installUI(c);
        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        NapkinUtil.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

