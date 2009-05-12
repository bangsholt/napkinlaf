package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinToolBarSeparatorUI extends BasicToolBarSeparatorUI
        implements NapkinPainter {

    private final NapkinSeparatorUI.Separator separator =
            new NapkinSeparatorUI.Separator();

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinToolBarSeparatorUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        separator.paint(g, (JSeparator) c);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return separator.getPreferredSize((JSeparator) c);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }
}

