// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class CircleGenerator extends ShapeGenerator implements NapkinConstants {
    private Value startX, startY;
    private Value endX, endY;
    private Value tlX, tlY;
    private Value trX, trY;
    private Value brX, brY;
    private Value blX, blY;
    private boolean forFill;

    public static final CubicGenerator INSTANCE = new CubicGenerator();

    public CircleGenerator() {
        this(false);
    }

    public CircleGenerator(boolean forFill) {
        this.forFill = forFill;

        startX = new Value(LENGTH / 2, 2);
        startY = new Value(0, 20);
        endX = new Value(LENGTH / 2, 2);
        endY = new Value(0, 20);
        tlX = new Value(0);
        tlY = new Value(0);
        trX = new Value(LENGTH);
        trY = new Value(0);
        brX = new Value(LENGTH);
        brY = new Value(LENGTH);
        blX = new Value(0);
        blY = new Value(LENGTH);
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

    public boolean isForFill() {
        return forFill;
    }

    public void setForFill(boolean forFill) {
        this.forFill = forFill;
    }

    public Value getBlX() {
        return blX;
    }

    public Value getBlY() {
        return blY;
    }

    public Value getBrX() {
        return brX;
    }

    public Value getBrY() {
        return brY;
    }

    public Value getStartX() {
        return startX;
    }

    public Value getStartY() {
        return startY;
    }

    public Value getEndX() {
        return endX;
    }

    public Value getEndY() {
        return endY;
    }

    public Value getTlX() {
        return tlX;
    }

    public Value getTlY() {
        return tlY;
    }

    public Value getTrX() {
        return trX;
    }

    public Value getTrY() {
        return trY;
    }
}
