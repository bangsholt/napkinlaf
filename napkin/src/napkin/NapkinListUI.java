// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinListUI extends BasicListUI {

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinListUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinTheme theme = NapkinUtil.background(g, c);
        Color penColor = theme.getPenColor();
        if (NapkinUtil.replace(list.getSelectionForeground(), penColor))
            list.setSelectionForeground(penColor);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}

