package napkin;

import java.awt.*;
import javax.swing.border.*;

public class NapkinLineBorder extends NapkinBorder {
    private final boolean vertical;
    private DrawnLineHolder line;

    private static final Insets DEFAULT_VERT_INSETS =
            new Insets(0, 0, 0, NapkinBoxBorder.DEFAULT_INSETS.right);
    private static final Insets DEFAULT_HORIZ_INSETS =
            new Insets(0, 0, NapkinBoxBorder.DEFAULT_INSETS.bottom, 0);

    public NapkinLineBorder(Border formalBorder, boolean vertical) {
        super(formalBorder);
        this.vertical = vertical;
    }

    protected Insets doGetBorderInsets(Component c) {
        return (vertical ? DEFAULT_VERT_INSETS : DEFAULT_HORIZ_INSETS);
    }

    public void doPaintBorder(Component c, Graphics g1, int x, int y,
            int width, int height) {

        Graphics2D g = (Graphics2D) g1;
        Rectangle passed = new Rectangle(x, y, width, height);
        if (line == null)
            line = new DrawnLineHolder(DrawnCubicLineGenerator.INSTANCE, vertical);
        line.shapeUpToDate(passed, null);

        Rectangle clip = g.getClipBounds();
        Insets insets = doGetBorderInsets(c);
        g.setClip(clip.x - insets.left, clip.y - insets.top,
                clip.width + insets.left + insets.right,
                clip.height + insets.top + insets.bottom);
        if (insets.bottom != 0)
            y += c.getHeight() - insets.bottom;
        else
            x += c.getWidth() - insets.right;
        g.translate(x, y);
        line.draw(g);
        g.translate(-x, -y);
    }
}