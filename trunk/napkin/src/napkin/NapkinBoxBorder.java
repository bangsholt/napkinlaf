package napkin;

import java.awt.*;
import java.util.Map;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class NapkinBoxBorder extends NapkinBorder {
    private static final int BORDER = 3;

    static final Insets DEFAULT_INSETS =
            new InsetsUIResource(BORDER, BORDER, BORDER, BORDER);

    public NapkinBoxBorder(Color color) {
        super(new LineBorder(color));
    }

    //!! We should revisit this decision.  -arnold
    /**
     * We use our own hash map instead of using get/putClientProperty because
     * those methods are only defined for JComponent, not component, and we're
     * sort of suspicious that we ought to do this for non-Swing components.
     */
    private static final Map borders = new DrawnShapeHolderMap(new DrawnShapeHolder.Factory() {
        public DrawnShapeHolder create() {
            return new DrawnBoxHolder();
        }
    });

    public void doPaintBorder(Component c, Graphics g1, int x, int y,
            int width, int height) {

        DrawnBoxHolder box = (DrawnBoxHolder) borders.get(c);
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