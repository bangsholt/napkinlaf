// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class NapkinBoxBorder extends NapkinBorder implements NapkinConstants {
    private static final int SMALL_BORDER = 3;
    private static final int LARGE_BORDER = 5;

    static final Insets SMALL_DEFAULT_INSETS =
            new InsetsUIResource(SMALL_BORDER, SMALL_BORDER,
                SMALL_BORDER, SMALL_BORDER);
    static final Insets LARGE_DEFAULT_INSETS =
            new InsetsUIResource(LARGE_BORDER, LARGE_BORDER,
                LARGE_BORDER, LARGE_BORDER);

    public static final NapkinUtil.PropertyFactory BOX_FACTORY =
            new NapkinUtil.PropertyFactory() {
                public Object createPropertyValue() {
                    return new DrawnBoxHolder();
                }
            };

    public NapkinBoxBorder() {
        super(new LineBorder(
                NapkinTheme.Manager.getCurrentTheme().getPenColor()));
    }

    public void doPaintBorder(Component c, Graphics g1, int x, int y,
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
    
    public static final Insets getDefaultInsets(Rectangle bounds) {
        int wi = bounds.width <= 100 ? SMALL_BORDER : LARGE_BORDER;
        int hi = bounds.height <= 100 ? SMALL_BORDER : LARGE_BORDER;
        return new Insets(hi, wi, hi, wi);
    }
    
    public static final int getDelta(int num) {
        return num > SMALL_BORDER ? num >> 1 : num;
    }
    
    public static final int getWidthDelta(Insets in) {
        return getDelta(in.left) + getDelta(in.right);
    }

    public static final int getHeightDelta(Insets in) {
        return getDelta(in.top) + getDelta(in.bottom);
    }

    public Insets doGetBorderInsets(Component c) {
        return getDefaultInsets(c.getBounds());
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        Insets di = doGetBorderInsets(c);
        insets.set(di.top, di.left, di.bottom, di.right);
        return insets;
    }
}
