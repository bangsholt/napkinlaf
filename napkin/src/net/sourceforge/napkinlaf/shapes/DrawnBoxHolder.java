// $Id: DrawnBoxHolder.java 355 2006-03-15 09:15:55Z kcrca $

package net.sourceforge.napkinlaf.shapes;

import net.sourceforge.napkinlaf.borders.NapkinBoxBorder;
import static net.sourceforge.napkinlaf.util.NapkinConstants.NO_SIDE;

import java.awt.*;
import java.awt.geom.*;

public class DrawnBoxHolder extends DrawnShapeHolder {
    private Rectangle size;
    private int breakSide;
    private final Point2D begBreak,
            endBreak;

    public DrawnBoxHolder(DrawnBoxGenerator gen) {
        super(gen);
        breakSide = NO_SIDE;
        begBreak = new Point2D.Double();
        endBreak = new Point2D.Double();
    }

    public DrawnBoxHolder() {
        this(new DrawnBoxGenerator());
    }

    public void shapeUpToDate(Rectangle sz) {
        shapeUpToDate(sz, -1, 0, 0, 0, 0);
    }

    public void shapeUpToDate(Rectangle sz, int bSide, double begX, double begY,
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

        Insets in = NapkinBoxBorder.getDefaultInsets(sz);

        double borderWidth = sz.getWidth() - NapkinBoxBorder.getWidthDelta(in);
        double borderHeight = sz.getHeight() - NapkinBoxBorder.getHeightDelta(
                in);

        int cornerX = NapkinBoxBorder.getDelta(in.left);
        int cornerY = NapkinBoxBorder.getDelta(in.top);

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
