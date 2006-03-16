// $Id: NapkinViewportUI.java 355 2006-03-15 09:15:55Z kcrca $

package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinViewportUI extends BasicViewportUI implements NapkinPainter {
    private int revertScrollMode;

    private static final NapkinViewportUI ui =
            new NapkinViewportUI();
    private JViewport viewport;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return ui;
    }

    @Override
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

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        if (revertScrollMode != viewport.getScrollMode())
            viewport.setScrollMode(revertScrollMode);
        super.uninstallUI(c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
//        if (viewport.getScrollMode() != JViewport.SIMPLE_SCROLL_MODE)
//            forceScrollMode();
        super.update(g, c);
    }
}

