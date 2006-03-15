// $Id$

package napkin.sketch;

import napkin.sketch.geometry.CubicLine;
import napkin.sketch.geometry.Path;
import napkin.sketch.geometry.Point;
import napkin.sketch.geometry.QuadLine;
import napkin.sketch.geometry.SketchShape;
import napkin.sketch.geometry.StraightLine;

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
public abstract class AbstractSketcher {
    /**
     * Sketches a TemplateItem exactly as specified by the TemplateItem itself.
     * Usually only useful for sketching items which have been deformed by other
     * transformations.
     *
     * @param tItem A component of a template specifying geometry and color
     *              information
     * @param g2d   The graphics object on which to sketch the image
     */
    protected static void cleanSketch(TemplateItem tItem, Graphics2D g2d) {
        if (tItem.isDrawFill()) {
            g2d.setColor(tItem.getFillColor());
            g2d.fill(tItem.getShape());
        }
        if (tItem.isDrawStroke()) {
            g2d.setStroke(getPen(tItem.getStrokeWeight()));
            g2d.setColor(tItem.getStrokeColor());

            g2d.draw(tItem.getShape());
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
     */
    public void sketch(Template template, Graphics2D g2d) {
        Iterator<TemplateItem> i = template.getListIterator();
        while (i.hasNext()) {
            TemplateItem current = i.next();
            current.setShape(current.getShape().deform(this));
            cleanSketch(current, g2d);
        }
    }

    /**
     * @return a StraightLine which has been deformed in the manner appropriate
     *         for this sketcher.
     */
    public abstract SketchShape deformLine(StraightLine l);

    /**
     * @return a QuadLine which has been deformed in the manner appropriate for
     *         this sketcher.
     */
    public abstract SketchShape deformQuad(QuadLine q);

    /**
     * @return a CubicLine which has been deformed in the manner appropriate for
     *         this sketcher.
     */
    public abstract SketchShape deformCubic(CubicLine c);

    /**
     * @return a Path which has been deformed in the manner appropriate for this
     *         sketcher.
     */
    public abstract SketchShape deformPath(Path p);

    /** @return a path which represents the input collection of lines. */
    protected static Path formPath(StraightLine[] l) {
        Path ret = new Path();
        Point p = new Point(l[0].getP1());
        ret.moveTo(p.floatX(), p.floatY());

        for (StraightLine line : l) {
            p = new Point(line.getP2());
            ret.lineTo(p.floatX(), p.floatY());
        }

        return ret;
    }
}

