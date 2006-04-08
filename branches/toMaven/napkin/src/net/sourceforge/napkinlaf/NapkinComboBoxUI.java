package net.sourceforge.napkinlaf;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.event.ListDataListener;
import static net.sourceforge.napkinlaf.util.NapkinConstants.SOUTH;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

public class NapkinComboBoxUI extends BasicComboBoxUI
        implements NapkinPainter {

    /**!!
     * Due to "GES, 980818" in BasicComboBoxUI, which seems to be a workaround
     * for java bug 4168483, we need to get set our popup to non-light-weight
     * in order for the workaround's setPopupVisible(false) not to fire, which
     * results in the popup menu disappearing upon focusGained().
     *
     * The "default" behaviour causes scroll bars to be seemed as non-functional
     */
    private boolean wasLightWeightPopupEnabled;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinComboBoxUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
        wasLightWeightPopupEnabled = comboBox.isLightWeightPopupEnabled();
        comboBox.setLightWeightPopupEnabled(false);
    }

    @Override
    public void uninstallUI(JComponent c) {
        comboBox.setLightWeightPopupEnabled(wasLightWeightPopupEnabled);
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    @Override
    protected JButton createArrowButton() {
        return NapkinUtil.createArrowButton(SOUTH);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds,
            boolean hasFocus) {
        // we don't want any special background
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }

}

