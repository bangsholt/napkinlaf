package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnLineHolder;
import static net.sourceforge.napkinlaf.util.NapkinConstants.*;
import net.sourceforge.napkinlaf.util.NapkinIconFactory;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinSliderUI extends BasicSliderUI implements NapkinPainter {
    private Icon thumb;
    private DrawnLineHolder track;
    private final Rectangle trackBounds;
    private final boolean vertical;
    private final List<DrawnLineHolder> major;
    private int majorPos;
    private final List<DrawnLineHolder> minor;
    private int minorPos;
    private final Rectangle tickBounds;
    private int textHeight;

    public static ComponentUI createUI(JComponent c) {
        return new NapkinSliderUI((JSlider) c);
    }

    private NapkinSliderUI(JSlider c) {
        super(c);
        vertical = (c.getOrientation() == SwingConstants.VERTICAL);
        trackBounds = new Rectangle();

        tickBounds = new Rectangle();
        major = new ArrayList<DrawnLineHolder>(0);
        minor = new ArrayList<DrawnLineHolder>(0);
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
    public void paint(Graphics g, JComponent c) {
        majorPos = minorPos = 0;
        super.paint(g, c);
    }

    @Override
    protected Dimension getThumbSize() {
        if (thumb == null) {
            int dir = SOUTH;
            if (vertical) {
                dir = (NapkinUtil.isLeftToRight(slider) ? EAST : WEST);
            }
            thumb = NapkinIconFactory.createArrowIcon(dir);
        }
        return new Dimension(thumb.getIconWidth(), thumb.getIconHeight());
    }

    /**
     * This is a copy of the code from the superclass because I need to expand
     * the clip rectangle -- the slider draws a bit outside of its area because
     * the lines are curved.  I wish there were a better way rather than the
     * copy/paste that caused this, but so far there isn't.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void setThumbLocation(int x, int y) {
        Rectangle unionRect = new Rectangle(thumbRect);

        thumbRect.setLocation(x, y);

        SwingUtilities.computeUnion(thumbRect.x, thumbRect.y, thumbRect.width,
                thumbRect.height, unionRect);
        slider.repaint(unionRect.x - 3, unionRect.y - 3, unionRect.width + 6,
                unionRect.height + 6);
    }

    @Override
    public void paintThumb(Graphics g) {
        thumb.paintIcon(slider, g, thumbRect.x, thumbRect.y);
    }

    @Override
    public void paintTrack(Graphics g) {
        trackBounds.width = trackRect.width;
        trackBounds.height = trackRect.height;
        trackBounds.x = trackRect.x;
        trackBounds.y = trackRect.y;
        g.setColor(slider.getForeground());

        track = NapkinUtil.paintLine(g, vertical, track, trackBounds);
    }

    @Override
    public void paintFocus(Graphics g) {
        // do nothing here -- we show focus by color on slider
    }

    @Override
    protected void paintMinorTickForHorizSlider(Graphics g, Rectangle tick,
            int x) {
        paintTick(minor, minorPos++, g, x, 0, x, tick.height / 2 - 1);
    }

    @Override
    protected void paintMajorTickForHorizSlider(Graphics g, Rectangle tick,
            int x) {
        paintTick(major, majorPos++, g, x, 0, x, tick.height - 2);
    }

    @Override
    protected void paintMinorTickForVertSlider(Graphics g, Rectangle tick,
            int y) {
        paintTick(minor, minorPos++, g, 0, y, tick.width / 2 - 1, y);
    }

    @Override
    protected void paintMajorTickForVertSlider(Graphics g, Rectangle tick,
            int y) {
        paintTick(major, majorPos++, g, 0, y, tick.width - 2, y);
    }

    private void paintTick(List<DrawnLineHolder> ticks, int pos, Graphics g,
            int x, int y, int width, int height) {

        boolean vertTicks = !vertical;
        tickBounds.x = x;
        tickBounds.y = y;
        tickBounds.width = width;
        tickBounds.height = height;

        // I don't know why this is needed, but it works
        if (vertTicks) {
            tickBounds.x /= 4;
        } else {
            tickBounds.y /= 4;
        }

        while (pos >= ticks.size()) {
            ticks.add(null);
        }
        DrawnLineHolder holder = ticks.get(pos);
        g.setColor(slider.getForeground());
        holder = NapkinUtil.paintLine(g, vertTicks, holder, tickBounds);
        ticks.set(pos, holder);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }

    @Override
    protected void paintHorizontalLabel(Graphics g, int value,
            Component label) {
        int labelCenter = xPositionForValue(value);
        int labelLeft = labelCenter - (label.getPreferredSize().width / 2);
        g.drawString(((JLabel) label).getText(), labelLeft, textHeight);
    }

    @Override
    protected void paintVerticalLabel(Graphics g, int value, Component label) {
        int labelCenter = yPositionForValue(value);
        int labelTop = labelCenter - (label.getPreferredSize().height / 2);
        g.drawString(((JLabel) label).getText(), 0, labelTop + textHeight);
    }

    @Override
    public void paintLabels(Graphics g) {
        Font oldFont = g.getFont();
        Font font = NapkinUtil.currentTheme(slider).getTextFont();
        g.setFont(font);
        textHeight = (int) (0.5f * font.getLineMetrics("0123456789",
                ((Graphics2D) g).getFontRenderContext()).getHeight() + 0.5f);
        super.paintLabels(g);
        g.setFont(oldFont);
    }
}

