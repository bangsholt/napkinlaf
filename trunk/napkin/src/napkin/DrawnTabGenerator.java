// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnTabGenerator extends DrawnShapeGenerator {
    private final RandomXY ul;
    private final RandomXY ur;
    private final RandomXY lr;
    private final RandomXY ll;
    private final RandomValue squeeze;
    private final int side;

    public static final DrawnTabGenerator LEFT_TAB = new DrawnTabGenerator(
            LEFT);
    public static final DrawnTabGenerator RIGHT_TAB = new DrawnTabGenerator(
            RIGHT);
    public static final DrawnTabGenerator TOP_TAB = new DrawnTabGenerator(TOP);
    public static final DrawnTabGenerator BOTTOM_TAB = new DrawnTabGenerator(
            BOTTOM);

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
        this.side = side;
        float shimmy = 0.03f;
        ul = new RandomXY(0, shimmy, 0, shimmy);
        ur = new RandomXY(1, shimmy, 0, shimmy);
        lr = new RandomXY(1, shimmy, 1, shimmy);
        ll = new RandomXY(0, shimmy, 1, shimmy);
        if (side == LEFT || side == RIGHT) {
            // when this gets exagerated to stretch out the line it's too much
            double simmyAdj = shimmy / 10;
            ul.getX().setRange(simmyAdj);
            ur.getX().setRange(simmyAdj);
            ll.getX().setRange(simmyAdj);
            lr.getX().setRange(simmyAdj);
        }
        squeeze = new RandomValue(0.09, 0.001);
    }

    public Shape generate(AffineTransform matrix) {
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
        NapkinUtil.transform(matrix, points);

        int start = STARTS[side];
        for (int i = 0; i < 4; i++) {
            if (start >= points.length)
                start = 0;
            float x = (float) points[start++];
            float y = (float) points[start++];
            if (i == 0)
                tab.moveTo(x, y);
            else
                tab.lineTo(x, y);
        }

        return tab;
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
}
