// $Id$

package napkin;

import java.awt.*;
import java.awt.geom.*;

public class DrawnLineHolder extends DrawnShapeHolder
        implements NapkinConstants {
    private Rectangle rect;
    private FontMetrics metrics;
    private final Endpoints endpoints;

    private static boolean DEBUG = false;

    static final Endpoints HORIZ_LINE = new Endpoints() {
        public Rectangle getEndpoints(Rectangle bounds) {
            return new Rectangle(bounds.x, bounds.y, bounds.width, 0);
        }
    };
    static final Endpoints VERTICAL_LINE = new Endpoints() {
        public Rectangle getEndpoints(Rectangle bounds) {
            return new Rectangle(bounds.x, bounds.y, 0, bounds.height);
        }
    };
    static final Endpoints SLASH_UP = new Endpoints() {
        public Rectangle getEndpoints(Rectangle bounds) {
            return new Rectangle(bounds.x, bounds.y + bounds.height,
                    bounds.width, -bounds.height);
        }
    };

    interface Endpoints {
        Rectangle getEndpoints(Rectangle bounds);
    }

    public DrawnLineHolder(DrawnShapeGenerator gen) {
        this(gen, false);
    }

    public DrawnLineHolder(DrawnShapeGenerator gen, boolean vertical) {
        this(gen, vertical ? VERTICAL_LINE : HORIZ_LINE);
    }

    public DrawnLineHolder(DrawnShapeGenerator gen, Endpoints endpoints) {
        super(gen);
        this.endpoints = endpoints;
    }

    public DrawnLineHolder(double len, boolean vertical) {
        this(generatorFor(len), vertical);
    }

    private static DrawnShapeGenerator generatorFor(double len) {
        Class type = DrawnShapeGenerator.defaultLineType(len);

        if (type == DrawnCubicLineGenerator.class)
            return new DrawnCubicLineGenerator();
        else
            return new DrawnQuadLineGenerator();
    }

    void shapeUpToDate(Rectangle cRect, FontMetrics cMetrics) {
        boolean sameMetrics = false;
        if ((metrics == null) == (cMetrics == null))
            sameMetrics = true;
        else if (metrics != null && metrics.equals(cMetrics))
            sameMetrics = true;
        if (sameMetrics && rect != null && rect.equals(cRect))
            return;

        rect = (Rectangle) cRect.clone();
        metrics = cMetrics;

        Rectangle ends = endpoints.getEndpoints(rect);

        double x1 = ends.getX();
        double y1 = ends.getY();
        double x2 = x1 + ends.getWidth();
        double y2 = y1 + ends.getHeight();
        if (cMetrics != null) {
            double below = Math.max(cMetrics.getDescent() / 10.0, 2);
            double yAdj = cRect.y + cMetrics.getAscent() + below;
            y1 += yAdj;
            y2 += yAdj;
            x1 -= below;
            x2 += below;
        }

        double xDelta = x2 - x1;
        double yDelta = y2 - y1;
        double len = Math.sqrt(xDelta * xDelta + yDelta * yDelta);

        double angle = Math.atan2(yDelta, xDelta);  // y before x (it's sin/cos)

        AffineTransform matrix = new AffineTransform();
        matrix.translate(x1, y1);
        matrix.rotate(angle);
        if (DEBUG) {
            System.out.println("");
            System.out.println("1: (" + x1 + " , " + y1 + ")");
            System.out.println("2: (" + x2 + " , " + y2 + ")");
            System.out.println("rot = " + angle);
            System.out.println("angle = " + angle);
            System.out.println("xDelta = " + xDelta);
            System.out.println("yDelta = " + yDelta);
            System.out.println("len = " + len);
        }
        double xScale = len / DrawnShapeGenerator.LENGTH;
        matrix.scale(xScale, 1);
        shape = gen.generate(matrix);
        return;
    }
}
