// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public abstract class NapkinIcon implements Icon {
    Shape place;
    Shape mark;

    private final int markColor;
    private final AffineTransform scaleMat;
    private int width;
    private int height;
    private DrawnShapeGenerator placeGen;
    DrawnShapeGenerator markGen;

    public NapkinIcon(int markColor, AffineTransform scaleMat) {
        this.markColor = markColor;
        this.scaleMat = scaleMat;
    }

    void init() {
        markGen = createMarkGenerator();
        placeGen = createPlaceGenerator();
        width = calcWidth();
        height = calcHeight();
    }

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g1, int x, int y) {
        if (place == null)
            place = placeGen.generate(scaleMat);
        boolean selected = false;
        if (c instanceof AbstractButton)
            selected = ((AbstractButton) c).isSelected();

        Graphics2D placeG = NapkinUtil.copy(g1);

        Graphics2D markG = null;
        if (!selected) {
            mark = null;
        } else {
            if (markGen != null && mark == null)
                mark = markGen.generate(scaleMat);
            markG = NapkinUtil.lineGraphics(g1, 2.5f);
            markG.setColor(NapkinUtil.themeFor(c).getColor(markColor));
        }
        placeG.setColor(c.getForeground());
        doPaint(placeG, markG, x, y);
    }

    abstract void doPaint(Graphics2D placeG, Graphics2D markG, int x, int y);

    abstract int calcWidth();

    abstract int calcHeight();

    abstract DrawnShapeGenerator createPlaceGenerator();

    abstract DrawnShapeGenerator createMarkGenerator();
}
