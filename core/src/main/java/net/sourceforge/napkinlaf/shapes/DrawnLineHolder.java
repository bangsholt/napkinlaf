package net.sourceforge.napkinlaf.shapes;

import static net.sourceforge.napkinlaf.util.NapkinConstants.LENGTH;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import java.awt.*;
import java.awt.geom.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"WeakerAccess"})
public class DrawnLineHolder extends DrawnShapeHolder {
    private Rectangle rect;
    private FontMetrics metrics;
    private final Endpoints endpoints;

    private static final Endpoints HORIZ_LINE = new Endpoints() {
        public Rectangle getEndpoints(Rectangle bounds) {
            return new Rectangle(bounds.x, bounds.y, bounds.width, 0);
        }
    };
    private static final Endpoints VERTICAL_LINE = new Endpoints() {
        public Rectangle getEndpoints(Rectangle bounds) {
            return new Rectangle(bounds.x, bounds.y, 0, bounds.height);
        }
    };

    private static final Logger logger = Logger.getLogger(
            DrawnLineHolder.class.getName());

    public interface Endpoints {
        Rectangle getEndpoints(Rectangle bounds);
    }

    public DrawnLineHolder(AbstractDrawnGenerator gen) {
        this(gen, false);
    }

    public DrawnLineHolder(AbstractDrawnGenerator gen, boolean vertical) {
        this(gen, vertical ? VERTICAL_LINE : HORIZ_LINE);
    }

    public DrawnLineHolder(AbstractDrawnGenerator gen, Endpoints endpoints) {
        super(gen);
        this.endpoints = endpoints;
    }

    public DrawnLineHolder(double len, boolean vertical) {
        this(generatorFor(len), vertical);
    }

    private static AbstractDrawnGenerator generatorFor(double len) {
        Class<?> type = AbstractDrawnGenerator.defaultLineType(len);
        return type == DrawnCubicLineGenerator.class ?
                new DrawnCubicLineGenerator() :
                new DrawnQuadLineGenerator();
    }

    public void shapeUpToDate(Rectangle cRect, FontMetrics cMetrics) {
        boolean sameMetrics = (metrics == null) == (cMetrics == null) ||
                (metrics != null && metrics.equals(cMetrics));
        if (!sameMetrics || rect == null || !rect.equals(cRect)) {
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

            double angle = Math.atan2(yDelta,
                    xDelta);  // y before x (it's sin/cos)

            AffineTransform matrix = new AffineTransform();
            matrix.translate(x1, y1);
            matrix.rotate(angle);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "");
                NapkinUtil.printPair(logger, Level.FINE, "1: ", x1, y1);
                NapkinUtil.printPair(logger, Level.FINE, "2: ", x2, y2);
                NapkinUtil.printPair(logger, Level.FINE, "delta = ", xDelta,
                        yDelta);
                logger.log(Level.FINE, "rot = " + angle);
                logger.log(Level.FINE, "angle = " + angle);
                logger.log(Level.FINE, "len = " + len);
            }
            double xScale = len / LENGTH;
            matrix.scale(xScale, 1);
            shape = gen.generate(matrix);
        }
    }
}
