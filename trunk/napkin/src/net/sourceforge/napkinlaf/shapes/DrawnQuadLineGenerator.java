// $Id$

package net.sourceforge.napkinlaf.shapes;

import static net.sourceforge.napkinlaf.util.NapkinConstants.LENGTH;
import net.sourceforge.napkinlaf.util.NapkinUtil;
import net.sourceforge.napkinlaf.util.RandomXY;

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

