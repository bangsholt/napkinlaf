// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinComboBoxUI extends BasicComboBoxUI
        implements NapkinPainter, NapkinConstants {

    /**
     * @noinspection MethodOverridesStaticMethod
     */
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
        return NapkinUtil.createArrowButton(SOUTH);
    }

    /**
     * @noinspection ParameterHidingMemberVariable
     */
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
            boolean hasFocus) {

        return; // we don't want any special background
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

