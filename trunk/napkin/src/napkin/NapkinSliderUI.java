// $Id$

package napkin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinSliderUI extends BasicSliderUI implements NapkinConstants {
    private Icon thumb;
    private DrawnLineHolder track;
    private final Rectangle trackBounds;
    private final boolean vertical;
    private final List major;
    private int majorPos;
    private final List minor;
    private int minorPos;
    private final Rectangle tickBounds;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinSliderUI((JSlider) c));
    }

    private NapkinSliderUI(JSlider c) {
        super(c);
        vertical = (((JSlider) c).getOrientation() == JSlider.VERTICAL);
        trackBounds = new Rectangle();

        tickBounds = new Rectangle();
        major = new ArrayList(0);
        minor = new ArrayList(0);
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        majorPos = minorPos = 0;
        super.paint(g, c);
    }

    protected Dimension getThumbSize() {
        if (thumb == null) {
            int dir = SOUTH;
            if (vertical)
                dir = (NapkinUtil.isLeftToRight(slider) ? EAST : WEST);
            thumb = NapkinIconFactory.createArrowIcon(dir);
        }
        return new Dimension(thumb.getIconWidth(), thumb.getIconHeight());
    }

    public void paintThumb(Graphics g) {
        thumb.paintIcon(slider, g, thumbRect.x, thumbRect.y);
    }

    public void paintTrack(Graphics g) {
        trackBounds.width = trackRect.width;
        trackBounds.height = trackRect.height;
        trackBounds.x = trackRect.x;
        trackBounds.y = trackRect.y;

        track = NapkinUtil.paintLine(g, vertical, track, trackBounds);
    }

    protected void paintMinorTickForHorizSlider(Graphics g,
            Rectangle tickBounds, int x) {
        paintTick(minor, minorPos++, g, x, 0, x, tickBounds.height / 2 - 1);
    }

    protected void paintMajorTickForHorizSlider(Graphics g,
            Rectangle tickBounds, int x) {
        paintTick(major, majorPos++, g, x, 0, x, tickBounds.height - 2);
    }

    protected void paintMinorTickForVertSlider(Graphics g,
            Rectangle tickBounds, int y) {
        paintTick(minor, minorPos++, g, 0, y, tickBounds.width / 2 - 1, y);
    }

    protected void paintMajorTickForVertSlider(Graphics g,
            Rectangle tickBounds, int y) {
        paintTick(major, majorPos++, g, 0, y, tickBounds.width - 2, y);
    }

    private void paintTick(List ticks, int pos, Graphics g, int x, int y,
            int width, int height) {

        boolean vertTicks = !vertical;
        tickBounds.x = x;
        tickBounds.y = y;
        tickBounds.width = width;
        tickBounds.height = height;

        // I don't know why this is needed, but it works
        if (vertTicks)
            tickBounds.x /= 4;
        else
            tickBounds.y /= 4;

        while (pos >= ticks.size())
            ticks.add(null);
        DrawnLineHolder holder = (DrawnLineHolder) ticks.get(pos);
        holder = NapkinUtil.paintLine(g, vertTicks, holder, tickBounds);
        ticks.set(pos, holder);
    }

    public void update(Graphics g, JComponent c) {
        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.background(g, c);
        super.update(g, c);
        NapkinUtil.finishGraphics(g, c);
    }
}

