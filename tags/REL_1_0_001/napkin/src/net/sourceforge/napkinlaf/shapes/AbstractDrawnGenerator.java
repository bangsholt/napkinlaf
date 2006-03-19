// $Id$

package net.sourceforge.napkinlaf.shapes;

import static net.sourceforge.napkinlaf.util.NapkinConstants.LENGTH;

import java.awt.*;
import java.awt.geom.*;

public abstract class AbstractDrawnGenerator {
    public abstract Shape generate(AffineTransform matrix);

    public static Shape addLine(GeneralPath path, AffineTransform matrix,
            AbstractDrawnGenerator gen) {

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

    public static Class<? extends AbstractDrawnGenerator>
            defaultLineType(double len) {
        return (len < 10 ?
                DrawnQuadLineGenerator.class : DrawnCubicLineGenerator.class);
    }

    public static AbstractDrawnGenerator defaultLineGenerator(double len) {
        return defaultLineType(len) == DrawnQuadLineGenerator.class ?
                new DrawnQuadLineGenerator() : new DrawnCubicLineGenerator();
    }
}
