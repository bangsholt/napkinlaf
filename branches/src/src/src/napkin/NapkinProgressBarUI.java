// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinProgressBarUI extends BasicProgressBarUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinProgressBarUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }
}

