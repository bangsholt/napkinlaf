package net.sourceforge.napkinlaf.shapes;

import net.sourceforge.napkinlaf.NapkinTheme;
import net.sourceforge.napkinlaf.sketch.DrawnIcon;
import net.sourceforge.napkinlaf.sketch.Template;
import net.sourceforge.napkinlaf.sketch.TemplateItem;
import net.sourceforge.napkinlaf.sketch.Sketcher;
import net.sourceforge.napkinlaf.sketch.geometry.Path;
import static net.sourceforge.napkinlaf.util.NapkinConstants.LENGTH;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import java.awt.*;
import java.awt.geom.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawnLineHolder extends DrawnShapeHolder {
    private Rectangle rect;
    private FontMetrics metrics;
    private Template sketched;
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
                new DrawnCubicLineGenerator() : new DrawnQuadLineGenerator();
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

            Path path = new Path(new Line2D.Double(x1, y1, x2, y2));
            TemplateItem item = new TemplateItem(path);
            Template template = new Template(item);
            sketched = sketcher().deform(template);
            template.setCustomAll(false);
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = NapkinUtil.lineGraphics(g, width, cap, join);
        g2d.translate(0, width / 2);
        sketcher().render(sketched, g2d);
    }

    private Sketcher sketcher() {
        return NapkinTheme.Manager.getCurrentTheme().getSketcher();
    }
}
