// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnBoxHolder extends DrawnShapeHolder
        implements NapkinConstants {
    private Rectangle size;
    private int breakSide;
    private final Point2D begBreak, endBreak;

    public DrawnBoxHolder(DrawnBoxGenerator gen) {
        super(gen);
        breakSide = NO_SIDE;
        begBreak = new Point2D.Double();
        endBreak = new Point2D.Double();
    }

    public DrawnBoxHolder() {
        this(new DrawnBoxGenerator());
    }

    void shapeUpToDate(Rectangle sz) {
        shapeUpToDate(sz, -1, 0, 0, 0, 0);
    }

    void shapeUpToDate(Rectangle sz, int bSide, double begX, double begY,
            double endX, double endY) {

        if (size != null && size.width == sz.width && size.height == sz.height
                && bSide == breakSide &&
                begBreak.getX() == begX && begBreak.getY() == begY &&
                endBreak.getX() == endX && endBreak.getY() == endY) {

            return;
        }

        size = (Rectangle) sz.clone();
        breakSide = bSide;
        begBreak.setLocation(begX, begY);
        endBreak.setLocation(endX, endY);

        Insets in = NapkinBoxBorder.DEFAULT_INSETS;

        double borderWidth = sz.getWidth() - (in.left + in.right);
        double borderHeight = sz.getHeight() - (in.top + in.bottom);

        int cornerX = in.top;
        int cornerY = in.left;

        DrawnBoxGenerator dbg = (DrawnBoxGenerator) gen;
        dbg.getSize().setMid(borderWidth, borderHeight);
        dbg.getCorner().setMid(cornerX, cornerY);

        AffineTransform matrix = new AffineTransform();
        matrix.translate(cornerX, cornerY);

        if (bSide == NO_SIDE)
            dbg.setNoBreak();
        else
            dbg.setBreak(bSide, begX, begY, endX, endY);

        shape = dbg.generate(matrix);
    }
}
