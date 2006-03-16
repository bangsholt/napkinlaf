// $Id$

package napkin.borders;

import napkin.shapes.DrawnCubicLineGenerator;
import napkin.shapes.DrawnLineHolder;

import java.awt.*;

public class NapkinLineBorder extends AbstractNapkinBorder {
    private final boolean vertical;
    private DrawnLineHolder line;

    @SuppressWarnings({"SameParameterValue"})
    public NapkinLineBorder(boolean vertical) {
        super();
        this.vertical = vertical;
    }

    @Override
    protected Insets doGetBorderInsets(Component c, Insets insets) {
        if (vertical) {
            insets.set(0, 0, 0, NapkinBoxBorder.SMALL_DEFAULT_INSETS.right);
        } else {
            insets.set(0, 0, NapkinBoxBorder.SMALL_DEFAULT_INSETS.bottom, 0);
        }
        return insets;
    }

    @Override
    public void doPaintBorder(Component c, Graphics g1, int x, int y,
            int width, int height) {

        Graphics2D g = (Graphics2D) g1;
        Rectangle passed = new Rectangle(x, y, width, height);
        if (line == null)
            line =
                    new DrawnLineHolder(DrawnCubicLineGenerator.INSTANCE,
                            vertical);
        line.shapeUpToDate(passed, null);

        Insets insets = getBorderInsets(c);
        if (insets.bottom != 0)
            y += c.getHeight() - insets.bottom;
        else
            x += c.getWidth() - insets.right;
        g.translate(x, y);
        line.draw(g);
        g.translate(-x, -y);
    }
}
