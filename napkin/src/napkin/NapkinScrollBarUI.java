// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinScrollBarUI extends BasicScrollBarUI {
    private LineHolder track;
    private final boolean vertical;
    private BoxHolder thumb;

    public NapkinScrollBarUI(JScrollBar bar) {
        vertical = (bar.getOrientation() == VERTICAL);
    }

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinScrollBarUI(((JScrollBar) c)));
    }

    public void installUI(JComponent c) {
        NapkinUtil.installUI(c);
        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        NapkinUtil.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        super.paint(g, c);
    }

    protected JButton createDecreaseButton(int orientation) {
        return NapkinUtil.createArrowButton(orientation);
    }

    protected JButton createIncreaseButton(int orientation) {
        return NapkinUtil.createArrowButton(orientation);
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        track = NapkinUtil.paintLine(g, vertical, track, trackBounds);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumb == null)
            thumb = new BoxHolder();
        thumb.shapeUpToDate(null, thumbBounds);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(Color.black);
        lineG.translate(thumbBounds.x, thumbBounds.y);
        thumb.draw(lineG);
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}