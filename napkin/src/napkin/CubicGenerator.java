// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class CubicGenerator extends ShapeGenerator {

    private final Value leftX;
    private final Value leftY;
    private final Value rightX;
    private final Value rightY;

    private static final boolean DEBUG = false;

    public static final CubicGenerator INSTANCE = new CubicGenerator();

    public CubicGenerator() {
        leftX = new Value(10, 4);
        leftY = new Value(-0.7, 1.5);
        rightX = new Value(20, 8);
        rightY = new Value(-1.3, 2);
    }

    public Value getLeftX() {
        return leftX;
    }

    public Value getLeftY() {
        return leftY;
    }

    public Value getRightX() {
        return rightX;
    }

    public Value getRightY() {
        return rightY;
    }

    public Shape generate(AffineTransform matrix) {
        double lx = NapkinUtil.leftRight(leftX.generate(), true);
        double ly = leftY.generate();
        double rx = NapkinUtil.leftRight(rightX.generate(), false);
        double ry = rightY.generate();
        double[] coords = {0, 0, lx, ly, rx, ry, LENGTH, 0};
        if (matrix != null)
            matrix.transform(coords, 0, coords, 0, 4);

        CubicCurve2D.Double line = new CubicCurve2D.Double(coords[0], coords[1],
                coords[2], coords[3],
                coords[4], coords[5],
                coords[6], coords[7]);
        if (!DEBUG)
            return line;
        else {
            GeneralPath shape = new GeneralPath(new Rectangle2D.Double(coords[0] - 2, coords[1] - 2, 4, 4));
            shape.append(line, false);
            shape.append(new Line2D.Double(coords[6] - 2, coords[7] - 2, coords[6] + 2, coords[7] + 2), false);
            shape.append(new Line2D.Double(coords[6] + 2, coords[7] - 2, coords[6] - 2, coords[7] + 2), false);
            return shape;
        }
    }
}
