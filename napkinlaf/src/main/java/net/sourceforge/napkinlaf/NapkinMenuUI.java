package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnCubicLineGenerator;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import static net.sourceforge.napkinlaf.util.NapkinConstants.*;
import net.sourceforge.napkinlaf.util.NapkinIconFactory;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinTextPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinMenuUI extends BasicMenuUI
        implements NapkinTextPainter, NapkinPainter {
    private DrawnLineHolder line;
    private Icon oldArrowIcon;
    private final PropertyChangeListener orientationListener =
            new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    ComponentOrientation orientation =
                            (ComponentOrientation) evt.getNewValue();
                    arrowIcon = NapkinIconFactory
                            .createArrowIcon(
                                    (orientation.isLeftToRight() ? EAST : WEST),
                                    8);
                }
            };

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinMenuUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        oldArrowIcon = arrowIcon;
        boolean isLeftToRight = c.getComponentOrientation().isLeftToRight();
        arrowIcon = NapkinIconFactory
                .createArrowIcon((isLeftToRight ? EAST : WEST), 8);
        c.addPropertyChangeListener("componentOrientation",
                orientationListener);
        NapkinUtil.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        arrowIcon = oldArrowIcon;
        c.removePropertyChangeListener("componentOrientation",
                orientationListener);
        super.uninstallUI(c);
    }

    @Override
    protected void paintText(Graphics g, JMenuItem item, Rectangle textRect,
            String text) {

        if (line == null) {
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        }
        NapkinUtil
                .paintButtonText(g, item, textRect, text, 0, line, false, this);
    }

    public void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        super.paintText(g, (JMenuItem) c, textRect, text);
    }

    /**
     * JMenu can either be on JMenuBar or within another JMenu and it could well
     * be changed throughout the life-time of the component, so we have to work
     * out the selectionForeground every time.
     *
     * @param c The component to udpate.
     */
    private void updateDefaultSelectionColor(Component c) {
        selectionForeground = NapkinUtil.currentTheme(c).getSelectionColor();
    }

    @Override
    protected PropertyChangeListener createPropertyChangeListener(
            JComponent c) {
        final PropertyChangeListener listener =
                super.createPropertyChangeListener(c);
        return new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                listener.propertyChange(evt);
                if (evt.getPropertyName().equals("ancestor")) {
                    updateDefaultSelectionColor((JComponent) evt.getSource());
                }
            }
        };
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }
}

