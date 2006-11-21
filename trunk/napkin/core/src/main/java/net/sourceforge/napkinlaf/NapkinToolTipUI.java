package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinToolTipUI extends BasicToolTipUI implements NapkinPainter {

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinToolTipUI();
    }

    @Override
    public void installUI(JComponent c) {
        NapkinUtil.installUI(c);
        super.installUI(c);
        NapkinUtil.setupPaper(c, NapkinKnownTheme.POPUP_THEME);
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }
}

