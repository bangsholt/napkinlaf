// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

class BoxHolder extends ShapeHolder {
    private Rectangle size;
    private Insets insets;

    BoxHolder(BoxGenerator gen) {
        super(gen);
    }

    BoxHolder() {
        this(new BoxGenerator());
    }

    void shapeUpToDate(Component c, Rectangle sz) {
        Insets in = (c instanceof JComponent ?
                ((JComponent) c).getInsets() : DrawnBorder.DEFAULT_INSETS);

        if (size != null && size.width == sz.width && size.height == sz.height
                && insets.equals(in)) {
            return;
        }

        size = (Rectangle) sz.clone();
        insets = (Insets) in.clone();

        int cornerX = in.top / 2 + 1;
        int cornerY = in.left / 2 + 1;

        double innerWidth = sz.getWidth() - (in.left + in.right);
        double innerHeight = sz.getHeight() - (in.top + in.bottom);
        double borderWidth = innerWidth + in.right - 1;
        double borderHeight = innerHeight + in.bottom - 1;

        BoxGenerator gen = (BoxGenerator) this.gen;
        gen.getSizeX().setMid(borderWidth);
        gen.getSizeY().setMid(borderHeight);
        gen.getBegX().setMid(cornerY);
        gen.getEndY().setMid(cornerX);

        AffineTransform matrix = new AffineTransform();
        matrix.translate(cornerY, cornerX);

        shape = gen.generate(matrix);
    }
}
