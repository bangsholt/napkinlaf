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
 * The {@link Sketcher} class uses the graphical information contained in a
 * {@link Template} to produce an image. The Sketcher is responsible for such
 * things as deforming shapes and changing stroke widths. The altered graphic
 * elements are then rasterized to create an icon's final image.
 * <p/>
 * To create a new sketching style,  you need only implement the abstract
 * methods specifying the deformations to be applied to each shape type.
 * However, you have the option of also overriding the default {@link
 * #deform(Template)} method if you wish.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public abstract class Sketcher {
    /**
     * Sketches a {@link TemplateItem} exactly as specified by the item itself.
     * Usually only useful for sketching items that have been deformed by other
     * transformations.
     *
     * @param tItem A component of a template specifying geometry and color
     *              information.
     * @param g2d   The graphics object on which to sketch the image.
     */
    protected void render(TemplateItem tItem, Graphics2D g2d) {
        if (tItem.isDrawFill()) {
            if (tItem.customFill())
                g2d.setColor(tItem.getFillColor());
            g2d.fill(tItem.getShape());
        }
        if (tItem.isDrawStroke()) {
            if (tItem.customStroke())
                g2d.setStroke(getPen(tItem.getStrokeWeight()));
            if (tItem.customPen())
                g2d.setColor(tItem.getStrokeColor());
            g2d.draw(tItem.getShape());
        }
    }

    /**
     * Returns a drawing pen with round caps and ends, and the specified stroke
     * weight.
     *
     * @param weight The width of the stroke of the pen.
     *
     * @return A pen with round caps and ends and with the specified stroke
     *         weight.
     */
    public static Stroke getPen(float weight) {
        return new BasicStroke(weight, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND);
    }

    /**
     * Actually renders the given template by rendering its template items. This
     * does no deformation.
     *
     * @param template The template to render.
     * @param g2d      The graphics object to use in rendering.
     *
     * @see #deform(Template)
     */
    public void render(Template template, Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Iterator<TemplateItem> items = template.getListIterator();
        while (items.hasNext())
            render(items.next(), g2d);
    }

    /**
     * Deforms the given template by the sketcher's style.
     *
     * @param template The template to deform.
     *
     * @return A new template which is a deformed copy of the original.
     */
    public Template deform(Template template) {
        Template copy = template.clone();
        Iterator<TemplateItem> items = copy.getListIterator();
        while (items.hasNext()) {
            TemplateItem item = items.next();
            item.setShape(item.getShape().deform(this));
        }
        return copy;
    }

    /**
     * Deforms the object and renders it.  This is equivalent to calling {@link
     * #render(Template, Graphics2D)} on a copy returned by {@link
     * #deform(Template)}.
     *
     * @param template The template to sketch.
     * @param g2d      The graphics object on which to sketch the image.
     */
    public void sketch(Template template, Graphics2D g2d) {
        Template copy = deform(template);
        render(copy, g2d);
    }

    /**
     * @return A {@link StraightLine} that has been deformed in the manner
     *         appropriate for this sketcher.
     */
    public abstract SketchShape deformLine(StraightLine l);

    /**
     * @return A {@link QuadLine} that has been deformed in the manner
     *         appropriate for this sketcher.
     */
    public abstract SketchShape deformQuad(QuadLine q);

    /**
     * @return A {@link CubicLine} that has been deformed in the manner
     *         appropriate for this sketcher.
     */
    public abstract SketchShape deformCubic(CubicLine c);

    /**
     * @return A {@link Path } that has been deformed in the manner appropriate
     *         for this sketcher.
     */
    public abstract SketchShape deformPath(Path p);

    /** @return A {@link Path} that represents the input collection of lines. */
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
