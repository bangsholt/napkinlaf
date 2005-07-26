// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinViewportUI extends BasicViewportUI
        implements NapkinConstants, NapkinPainter {
    private int revertScrollMode;

    private static final NapkinViewportUI napkinViewportUI =
            new NapkinViewportUI();
    private JViewport viewport;

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, napkinViewportUI);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        viewport = (JViewport) c;
        forceScrollMode();
    }

    private void forceScrollMode() {
        revertScrollMode = viewport.getScrollMode();
        viewport.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        if (revertScrollMode != viewport.getScrollMode())
            viewport.setScrollMode(revertScrollMode);
        super.uninstallUI(c);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        if (viewport.getScrollMode() != JViewport.SIMPLE_SCROLL_MODE)
            forceScrollMode();
        super.update(g, c);
    }
}

