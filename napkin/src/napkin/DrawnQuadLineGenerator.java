// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnQuadLineGenerator extends DrawnShapeGenerator {

    private final RandomValue ctlX;
    private final RandomValue ctlY;
    public static final DrawnQuadLineGenerator INSTANCE = new DrawnQuadLineGenerator();

    public DrawnQuadLineGenerator() {
        ctlX = new RandomValue(60, 3);
        ctlY = new RandomValue(0, 0.5);
    }

    public RandomValue getCtlX() {
        return ctlX;
    }

    public RandomValue getCtlY() {
        return ctlY;
    }

    public Shape generate(AffineTransform matrix) {
        double lx = NapkinUtil.leftRight(ctlX.generate(), true);
        double ly = ctlY.generate();
        double[] coords = {0, 0, lx, ly, LENGTH, 0};
        if (matrix != null)
            matrix.transform(coords, 0, coords, 0, coords.length / 2);

        return new QuadCurve2D.Double(coords[0], coords[1],
                coords[2], coords[3],
                coords[4], coords[5]);
    }
}

