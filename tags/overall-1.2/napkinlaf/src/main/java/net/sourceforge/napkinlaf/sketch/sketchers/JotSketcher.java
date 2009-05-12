package net.sourceforge.napkinlaf.sketch.sketchers;

import net.sourceforge.napkinlaf.sketch.AbstractSketcher;
import net.sourceforge.napkinlaf.sketch.Template;
import net.sourceforge.napkinlaf.sketch.TemplateItem;
import net.sourceforge.napkinlaf.sketch.geometry.CubicLine;
import net.sourceforge.napkinlaf.sketch.geometry.Path;
import net.sourceforge.napkinlaf.sketch.geometry.Point;
import net.sourceforge.napkinlaf.sketch.geometry.QuadLine;
import net.sourceforge.napkinlaf.sketch.geometry.SketchShape;
import net.sourceforge.napkinlaf.sketch.geometry.StraightLine;
import net.sourceforge.napkinlaf.util.NapkinRandom;

import java.awt.*;
import java.awt.geom.*;
import java.util.Iterator;

/**
 * JotSketcher: Sketches the image in such a manner that it resembles something
 * which has been hand-drawn. This is accomplished by transforming all the
 * component lines into cubics, and then manipulating the endpoints and the
 * control points.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class JotSketcher extends AbstractSketcher {
    private static final double DEFORM_FACTOR = 0.2;

    /** {@inheritDoc} */
    @Override
    public void sketch(Template template, Graphics2D g2d, Component c) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Iterator<TemplateItem> iter = template.getListIterator();
        while (iter.hasNext()) {
            TemplateItem current = iter.next();
            TemplateItem draw = current.clone();
            if (current.isDrawFill()) {
                draw.setDrawStroke(false);
                draw.setDrawFill(true);

                draw.setShape(deform(current.getShape().transformToPath(),
                        true));

                cleanSketch(draw, g2d, c);
            }
            if (current.isDrawStroke()) {
                draw.setDrawFill(false);
                draw.setDrawStroke(true);

                SketchShape u = current.getShape().deform(this);
                draw.setStrokeWeight(computeStrokeModifier(
                        u.approximateLength()));
                draw.setShape(u);

                cleanSketch(draw, g2d, c);
            }
        }
    }

    private static CubicLine deform(CubicLine c) {
        return deform(c, true);
    }

    private static CubicLine deform(CubicLine c, boolean perturbInitial) {
        double twopercent = c.approximateLength() * DEFORM_FACTOR;

        Point p1 = (perturbInitial ?
                Point.random(c.getP1(), twopercent) :
                new Point(c.getP1()));
        return new CubicLine(p1, Point.random(c.getCtrlP1(), twopercent * 5),
                Point.random(c.getCtrlP2(), twopercent * 5), Point.random(
                        c.getP2(), twopercent));
    }

    /**
     * Deforms a {@link Path} to resemble something which might have been drawn
     * by hand. Equivalent to <tt>deform(Path, false)</tt>.
     *
     * @param p Path to deform.
     *
     * @return a Path resembling the original Path, but deformed.
     *
     * @see JotSketcher#deform(Path,boolean)
     */
    private static Path deform(Path p) {
        return deform(p, false);
    }

    /**
     * As {@link #deform(Path)}, but with the additional behavior: If
     * <tt>close</tt> is <tt>true</tt>, an attempt is made to close the path.
     *
     * @param p     Path to deform.
     * @param close If <tt>true</tt>, close the path when finished.
     *
     * @return a closed Path resembling the original, but deformed.
     *
     * @see JotSketcher#deform(Path)
     */
    @SuppressWarnings({"TooBroadScope"})
    private static Path deform(Path p, boolean close) {
        Path ret = new Path();
        Point initial = new Point(0, 0);
        Point current = new Point(0, 0);
        Point ctrl1;
        Point ctrl2;
        Point far;
        SketchShape seg;
        CubicLine draw;

        double[] coords = new double[6];

        PathIterator pi = p.getPathIterator(new AffineTransform());
        while (!pi.isDone()) {
            int segType = pi.currentSegment(coords);
            pi.next();

            // Do we need to keep creating new points or can we set coordinates
            switch (segType) {
            case PathIterator.SEG_MOVETO:
                current = new Point(coords[0], coords[1]);
                initial = new Point(current);
                ret.moveTo(current.floatX(), current.floatY());
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
                ret.curveTo(ctrl1.floatX(), ctrl1.floatY(), ctrl2.floatX(),
                        ctrl2.floatY(), far.floatX(), far.floatY());
            }
        }

        if (close) {
            draw = deform(new StraightLine(current, initial).transformToCubic(),
                    false);
            ctrl1 = new Point(draw.getCtrlP1());
            ctrl2 = new Point(draw.getCtrlP2());
            far = new Point(draw.getP2());
            ret.curveTo(ctrl1.floatX(), ctrl1.floatY(), ctrl2.floatX(),
                    ctrl2.floatY(), far.floatX(), far.floatY());
            ret.closePath();
        }

        return ret;
    }

    private static float computeStrokeModifier(double lineLength) {
        double ret;

        if (lineLength < 1.68) {
            ret = 2 - 0.19 * lineLength;
        } else {
            ret = Math.pow(lineLength + 0.5, 2) / Math.pow(lineLength, 2);
        }
        ret *= NapkinRandom.gaussian(0.15) + 1;

        return (float) ret;
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformLine(StraightLine l) {
        return l.transformToCubic().deform(this);
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformQuad(QuadLine q) {
        return q.transformToCubic().deform(this);
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformCubic(CubicLine c) {
        return deform(c);
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformPath(Path p) {
        return deform(p);
    }
}
