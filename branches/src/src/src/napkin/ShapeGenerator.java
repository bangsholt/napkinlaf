// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public abstract class ShapeGenerator implements NapkinConstants {
    public abstract Shape generate(AffineTransform matrix);

    public static Shape addLine(GeneralPath path, AffineTransform matrix,
            ShapeGenerator gen) {

        Shape shape;
        if (gen != null)
            shape = gen.generate(matrix);
        else {
            double[] coords = {0, 0, LENGTH, 0};
            matrix.transform(coords, 0, coords, 0, coords.length / 2);
            shape = new Line2D.Double(coords[0], coords[1],
                    coords[2], coords[3]);
        }
        path.append(shape, false);
        return shape;
    }

    public static Class defaultLineGenerator(double mid) {
        return (mid < 10 ? QuadGenerator.class : CubicGenerator.class);
    }
}
