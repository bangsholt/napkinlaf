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
        Color bg = c.getBackground();
        super.installUI(c);
        NapkinUtil.installUI(c);
        c.setOpaque(true);
        c.setBackground(bg);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}
