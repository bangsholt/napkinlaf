package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.borders.NapkinCompoundBorder;
import net.sourceforge.napkinlaf.borders.NapkinLineBorder;
import static net.sourceforge.napkinlaf.util.NapkinConstants.SOUTH;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinComboBoxUI extends BasicComboBoxUI implements NapkinPainter {

    /**
     * !! Due to "GES, 980818" in BasicComboBoxUI, which seems to be a
     * workaround for java bug 4168483, we need to get set our popup to
     * non-light-weight in order for the workaround's setPopupVisible(false) not
     * to fire, which results in the popup menu disappearing upon
     * focusGained().
     * <p/>
     * The "default" behaviour causes scroll bars to be seemed as
     * non-functional
     */
    private boolean wasLightWeightPopupEnabled;

    private Border oldBorder;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinComboBoxUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        oldBorder = c.getBorder();
        c.setBorder(new NapkinCompoundBorder(new NapkinLineBorder(false),
                new EmptyBorder(0, 0, 3, 0)));
        NapkinUtil.installUI(c);
        wasLightWeightPopupEnabled = comboBox.isLightWeightPopupEnabled();
        comboBox.setLightWeightPopupEnabled(false);
    }

    @Override
    public void uninstallUI(JComponent c) {
        comboBox.setLightWeightPopupEnabled(wasLightWeightPopupEnabled);
        NapkinUtil.uninstallUI(c);
        c.setBorder(oldBorder);
        super.uninstallUI(c);
    }

    @Override
    protected JButton createArrowButton() {
        return NapkinUtil.createArrowButton(SOUTH);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
            boolean hasTheFocus) {
        // we don't want any special background
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }
}

