
package napkin;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

class BoxHolder extends ShapeHolder implements NapkinConstants {
    private Rectangle size;
    private Insets insets;
    private int breakSide;
    private final Point2D begBreak, endBreak;

    BoxHolder(BoxGenerator gen) {
        super(gen);
        breakSide = NO_SIDE;
        begBreak = new Point2D.Double();
        endBreak = new Point2D.Double();
    }

    BoxHolder() {
        this(new BoxGenerator());
    }

    void shapeUpToDate(Component c, Rectangle sz) {
        shapeUpToDate(c, sz, -1, 0, 0, 0, 0);
    }

    void shapeUpToDate(Component c, Rectangle sz, int bSide, double begX,
            double begY, double endX, double endY) {

        Insets in = (c instanceof JComponent ?
                ((JComponent) c).getInsets() : DrawnBorder.DEFAULT_INSETS);

        if (size != null && size.width == sz.width && size.height == sz.height
                && insets.equals(in) && bSide == breakSide &&
                begBreak.getX() == begX && begBreak.getY() == begY &&
                endBreak.getX() == endX && endBreak.getY() == endY) {

            return;
        }

        size = (Rectangle) sz.clone();
        insets = (Insets) in.clone();
        breakSide = bSide;
        begBreak.setLocation(begX, begY);
        endBreak.setLocation(endX, endY);

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

        if (bSide == NO_SIDE)
            gen.setNoBreak();
        else
            gen.setBreak(bSide, begX, begY, endX, endY);

        shape = gen.generate(matrix);
    }
}