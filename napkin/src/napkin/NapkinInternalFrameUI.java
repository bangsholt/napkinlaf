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
        NapkinUtil.setupPaper(c, NapkinBackground.POSTIT_BG);
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

    protected JComponent createNorthPane(JInternalFrame w) {
        titlePane = new NapkinInternalFrameTitlePane(w);
        return titlePane;
    }
}
