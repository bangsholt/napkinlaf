// $Id$

package napkin.borders;

import napkin.shapes.DrawnBoxHolder;
import static napkin.util.NapkinConstants.BORDER_KEY;
import napkin.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;

public class NapkinBoxBorder extends AbstractNapkinBorder {
    private static final int SMALL_BORDER = 3;
    private static final int LARGE_BORDER = 4;

    public static final Insets SMALL_DEFAULT_INSETS =
            new InsetsUIResource(SMALL_BORDER, SMALL_BORDER,
                    SMALL_BORDER, SMALL_BORDER);
    public static final Insets LARGE_DEFAULT_INSETS =
            new InsetsUIResource(LARGE_BORDER, LARGE_BORDER,
                    LARGE_BORDER, LARGE_BORDER);

    private static final NapkinUtil.PropertyFactory BOX_FACTORY =
            new NapkinUtil.PropertyFactory() {
                public Object createPropertyValue() {
                    return new DrawnBoxHolder();
                }
            };

    @Override
    protected void doPaintBorder(Component c, Graphics g1, int x, int y,
            int width, int height) {

        DrawnBoxHolder box = (DrawnBoxHolder)
                NapkinUtil.getProperty((JComponent) c, BORDER_KEY, BOX_FACTORY);

        Rectangle passed = new Rectangle(x, y, width, height);
        box.shapeUpToDate(passed);

        Graphics2D g = (Graphics2D) g1;
        g.translate(x, y);
        box.draw(g);
        g.translate(-x, -y);
    }

    public static Insets getDefaultInsets(Rectangle bounds, Insets insets) {
        int wi = bounds.height <= 100 ? SMALL_BORDER : LARGE_BORDER;
        int hi = bounds.width <= 100 ? SMALL_BORDER : LARGE_BORDER;
        insets.set(hi, wi, hi, wi);
        return insets;
    }

    public static Insets getDefaultInsets(Rectangle bounds) {
        return getDefaultInsets(bounds, new Insets(0, 0, 0, 0));
    }

    public static int getDelta(int num) {
        return num > SMALL_BORDER ? num >> 1 : num;
    }

    public static int getWidthDelta(Insets in) {
        return getDelta(in.left) + getDelta(in.right);
    }

    public static int getHeightDelta(Insets in) {
        return getDelta(in.top) + getDelta(in.bottom);
    }

    @Override
    public Insets doGetBorderInsets(Component c, Insets insets) {
        return getDefaultInsets(c.getBounds(), insets);
    }
}
