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
        NapkinUtil.installUI(c);
        super.installUI(c);
        c.setOpaque(true);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        NapkinUtil.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        super.paint(g, c);
    }

    protected JComponent createNorthPane(JInternalFrame w) {
        titlePane = new NapkinInternalFrameTitlePane(w);
        return titlePane;
    }
}
