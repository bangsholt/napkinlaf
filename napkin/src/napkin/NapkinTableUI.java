// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import napkin.util.NapkinPainter;
import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

public class NapkinTableUI extends BasicTableUI implements NapkinPainter {
    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return new NapkinTableUI();
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
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        Color highlightColor = theme.getHighlightColor();
        if (NapkinUtil.replace(table.getSelectionBackground(), highlightColor))
            table.setSelectionBackground(highlightColor);
        Color penColor = theme.getPenColor();
        if (NapkinUtil.replace(table.getSelectionForeground(), penColor))
            table.setSelectionForeground(penColor);
        super.update(g, c);
    }
}

