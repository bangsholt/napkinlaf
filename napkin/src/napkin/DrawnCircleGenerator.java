// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnCircleGenerator extends DrawnShapeGenerator
        implements NapkinConstants {
    private final RandomXY start;
    private final RandomXY end;
    private final RandomXY tl;
    private final RandomXY tr;
    private final RandomXY br;
    private final RandomXY bl;
    private boolean forFill;

    public static final DrawnCubicLineGenerator INSTANCE = new DrawnCubicLineGenerator();

    public DrawnCircleGenerator() {
        this(false);
    }

    public DrawnCircleGenerator(boolean forFill) {
        this.forFill = forFill;

        start = new RandomXY(LENGTH / 2.0, 2, 0, 20);
        end = new RandomXY(LENGTH / 2.0, 2, 0, 20);
        tl = new RandomXY(0, 0);
        tr = new RandomXY(LENGTH, 0);
        br = new RandomXY(LENGTH, LENGTH);
        bl = new RandomXY(0, LENGTH);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath circle = new GeneralPath();

        Point2D startAt = start.generate();
        Point2D endAt = (forFill ? startAt : end.generate());
        Point2D tlAt = tl.generate();
        Point2D trAt = tr.generate();
        Point2D brAt = br.generate();
        Point2D blAt = bl.generate();

        double xBL = blAt.getX();
        double yBL = blAt.getY();
        double xBR = brAt.getX();
        double yBR = brAt.getY();

        double bottomX = xBL + (xBR - xBL) / 2;
        double bottomY = yBL + (yBR - yBL) / 2;

        double[] coords = {
            startAt.getX(), startAt.getY(), trAt.getX(), trAt.getY(),
            xBR, yBR, bottomX, bottomY,

            bottomX, bottomY, xBL, yBL, tlAt.getX(),
            tlAt.getY(), endAt.getX(), endAt.getY(),
        };
        if (matrix != null)
            matrix.transform(coords, 0, coords, 0, coords.length / 2);

        CubicCurve2D left = new CubicCurve2D.Double(coords[0], coords[1],
                coords[2], coords[3],
                coords[4], coords[5],
                coords[6], coords[7]);
        CubicCurve2D right = new CubicCurve2D.Double(coords[8], coords[9],
                coords[10], coords[11],
                coords[12], coords[13],
                coords[14], coords[15]);

        circle.append(left, false);
        circle.append(right, false);
        return circle;
    }

    public RandomXY getTL() {
        return tl;
    }

    public RandomXY getBR() {
        return br;
    }

    public RandomXY getBL() {
        return bl;
    }

    public RandomXY getTR() {
        return tr;
    }
}
