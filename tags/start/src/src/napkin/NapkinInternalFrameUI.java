// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinInternalFrameUI extends BasicInternalFrameUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c,
                new NapkinInternalFrameUI((JInternalFrame) c));
    }

    private NapkinInternalFrameUI(JInternalFrame c) {
        super(c);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        c.setOpaque(true);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }
}

