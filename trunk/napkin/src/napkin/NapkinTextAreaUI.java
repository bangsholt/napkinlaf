// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinTextAreaUI extends BasicTextAreaUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinTextAreaUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        c.setMaximumSize(new Dimension(1, 1));
    }

    protected void paintSafely(Graphics g) {
        NapkinUtil.defaultGraphics(g);
        super.paintSafely(g);
    }
}

