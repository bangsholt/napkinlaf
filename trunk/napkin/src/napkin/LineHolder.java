
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

    public LineHolder(ShapeGenerator gen, boolean vertical) {
        super(gen);
        this.vertical = vertical;
    }

    boolean shapeUpToDate(Rectangle rect, FontMetrics fm) {
        boolean sameMetrics = false;
        if ((metrics == null) == (fm == null))
            sameMetrics = true;
        else if (metrics != null && metrics.equals(fm))
            sameMetrics = true;
        if (sameMetrics && this.rect != null && this.rect.equals(rect))
            return false;

        this.rect = (Rectangle) rect.clone();
        metrics = fm;
        if (vertical)
            rect = new Rectangle(rect.x, rect.y, rect.height, rect.width);

        double below = (fm == null ? 0 : Math.max(fm.getDescent() / 10.0, 2));
        double y = (fm == null ? rect.y : rect.y + fm.getAscent() + below);
        double begX = rect.getMinX() - below;
        double endX = rect.getMaxX() + below;

        AffineTransform matrix = new AffineTransform();
        matrix.translate(begX, y);
        if (vertical)
            matrix.rotate(Math.PI / 2);
        matrix.scale((endX - begX) / ShapeGenerator.LENGTH, 1);
        shape = gen.generate(matrix);
        return true;
    }
}