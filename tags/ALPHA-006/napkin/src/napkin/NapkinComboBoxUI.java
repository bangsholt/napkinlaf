// $Id$

package napkin;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinComboBoxUI extends BasicComboBoxUI
        implements NapkinConstants {

    public static class RenderResource extends BasicComboBoxRenderer
            implements UIResource {

        RenderResource() {
            setOpaque(false);
        }
    }

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinComboBoxUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        listBox.setSelectionForeground(NapkinIconFactory.CheckBoxIcon.MARK_COLOR);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    protected JButton createArrowButton() {
        return NapkinUtil.createArrowButton(SOUTH);
    }

    protected ListCellRenderer createRenderer() {
        return new RenderResource();
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        super.paint(g, c);
    }

    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
                                            boolean hasFocus) {

        return; // we don't want any background
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

