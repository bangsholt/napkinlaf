// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnCircleGenerator extends DrawnShapeGenerator
        implements NapkinConstants {
    private final RandomValue startX;
    private final RandomValue startY;
    private final RandomValue endX;
    private final RandomValue endY;
    private final RandomValue tlX;
    private final RandomValue tlY;
    private final RandomValue trX;
    private final RandomValue trY;
    private final RandomValue brX;
    private final RandomValue brY;
    private final RandomValue blX;
    private final RandomValue blY;
    private boolean forFill;

    public static final DrawnCubicLineGenerator INSTANCE = new DrawnCubicLineGenerator();

    public DrawnCircleGenerator() {
        this(false);
    }

    public DrawnCircleGenerator(boolean forFill) {
        this.forFill = forFill;

        startX = new RandomValue(LENGTH / 2.0, 2);
        startY = new RandomValue(0, 20);
        endX = new RandomValue(LENGTH / 2.0, 2);
        endY = new RandomValue(0, 20);
        tlX = new RandomValue(0);
        tlY = new RandomValue(0);
        trX = new RandomValue(LENGTH);
        trY = new RandomValue(0);
        brX = new RandomValue(LENGTH);
        brY = new RandomValue(LENGTH);
        blX = new RandomValue(0);
        blY = new RandomValue(LENGTH);
    }

    public Shape generate(AffineTransform matrix) {
        GeneralPath circle = new GeneralPath();

        double xStart = startX.generate();
        double yStart = startY.generate();
        double xEnd = (forFill ? xStart : endX.generate());
        double yEnd = (forFill ? yStart : endY.generate());
        double xTL = tlX.generate();
        double yTL = tlY.generate();
        double xTR = trX.generate();
        double yTR = trY.generate();
        double xBR = brX.generate();
        double yBR = brY.generate();
        double xBL = blX.generate();
        double yBL = blY.generate();

        double bottomX = xBL + (xBR - xBL) / 2;
        double bottomY = yBL + (yBR - yBL) / 2;

        double[] coords = {
            xStart, yStart, xTR, yTR, xBR, yBR, bottomX, bottomY,
            bottomX, bottomY, xBL, yBL, xTL, yTL, xEnd, yEnd,
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

    public RandomValue getBlX() {
        return blX;
    }

    public RandomValue getBlY() {
        return blY;
    }

    public RandomValue getBrX() {
        return brX;
    }

    public RandomValue getBrY() {
        return brY;
    }

    public RandomValue getStartX() {
        return startX;
    }

    public RandomValue getStartY() {
        return startY;
    }

    public RandomValue getEndX() {
        return endX;
    }

    public RandomValue getEndY() {
        return endY;
    }

    public RandomValue getTlX() {
        return tlX;
    }

    public RandomValue getTlY() {
        return tlY;
    }

    public RandomValue getTrX() {
        return trX;
    }

    public RandomValue getTrY() {
        return trY;
    }
}
