package net.sourceforge.napkinlaf.sketch.sketchers;

import net.sourceforge.napkinlaf.sketch.Sketcher;
import net.sourceforge.napkinlaf.sketch.Template;
import net.sourceforge.napkinlaf.sketch.TemplateItem;
import net.sourceforge.napkinlaf.sketch.geometry.CubicLine;
import net.sourceforge.napkinlaf.sketch.geometry.Path;
import net.sourceforge.napkinlaf.sketch.geometry.Point;
import net.sourceforge.napkinlaf.sketch.geometry.QuadLine;
import net.sourceforge.napkinlaf.sketch.geometry.SketchShape;
import net.sourceforge.napkinlaf.sketch.geometry.StraightLine;
import static net.sourceforge.napkinlaf.util.NapkinConstants.LENGTH;
import net.sourceforge.napkinlaf.util.NapkinRandom;
import net.sourceforge.napkinlaf.util.NapkinUtil;
import net.sourceforge.napkinlaf.util.RandomValue;
import net.sourceforge.napkinlaf.util.RandomXY;

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
public class JotSketcher extends Sketcher {
    private static final double DEFORM_FACTOR = 0.2;
    private static final double CUBIC_LEN = 5.0;

    private static final RandomXY start = new RandomXY(-1, 3, 0, 2.5);
    private static final RandomValue startAdjust = new RandomValue(5);
    private static final RandomXY mid = new RandomXY(60, 3, 0, 0.5);
    private static final RandomXY left = new RandomXY(10, 4, -0.7, 1.5);
    private static final RandomXY right = new RandomXY(20, 8, -1.3, 2);

    /** {@inheritDoc} */
    @Override
    public void sketch(Template template, Graphics2D g2d) {
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

                render(draw, g2d);
            }
            if (current.isDrawStroke()) {
                draw.setDrawFill(false);
                draw.setDrawStroke(true);

                SketchShape u = current.getShape().deform(this);
                draw.setStrokeWeight(
                        computeStrokeModifier(u.approximateLength()));
                draw.setShape(u);

                render(draw, g2d);
            }
        }
    }

    private static CubicLine deform(CubicLine c) {
        return deform(c, true);
    }

    private static CubicLine deform(CubicLine c, boolean perturbInitial) {
        double len = c.approximateLength();
        if (len < 16) {
            double twopercent = len * DEFORM_FACTOR;

            Point p1 = (perturbInitial ?
                    Point.random(c.getP1(), twopercent) : new Point(c.getP1()));
            return new CubicLine(p1,
                    Point.random(c.getCtrlP1(), twopercent * 5),
                    Point.random(c.getCtrlP2(), twopercent * 5),
                    Point.random(c.getP2(), twopercent));
        } else {
            Rectangle2D bounds = c.getBounds2D();
            double angle = Math.atan2(bounds.getHeight(), bounds.getWidth());
            AffineTransform matrix = new AffineTransform();
            matrix.rotate(angle);
            matrix.scale(len / LENGTH, 1);

            Point2D leftAt = left.generate();
            Point2D rightAt = right.generate();
            double lx = NapkinUtil.leftRight(leftAt.getX(), true);
            double ly = leftAt.getY();
            double rx = NapkinUtil.leftRight(rightAt.getX(), false);
            double ry = rightAt.getY();

            if (perturbInitial) {
                lx += startAdjust.generate();
                ly += startAdjust.generate();
            }

            double[] coords = {0, 0, lx, ly, rx, ry, LENGTH, 0};
            matrix.transform(coords, 0, coords, 0, 4);

            return new CubicLine(
                    coords[0], coords[1],
                    coords[2], coords[3],
                    coords[4], coords[5],
                    coords[6], coords[7]);
        }
    }

    /**
     * Deforms a Path to resemble something which might have been drawn by hand.
     * Equivalent to deform(Path, false).
     *
     * @param p
     *
     * @return a Path resembling the original Path, but deformed
     *
     * @see JotSketcher#deform(Path, boolean)
     */
    private static Path deform(Path p) {
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

        boolean needClose = close;
        PathIterator pi = p.getPathIterator(new AffineTransform());
        while (!pi.isDone() || needClose) {
            int segType;
            if (!pi.isDone()) {
                segType = pi.currentSegment(coords);
                pi.next();
            } else {
                assert needClose;
                needClose = false;
                segType = PathIterator.SEG_LINETO;
                coords[0] = initial.getX();
                coords[1] = initial.getY();
            }

            // Do we need to keep creating new points or can we set coordinates
            switch (segType) {
            case PathIterator.SEG_MOVETO:
                current = new Point(coords[0], coords[1]);
                initial = new Point(current);
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
                ret.append(draw, true);
            }
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
        double basicLen = l.approximateLength();
        if (basicLen >= CUBIC_LEN)
            return l.transformToCubic().deform(this);
        else
            return l.transformToQuad().deform(this);
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformQuad(QuadLine q) {
        return q.transformToQuad().deform(this);
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
