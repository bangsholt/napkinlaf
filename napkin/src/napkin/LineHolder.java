package napkin;

import java.awt.*;
import java.awt.geom.*;

class LineHolder extends ShapeHolder implements NapkinConstants {
    private Rectangle rect;
    private FontMetrics metrics;
    private final boolean vertical;

    LineHolder(ShapeGenerator gen) {
        this(gen, false);
    }

    LineHolder(ShapeGenerator gen, boolean vertical) {
        super(gen);
        this.vertical = vertical;
    }

    boolean shapeUpToDate(Rectangle cRect, FontMetrics cMetrics) {
        boolean sameMetrics = false;
        if ((metrics == null) == (cMetrics == null))
            sameMetrics = true;
        else if (metrics != null && metrics.equals(cMetrics))
            sameMetrics = true;
        if (sameMetrics && rect != null && rect.equals(cRect))
            return false;

        rect = (Rectangle) cRect.clone();
        metrics = cMetrics;
        if (vertical)
            cRect = new Rectangle(cRect.x, cRect.y, cRect.height, cRect.width);

        double below = (cMetrics == null ? 0 : Math.max(cMetrics.getDescent() / 10.0, 2));
        double y = (cMetrics == null ? cRect.y : cRect.y + cMetrics.getAscent() + below);
        double begX = cRect.getMinX() - below;
        double endX = cRect.getMaxX() + below;

        AffineTransform matrix = new AffineTransform();
        matrix.translate(begX, y);
        if (vertical)
            matrix.rotate(Math.PI / 2);
        matrix.scale((endX - begX) / ShapeGenerator.LENGTH, 1);
        shape = gen.generate(matrix);
        return true;
    }
}