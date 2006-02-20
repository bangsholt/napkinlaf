// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class NapkinBoxBorder extends NapkinBorder implements NapkinConstants {
    private static final int BORDER = 5;

    static final Insets DEFAULT_INSETS =
            new InsetsUIResource(BORDER, BORDER, BORDER, BORDER);

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

    public Insets doGetBorderInsets(Component c) {
        return DEFAULT_INSETS;
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = insets.left = insets.bottom = insets.right = BORDER;
        return insets;
    }
}
