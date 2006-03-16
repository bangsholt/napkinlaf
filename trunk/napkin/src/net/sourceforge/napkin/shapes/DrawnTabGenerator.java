// $Id$

package napkin.shapes;

import static napkin.util.NapkinConstants.*;
import napkin.util.RandomValue;
import napkin.util.RandomXY;

import java.awt.*;
import java.awt.geom.*;

public class DrawnTabGenerator extends AbstractDrawnGenerator {
    private final RandomXY ul;
    private final RandomXY ur;
    private final RandomXY lr;
    private final RandomXY ll;
    private final RandomValue squeeze;
    private final int side;

    public static final DrawnTabGenerator LEFT_TAB =
            new DrawnTabGenerator(LEFT);
    public static final DrawnTabGenerator RIGHT_TAB =
            new DrawnTabGenerator(RIGHT);
    public static final DrawnTabGenerator TOP_TAB =
            new DrawnTabGenerator(TOP);
    public static final DrawnTabGenerator BOTTOM_TAB =
            new DrawnTabGenerator(BOTTOM);

    private static final int[] STARTS;
    private static final DrawnTabGenerator[] SIDES;

    static {
        STARTS = new int[5];
        STARTS[0] = -1;     // not used, so we want to force a failure if it is
        STARTS[LEFT] = 4;
        STARTS[RIGHT] = 0;
        STARTS[TOP] = 6;
        STARTS[BOTTOM] = 2;

        SIDES = new DrawnTabGenerator[5];
        SIDES[0] = null;     // not used, so we want to force a failure if it is
        SIDES[LEFT] = LEFT_TAB;
        SIDES[RIGHT] = RIGHT_TAB;
        SIDES[TOP] = TOP_TAB;
        SIDES[BOTTOM] = BOTTOM_TAB;
    }

    public static DrawnTabGenerator generatorFor(int side) {
        if (side <= 0)
            throw new IndexOutOfBoundsException();
        return SIDES[side];
    }

    private DrawnTabGenerator(int side) {
        super();
        this.side = side;
        float shimmy = 0.03f;
        ul = new RandomXY(0, shimmy, 0, shimmy);
        ur = new RandomXY(1, shimmy, 0, shimmy);
        lr = new RandomXY(1, shimmy, 1, shimmy);
        ll = new RandomXY(0, shimmy, 1, shimmy);
        squeeze = new RandomValue(0.09, 0.001);
    }

    @Override
    public Shape generate(AffineTransform matrix) {
        if (getXScale(matrix) > 100.0) {
            setScales(0.008, 0.03);
        } else {
            setScales(0.03, 0.1);
        }

        GeneralPath tab = new GeneralPath();

        Point2D ulAt = ul.generate();
        Point2D urAt = ur.generate();
        Point2D llAt = ll.generate();
        Point2D lrAt = lr.generate();
        double xUL = ulAt.getX();
        double yUL = ulAt.getY();
        double xUR = urAt.getX();
        double yUR = urAt.getY();
        double xLR = lrAt.getX();
        double yLR = lrAt.getY();
        double xLL = llAt.getX();
        double yLL = llAt.getY();

        switch (side) {
        case LEFT:
            yUL += squeeze.generate();
            yLL -= squeeze.generate();
            break;
        case RIGHT:
            yUR += squeeze.generate();
            yLR -= squeeze.generate();
            break;
        case TOP:
            xUL += squeeze.generate();
            xUR -= squeeze.generate();
            break;
        case BOTTOM:
            xLL += squeeze.generate();
            xLR -= squeeze.generate();
            break;
        default:
            throw new IllegalStateException("unknown side: " + side);
        }

        double[] points = {xUL, yUL, xUR, yUR, xLR, yLR, xLL, yLL};
        matrix.transform(points, 0, points, 0, 4);

        int start = STARTS[side];
        double prevX = points[start++];
        double prevY = points[start++];
        for (int i = 0; i < 3; i++) {
            start %= points.length;
            double x = points[start++];
            double y = points[start++];
            tab.append(fromPts(prevX, prevY, x, y), false);
            prevX = x;
            prevY = y;
        }

        return tab;
    }

    private static Shape fromPts(double x0, double y0, double x1, double y1) {
        AffineTransform matrix = new AffineTransform();
        double dx = x1 - x0;
        double dy = y1 - y0;
        double len = Math.sqrt(dx * dx + dy * dy);
        matrix.translate(x0, y0);
        matrix.rotate(Math.atan2(dy, dx));
        matrix.scale(len / LENGTH, 1.0);
        return defaultLineGenerator(len).generate(matrix);
    }

    public RandomXY getUL() {
        return ul;
    }

    public RandomXY getUR() {
        return ur;
    }

    public RandomXY getLL() {
        return ll;
    }

    public RandomXY getLR() {
        return lr;
    }

    private static double getXScale(AffineTransform matrix) {
        Point2D[] points = {
                new Point2D.Double(0, 0), new Point2D.Double(1, 0)};
        matrix.transform(points, 0, points, 0, 2);
        return points[0].distance(points[1]);
    }

    private void setScales(double shimmy, double meanSqueeze) {
        ul.getY().setRange(shimmy);
        ur.getY().setRange(shimmy);
        lr.getY().setRange(shimmy);
        ll.getY().setRange(shimmy);
        // when this gets exagerated to stretch out the line it's too much
        if (side == LEFT || side == RIGHT)
            shimmy /= 10;
        ul.getX().setRange(shimmy);
        ur.getX().setRange(shimmy);
        ll.getX().setRange(shimmy);
        lr.getX().setRange(shimmy);
        squeeze.setMid(meanSqueeze);
        squeeze.setRange(meanSqueeze / 100);
    }
}
