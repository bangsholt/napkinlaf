// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class NapkinBoxBorder extends NapkinBorder implements NapkinConstants {
    private static final int BORDER = 3;

    static final Insets DEFAULT_INSETS =
            new InsetsUIResource(BORDER, BORDER, BORDER, BORDER);

    public NapkinBoxBorder() {
        super(
                new LineBorder(
                        NapkinTheme.Manager.getCurrentTheme().getPenColor()));
    }

    private static final NapkinUtil.PropertyFactory BOX_FACTORY =
            new NapkinUtil.PropertyFactory() {
                public Object createPropertyValue() {
                    return new DrawnBoxHolder();
                }
            };

    public void doPaintBorder(Component c, Graphics g1, int x, int y,
            int width, int height) {

        DrawnBoxHolder box = (DrawnBoxHolder)
                NapkinUtil.getProperty((JComponent) c, BORDER_KEY, BOX_FACTORY);

        Rectangle passed = new Rectangle(x, y, width, height);
        box.shapeUpToDate(passed);

        Rectangle clip = g1.getClipBounds();
        g1.setClip(clip.x - BORDER, clip.y - BORDER, clip.width + 2 * BORDER,
                clip.height + 2 * BORDER);
        Graphics2D g = NapkinUtil.defaultGraphics(g1, c);
        g.translate(x, y);
        box.draw(g);
        g.translate(-x, -y);
        NapkinUtil.finishGraphics(g, c);
    }

    public Insets doGetBorderInsets(Component c) {
        return DEFAULT_INSETS;
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = insets.left = insets.bottom = insets.right = BORDER;
        return insets;
    }
}
