// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinToolTipUI extends BasicToolTipUI implements NapkinConstants {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinToolTipUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        Color bg = c.getBackground();
        NapkinUtil.installUI(c);
        c.setOpaque(true);
        c.setBackground(bg);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }
}

