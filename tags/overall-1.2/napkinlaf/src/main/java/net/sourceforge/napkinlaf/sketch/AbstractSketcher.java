package net.sourceforge.napkinlaf.sketch;

import net.sourceforge.napkinlaf.sketch.geometry.CubicLine;
import net.sourceforge.napkinlaf.sketch.geometry.Path;
import net.sourceforge.napkinlaf.sketch.geometry.Point;
import net.sourceforge.napkinlaf.sketch.geometry.QuadLine;
import net.sourceforge.napkinlaf.sketch.geometry.SketchShape;
import net.sourceforge.napkinlaf.sketch.geometry.StraightLine;

import java.awt.*;
import java.util.Iterator;

/**
 * The <tt>AbstractSketcher<tt> class uses the graphical information contained
 * in a Template to produce an image. The Sketcher is responsible for such
 * things as deforming shapes and changing stroke widths. The altered graphic
 * elements are then rasterized to create an icon's final image.
 * <p/>
 * A developer wishing to create a new sketching style needs only implement the
 * abstract methods specifying the deformations that need to be applied to each
 * UtilityShape type. However, they have the option of also overriding the
 * default <tt>sketch</tt> method if they wish.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
@SuppressWarnings({"StaticMethodOnlyUsedInOneClass", "WeakerAccess"})
public abstract class AbstractSketcher {
    /**
     * Sketches a TemplateItem exactly as specified by the TemplateItem itself.
     * Usually only useful for sketching items which have been deformed by other
     * transformations.
     *
     * @param item A component of a template specifying geometry and color
     *             information
     * @param g2d  The graphics object on which to sketch the image
     * @param c    The component to sketch on, which is used for abstract colors
     *             such as "pen". No drawing is done here.
     */
    protected static void cleanSketch(TemplateItem item, Graphics2D g2d,
            Component c) {

        if (item.isDrawFill()) {
            g2d.setColor(item.getFillColor(c));
            g2d.fill(item.getShape());
        }
        if (item.isDrawStroke()) {
            g2d.setStroke(getPen(item.getStrokeWeight()));
            g2d.setColor(item.getStrokeColor(c));

            g2d.draw(item.getShape());
        }
    }

    /**
     * Returns a drawing pen with round caps and ends, and the specified stroke
     * weight.
     *
     * @param weight The width of the stroke of the pen
     *
     * @return a pen with round caps and ends and with the specified stroke
     *         weight
     */
    public static Stroke getPen(float weight) {
        return new BasicStroke(weight, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
    }

    /**
     * Performs the actual drawing of the template on a Graphics2D object.
     *
     * @param template the template to sketch
     * @param g2d      the graphics object on which to sketch the image
     * @param c        The component to sketch on, which is used for abstract
     *                 colors such as "pen". No drawing is done here.
     */
    public void sketch(Template template, Graphics2D g2d, Component c) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Iterator<TemplateItem> i = template.getListIterator();
        while (i.hasNext()) {
            TemplateItem current = i.next();
            current.setShape(current.getShape().deform(this));
            cleanSketch(current, g2d, c);
        }
    }

    /**
     * @param l Line to start from.
     *
     * @return A {@link StraightLine} which has been deformed in the manner
     *         appropriate for this sketcher.
     */
    public abstract SketchShape deformLine(StraightLine l);

    /**
     * @param q Line to start from.
     *
     * @return A {@link QuadLine} which has been deformed in the manner
     *         appropriate for this sketcher.
     */
    public abstract SketchShape deformQuad(QuadLine q);

    /**
     * @param c Line to start from.
     *
     * @return A {@link CubicLine} which has been deformed in the manner
     *         appropriate for this sketcher.
     */
    public abstract SketchShape deformCubic(CubicLine c);

    /**
     * @param p The path to start from.
     *
     * @return A {@link Path} which has been deformed in the manner appropriate
     *         for this sketcher.
     */
    public abstract SketchShape deformPath(Path p);

    /**
     * @param lines The lines that constitute the path.
     *
     * @return A path which represents the input collection of lines.
     */
    protected static Path formPath(StraightLine[] lines) {
        Path ret = new Path();
        Point p = new Point(lines[0].getP1());
        ret.moveTo(p.floatX(), p.floatY());

        for (StraightLine line : lines) {
            p = new Point(line.getP2());
            ret.lineTo(p.floatX(), p.floatY());
        }

        return ret;
    }
}

