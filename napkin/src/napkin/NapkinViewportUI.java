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
        NapkinUtil.installUI(c);
        super.installUI(c);
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

