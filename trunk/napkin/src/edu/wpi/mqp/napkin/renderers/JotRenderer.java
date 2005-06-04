// $Id$

package edu.wpi.mqp.napkin.renderers;

import edu.wpi.mqp.napkin.Renderer;
import edu.wpi.mqp.napkin.Template;
import edu.wpi.mqp.napkin.TemplateItem;
import edu.wpi.mqp.napkin.geometry.CubicLine;
import edu.wpi.mqp.napkin.geometry.Path;
import edu.wpi.mqp.napkin.geometry.Point;
import edu.wpi.mqp.napkin.geometry.QuadLine;
import edu.wpi.mqp.napkin.geometry.StraightLine;
import edu.wpi.mqp.napkin.geometry.UtilityShape;

import java.awt.*;
import java.awt.geom.*;
import java.util.Iterator;
import java.util.Random;

/**
 * JotRenderer: Renders the image in such a manner that it resembles something
 * which has been hand-drawn. This is accomplished by transforming all the
 * component lines into cubics, and then manipulating the endpoints and the
 * control points.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class JotRenderer extends Renderer {
    private static final double DEFORM_FACTOR = 0.2;

    /** @see Renderer#render(Template, Graphics2D) */
    public void render(Template template, Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        TemplateItem draw;

        Iterator<TemplateItem> iter = template.getListIterator();
        while (iter.hasNext()) {
            TemplateItem current = iter.next();
            draw = (TemplateItem) current.clone();
            if (current.isDrawFill()) {
                draw.setDrawStroke(false);
                draw.setDrawFill(true);

                draw.setShape(deform(current.getShape().transformToPath(),
                        true));

                //try the new scribble generator
                //				draw.setShape(this.generateScribblePath(
                //						current.getShape().transformToPath()).deform(this));
                //it is horrible

                quickRender(draw, g2d);
            }
            if (current.isDrawStroke()) {
                draw.setDrawFill(false);
                draw.setDrawStroke(true);

                UtilityShape u = current.getShape().deform(this);
                draw.setStrokeWeight(
                        computeStrokeModifier(u.approximateLength()));
                draw.setShape(u);

                quickRender(draw, g2d);
            }
        }
    }

    private CubicLine deform(CubicLine c) {
        return deform(c, true);
    }

    private CubicLine deform(CubicLine c, boolean perturbInitial) {
        double twopercent = c.approximateLength() * DEFORM_FACTOR;

        return new CubicLine((perturbInitial ?
                Point.random(c.getP1(), twopercent)
                : new Point(c.getP1())), Point
                .random(c.getCtrlP1(), twopercent * 5),
                Point.random(c.getCtrlP2(),
                        twopercent * 5), Point.random(c.getP2(), twopercent));
    }

    /**
     * Deforms a Path to resemble something which might have been drawn by hand.
     * Equivalent to deform(Path, false).
     *
     * @param p
     *
     * @return a Path resembling the original Path, but deformed
     *
     * @see JotRenderer#deform(Path, boolean)
     */
    private Path deform(Path p) {
        return deform(p, false);
    }

    /**
     * As deform(Path) but with the additional behavior: if <tt>close</tt> is
     * true, an attempt is made to close the path.
     *
     * @param p
     * @param close
     *
     * @return a closed Path resembling the original, but deformed.
     *
     * @see JotRenderer#deform(Path)
     */
    private Path deform(Path p, boolean close) {
        Path ret = new Path();
        Point initial = new Point(0, 0);
        Point current = new Point(0, 0);
        Point ctrl1;
        Point ctrl2;
        Point far;
        UtilityShape seg;
        CubicLine draw;

        int segType;
        double[] coords = new double[6];

        PathIterator pi = p.getPathIterator(new AffineTransform());
        while (!pi.isDone()) {
            segType = pi.currentSegment(coords);
            pi.next();

            switch (segType) {
            case PathIterator.SEG_MOVETO:
                current = new Point(coords[0], coords[1]);
                initial = new Point(current);
                ret.moveTo(current.fX(), current.fY());
                seg = null;
                break;
            case PathIterator.SEG_LINETO:
                far = new Point(coords[0], coords[1]);
                seg = new StraightLine(current, far);
                current = new Point(far);
                break;
            case PathIterator.SEG_QUADTO:
                ctrl1 = new Point(coords[0], coords[1]);
                far = new Point(coords[2], coords[3]);
                seg = new QuadLine(current, ctrl1, far);
                current = new Point(far);
                break;
            case PathIterator.SEG_CUBICTO:
                ctrl1 = new Point(coords[0], coords[1]);
                ctrl2 = new Point(coords[2], coords[3]);
                far = new Point(coords[4], coords[5]);
                seg = new CubicLine(current, ctrl1, ctrl2, far);
                current = new Point(far);
                break;
            case PathIterator.SEG_CLOSE:
                seg = new StraightLine(current, initial);
                current = new Point(initial);
                break;
            default:
                throw new IllegalStateException(segType + ": unknown");
            }
            if (seg != null) {
                draw = deform(seg.transformToCubic(), false);
                ctrl1 = new Point(draw.getCtrlP1());
                ctrl2 = new Point(draw.getCtrlP2());
                far = new Point(draw.getP2());
                ret.curveTo(ctrl1.fX(), ctrl1.fY(), ctrl2.fX(), ctrl2.fY(), far
                        .fX(), far.fY());
            }
        }

        if (close) {
            draw = deform(new StraightLine(current, initial).transformToCubic(),
                    false);
            ctrl1 = new Point(draw.getCtrlP1());
            ctrl2 = new Point(draw.getCtrlP2());
            far = new Point(draw.getP2());
            ret.curveTo(ctrl1.fX(), ctrl1.fY(), ctrl2.fX(), ctrl2.fY(),
                    far.fX(),
                    far.fY());
            ret.closePath();
        }

        return ret;
    }

    private float computeStrokeModifier(double lineLength) {
        float ret;

        if (lineLength < 1.68) {
            ret = (float) (2 - (0.19 * lineLength));
        } else {
            ret = (float)
                    (Math.pow(lineLength + 0.5, 2) / Math.pow(lineLength, 2));
        }
        ret *= new Random().nextGaussian() * 0.15 + 1;

        return ret;
    }

    /** @see Renderer#deformLine(StraightLine) */
    public UtilityShape deformLine(StraightLine l) {
        return l.transformToCubic().deform(this);
    }

    /** @see Renderer#deformQuad(QuadLine) */
    public UtilityShape deformQuad(QuadLine q) {
        return q.transformToCubic().deform(this);
    }

    /** @see Renderer#deformCubic(CubicLine) */
    public UtilityShape deformCubic(CubicLine c) {
        return deform(c);
    }

    /** @see Renderer#deformPath(Path) */
    public UtilityShape deformPath(Path p) {
        return deform(p);
    }
}
