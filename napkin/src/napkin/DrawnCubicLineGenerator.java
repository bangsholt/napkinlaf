// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawnCubicLineGenerator extends DrawnShapeGenerator {

    private final RandomValue leftX;
    private final RandomValue leftY;
    private final RandomValue rightX;
    private final RandomValue rightY;

    public static final DrawnCubicLineGenerator INSTANCE = new DrawnCubicLineGenerator();

    private static final Logger logger = Logger.getLogger(
            DrawnCubicLineGenerator.class.getName());

    public DrawnCubicLineGenerator() {
        leftX = new RandomValue(10, 4);
        leftY = new RandomValue(-0.7, 1.5);
        rightX = new RandomValue(20, 8);
        rightY = new RandomValue(-1.3, 2);
    }

    public RandomValue getLeftX() {
        return leftX;
    }

    public RandomValue getLeftY() {
        return leftY;
    }

    public RandomValue getRightX() {
        return rightX;
    }

    public RandomValue getRightY() {
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
        if (!logger.isLoggable(Level.FINE))
            return line;
        else {
            GeneralPath shape = new GeneralPath(
                    new Rectangle2D.Double(coords[0] - 2, coords[1] - 2, 4, 4));
            shape.append(line, false);
            shape.append(
                    new Line2D.Double(coords[6] - 2, coords[7] - 2,
                            coords[6] + 2, coords[7] + 2),
                    false);
            shape.append(
                    new Line2D.Double(coords[6] + 2, coords[7] - 2,
                            coords[6] - 2, coords[7] + 2),
                    false);
            return shape;
        }
    }
}
