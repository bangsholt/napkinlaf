// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

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
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    protected JButton createArrowButton() {
        return NapkinUtil.createArrowButton(SOUTH, comboBox);
    }

    protected ComboPopup createPopup() {
        NapkinComboPopup popup = new NapkinComboPopup(comboBox);
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    protected ListCellRenderer createRenderer() {
        return new RenderResource();
    }

    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
            boolean hasFocus) {

        return; // we don't want any special background
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}
