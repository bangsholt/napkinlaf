// $Id$

package napkin.shapes;

import static napkin.util.NapkinConstants.LENGTH;
import napkin.util.NapkinUtil;
import napkin.util.RandomXY;

import java.awt.*;
import java.awt.geom.*;

public class DrawnQuadLineGenerator extends AbstractDrawnGenerator {
    private final RandomXY ctl;

    public static final DrawnQuadLineGenerator INSTANCE =
            new DrawnQuadLineGenerator();

    public DrawnQuadLineGenerator() {
        super();
        ctl = new RandomXY(60, 3, 0, 0.5);
    }

    @Override
    public Shape generate(AffineTransform matrix) {
        Point2D ctlAt = ctl.generate();
        double lx = NapkinUtil.leftRight(ctlAt.getX(), true);
        double ly = ctlAt.getY();
        double[] coords = {0, 0, lx, ly, LENGTH, 0};
        if (matrix != null)
            matrix.transform(coords, 0, coords, 0, coords.length / 2);

        return new QuadCurve2D.Double(coords[0], coords[1],
                coords[2], coords[3],
                coords[4], coords[5]);
    }

    public RandomXY getCtl() {
        return ctl;
    }
}

