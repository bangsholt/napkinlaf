// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinSplitPaneUI extends BasicSplitPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinSplitPaneUI());
    }

    public BasicSplitPaneDivider createDefaultDivider() {
        return new NapkinSplitPaneDivider(this);
    }

    public void installUI(JComponent c) {
        NapkinUtil.installUI(c);
        super.installUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }

    protected void resetLayoutManager() {
        super.resetLayoutManager();
        int orientation = splitPane.getOrientation();
        ((NapkinSplitPaneDivider) divider).setOrientation(orientation);
    }
}

