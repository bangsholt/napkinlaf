
package napkin;

import java.awt.*;
import java.awt.geom.*;

public class TabGenerator extends ShapeGenerator {
    private final Value ulX, ulY;
    private final Value urX, urY;
    private final Value lrX, lrY;
    private final Value llX, llY;
    private final Value squeeze;
    private final int side;

    public static final TabGenerator LEFT_TAB = new TabGenerator(LEFT);
    public static final TabGenerator RIGHT_TAB = new TabGenerator(RIGHT);
    public static final TabGenerator TOP_TAB = new TabGenerator(TOP);
    public static final TabGenerator BOTTOM_TAB = new TabGenerator(BOTTOM);

    private static final int[] STARTS;
    private static final TabGenerator[] SIDES;

    static {
        STARTS = new int[5];
        STARTS[0] = -1;     // not used, so we want to force a failure if it is
        STARTS[LEFT] = 4;
        STARTS[RIGHT] = 0;
        STARTS[TOP] = 6;
        STARTS[BOTTOM] = 2;

        SIDES = new TabGenerator[5];
        SIDES[0] = null;     // not used, so we want to force a failure if it is
        SIDES[LEFT] = LEFT_TAB;
        SIDES[RIGHT] = RIGHT_TAB;
        SIDES[TOP] = TOP_TAB;
        SIDES[BOTTOM] = BOTTOM_TAB;
    }

    public static TabGenerator generatorFor(int side) {
        if (side <= 0)
            throw new IndexOutOfBoundsException();
        return SIDES[side];
    }

    private TabGenerator(int side) {
        this.side = side;
        float shimmy = 0.03f;
        ulX = new Value(0, shimmy);
        ulY = new Value(0, shimmy);
        urX = new Value(1, shimmy);
        urY = new Value(0, shimmy);
        lrX = new Value(1, shimmy);
        lrY = new Value(1, shimmy);
        llX = new Value(0, shimmy);
        llY = new Value(1, shimmy);
        if (side == LEFT || side == RIGHT) {
            // when this gets exagerated to stretch out the line it's too much
            final double horizAdj = 10;
            ulX.setRange(shimmy / horizAdj);
            urX.setRange(shimmy / horizAdj);
            llX.setRange(shimmy / horizAdj);
            lrX.setRange(shimmy / horizAdj);
        }
        squeeze = new Value(0.09, 0.001);
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

        double points[] = {xUL, yUL, xUR, yUR, xLR, yLR, xLL, yLL};
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

    public Value getLLX() {
        return llX;
    }

    public Value getLLY() {
        return llY;
    }

    public Value getLRX() {
        return lrX;
    }

    public Value getLRY() {
        return lrY;
    }

    public Value getULX() {
        return ulX;
    }

    public Value getULY() {
        return ulY;
    }

    public Value getURX() {
        return urX;
    }

    public Value getURY() {
        return urY;
    }
}