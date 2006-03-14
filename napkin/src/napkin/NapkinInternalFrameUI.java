// $Id$

package napkin;

import napkin.util.NapkinPainter;
import napkin.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinInternalFrameUI extends BasicInternalFrameUI
        implements NapkinPainter {

    public static ComponentUI createUI(JComponent c) {
        return new NapkinInternalFrameUI((JInternalFrame) c);
    }

    private NapkinInternalFrameUI(JInternalFrame c) {
        super(c);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        NapkinUtil.setupPaper(c, NapkinKnownTheme.POPUP_THEME);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }

    protected JComponent createNorthPane(JInternalFrame w) {
        titlePane = new NapkinInternalFrameTitlePane(w);
        return titlePane;
    }
}
