// $Id$

package napkin.shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import napkin.util.NapkinConstants;

public abstract class DrawnShapeGenerator implements NapkinConstants {
    public abstract Shape generate(AffineTransform matrix);

    public static Shape addLine(GeneralPath path, AffineTransform matrix,
            DrawnShapeGenerator gen) {

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

    public static Class defaultLineType(double len) {
        return (len < 10 ?
                DrawnQuadLineGenerator.class : DrawnCubicLineGenerator.class);
    }
}
