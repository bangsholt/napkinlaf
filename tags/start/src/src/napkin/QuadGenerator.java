// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class QuadGenerator extends ShapeGenerator {

    private final Value ctlX;
    private final Value ctlY;
    public static final QuadGenerator INSTANCE = new QuadGenerator();

    public QuadGenerator() {
        ctlX = new Value(60, 3);
        ctlY = new Value(0, 0.5);
    }

    public Value getCtlX() {
        return ctlX;
    }

    public Value getCtlY() {
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

