// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnTabGenerator extends DrawnShapeGenerator {
    private final RandomValue ulX, ulY;
    private final RandomValue urX, urY;
    private final RandomValue lrX, lrY;
    private final RandomValue llX, llY;
    private final RandomValue squeeze;
    private final int side;

    public static final DrawnTabGenerator LEFT_TAB = new DrawnTabGenerator(LEFT);
    public static final DrawnTabGenerator RIGHT_TAB = new DrawnTabGenerator(RIGHT);
    public static final DrawnTabGenerator TOP_TAB = new DrawnTabGenerator(TOP);
    public static final DrawnTabGenerator BOTTOM_TAB = new DrawnTabGenerator(BOTTOM);

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
        ulX = new RandomValue(0, shimmy);
        ulY = new RandomValue(0, shimmy);
        urX = new RandomValue(1, shimmy);
        urY = new RandomValue(0, shimmy);
        lrX = new RandomValue(1, shimmy);
        lrY = new RandomValue(1, shimmy);
        llX = new RandomValue(0, shimmy);
        llY = new RandomValue(1, shimmy);
        if (side == LEFT || side == RIGHT) {
            // when this gets exagerated to stretch out the line it's too much
            final double horizAdj = 10;
            ulX.setRange(shimmy / horizAdj);
            urX.setRange(shimmy / horizAdj);
            llX.setRange(shimmy / horizAdj);
            lrX.setRange(shimmy / horizAdj);
        }
        squeeze = new RandomValue(0.09, 0.001);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath tab = new GeneralPath();

        double xUL = ulX.generate();
        double yUL = ulY.generate();
        double xUR = urX.generate();
        double yUR = urY.generate();
        double xLR = lrX.generate();
        double yLR = lrY.generate();
        double xLL = llX.generate();
        double yLL = llY.generate();

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

    public RandomValue getLLX() {
        return llX;
    }

    public RandomValue getLLY() {
        return llY;
    }

    public RandomValue getLRX() {
        return lrX;
    }

    public RandomValue getLRY() {
        return lrY;
    }

    public RandomValue getULX() {
        return ulX;
    }

    public RandomValue getULY() {
        return ulY;
    }

    public RandomValue getURX() {
        return urX;
    }

    public RandomValue getURY() {
        return urY;
    }
}
