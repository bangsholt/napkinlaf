// $Id$

package napkin;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import napkin.shapes.DrawnCubicLineGenerator;
import napkin.shapes.DrawnLineHolder;
import napkin.util.NapkinConstants;
import napkin.util.NapkinIconFactory;
import napkin.util.NapkinPainter;
import napkin.util.NapkinTextPainter;
import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

public class NapkinMenuUI extends BasicMenuUI
        implements NapkinTextPainter, NapkinPainter {
    private DrawnLineHolder line;
    private Icon oldArrowIcon;
    private PropertyChangeListener orientationListener =
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    ComponentOrientation orientation =
                            (ComponentOrientation) evt.getNewValue();
                    arrowIcon = NapkinIconFactory.createArrowIcon(
                            orientation.isLeftToRight() ?
                                NapkinConstants.EAST : NapkinConstants.WEST, 8);
                }
            };

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinMenuUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        oldArrowIcon = arrowIcon;
        boolean isLeftToRight = c.getComponentOrientation().isLeftToRight();
        arrowIcon = NapkinIconFactory.createArrowIcon(
                 isLeftToRight ? NapkinConstants.EAST : NapkinConstants.WEST, 8);
        c.addPropertyChangeListener("componentOrientation", orientationListener);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        arrowIcon = oldArrowIcon;
        c.removePropertyChangeListener(
                "componentOrientation", orientationListener);
        super.uninstallUI(c);
    }

    protected void paintText(Graphics g, JMenuItem item, Rectangle textRect,
            String text) {

        if (line == null)
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        NapkinUtil
                .paintButtonText(g, item, textRect, text, 0, line, false, this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        super.paintText(g, (JMenuItem) c, textRect, text);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}

