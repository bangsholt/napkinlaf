// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinScrollBarUI extends BasicScrollBarUI
        implements NapkinPainter {

    private DrawnLineHolder track;
    private final boolean vertical;
    private DrawnBoxHolder thumb;

    public NapkinScrollBarUI(JScrollBar bar) {
        vertical = (bar.getOrientation() == VERTICAL);
    }

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinScrollBarUI(((JScrollBar) c)));
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    protected JButton createDecreaseButton(int orientation) {
        return NapkinUtil.createArrowButton(orientation);
    }

    protected JButton createIncreaseButton(int orientation) {
        return NapkinUtil.createArrowButton(orientation);
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(c.getForeground());
        track = NapkinUtil.paintLine(g, vertical, track, trackBounds);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumb == null)
            thumb = new DrawnBoxHolder();
        thumb.shapeUpToDate(thumbBounds);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(c.getForeground());
        lineG.translate(thumbBounds.x, thumbBounds.y);
        thumb.draw(lineG);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        super.update(g, c);
    }
}
