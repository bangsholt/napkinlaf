package net.sourceforge.napkinlaf.shapes;

import static net.sourceforge.napkinlaf.util.NapkinConstants.LENGTH;
import net.sourceforge.napkinlaf.util.NapkinUtil;
import net.sourceforge.napkinlaf.util.RandomXY;

import java.awt.*;
import java.awt.geom.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawnCubicLineGenerator extends AbstractDrawnGenerator {

    private final RandomXY left;
    private final RandomXY right;

    public static final DrawnCubicLineGenerator INSTANCE =
            new DrawnCubicLineGenerator();

    private static final Logger logger = Logger.getLogger(
            DrawnCubicLineGenerator.class.getName());

    public DrawnCubicLineGenerator() {
        left = new RandomXY(10, 4, -0.7, 1.5);
        right = new RandomXY(20, 8, -1.3, 2);
    }

    @Override
    public Shape generate(AffineTransform matrix) {
        Point2D leftAt = left.generate();
        Point2D rightAt = right.generate();
        double lx = NapkinUtil.leftRight(leftAt.getX(), true);
        double ly = leftAt.getY();
        double rx = NapkinUtil.leftRight(rightAt.getX(), false);
        double ry = rightAt.getY();
        double[] coords = {0, 0, lx, ly, rx, ry, LENGTH, 0};
        if (matrix != null) {
            matrix.transform(coords, 0, coords, 0, 4);
        }

        CubicCurve2D.Double line = new CubicCurve2D.Double(coords[0], coords[1],
                coords[2], coords[3],
                coords[4], coords[5],
                coords[6], coords[7]);
        
        Shape result;
        if (logger.isLoggable(Level.FINE)) {
            GeneralPath shape = new GeneralPath(
                    new Rectangle2D.Double(coords[0] - 2, coords[1] - 2, 4, 4));
            shape.append(line, false);
            shape.append(new Line2D.Double(coords[6] - 2, coords[7] - 2,
                    coords[6] + 2, coords[7] + 2),
                    false);
            shape.append(new Line2D.Double(coords[6] + 2, coords[7] - 2,
                    coords[6] - 2, coords[7] + 2),
                    false);
            result = shape;
        } else {
            result = line;
        }
        return result;
    }

    public RandomXY getLeft() {
        return left;
    }

    public RandomXY getRight() {
        return right;
    }
}
