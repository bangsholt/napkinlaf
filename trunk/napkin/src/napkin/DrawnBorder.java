// $Id$

package napkin;

import napkin.ShapeHolder.Factory;

import java.awt.*;
import java.util.Map;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class DrawnBorder extends AbstractBorder implements UIResource {

    private static final int BORDER = 6;

    static final Insets DEFAULT_INSETS =
            new Insets(BORDER, BORDER, BORDER, BORDER);

    //!! We should revisit this decision.  -arnold
    /**
     * We use our own hash map instead of using get/putClientProperty because
     * those methods are only defined for JComponent, not component, and we're
     * sort of suspicious that we ought to do this for non-Swing components.
     */
    private static final Map borders = new ShapeHolderMap(new Factory() {
        public ShapeHolder create() {
            return new BoxHolder();
        }
    });

    public DrawnBorder() {
    }

    public void paintBorder(Component c, Graphics g1, int x, int y,
            int width, int height) {

        Graphics2D g = (Graphics2D) g1;
        BoxHolder box = (BoxHolder) borders.get(c);
        Rectangle passed = new Rectangle(x, y, width, height);
        box.shapeUpToDate(c, passed);

        Rectangle rect = c.getBounds();
        rect.x = rect.y = 0;
        box.draw(g);

    }

    public Insets getBorderInsets(Component c) {
        return DEFAULT_INSETS;
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = insets.left = insets.bottom = insets.right = BORDER;
        return insets;
    }
}

