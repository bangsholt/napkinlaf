package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnBoxHolder;
import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;

@SuppressWarnings(
        {"QuestionableName", "WeakerAccess", "MethodOverridesStaticMethodOfSuperclass"})
public class NapkinScrollBarUI extends BasicScrollBarUI
        implements NapkinPainter {
    private DrawnLineHolder track;
    private final boolean vertical;
    private DrawnBoxHolder thumb;

    @SuppressWarnings({"TypeMayBeWeakened"})
    public NapkinScrollBarUI(JScrollBar bar) {
        vertical = (bar.getOrientation() == VERTICAL);
    }

    @SuppressWarnings({"TypeMayBeWeakened"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinScrollBarUI(((JScrollBar) c));
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
    protected JButton createDecreaseButton(int orientation) {
        return NapkinUtil.createArrowButton(orientation);
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return NapkinUtil.createArrowButton(orientation);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(c.getForeground());
        track = NapkinUtil.paintLine(g, vertical, track, trackBounds);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumb == null) {
            thumb = new DrawnBoxHolder();
        }
        thumb.shapeUpToDate(thumbBounds);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(c.getForeground());
        lineG.translate(thumbBounds.x, thumbBounds.y);
        thumb.draw(lineG);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }
}
