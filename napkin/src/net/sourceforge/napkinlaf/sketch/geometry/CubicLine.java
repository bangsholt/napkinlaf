// $Id: CubicLine.java 355 2006-03-15 09:15:55Z kcrca $

package net.sourceforge.napkinlaf.sketch.geometry;

import net.sourceforge.napkinlaf.sketch.AbstractSketcher;

import java.awt.geom.*;

/**
 * CubicLine: An extension of CubicCurve2D that has added utility methods.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class CubicLine extends CubicCurve2D.Double implements SketchShape {
    /** Constructs a new <tt>CubicLine</tt> object. */
    public CubicLine() {
        super();
    }

    /**
     * @param x1    the x coordinate of the initial point.
     * @param y1    the y coordinate of the initial point.
     * @param ctlx1 the x coordinate of the first control point.
     * @param ctly1 the y coordinate of the first control point.
     * @param ctlx2 the x coordinate of the second control point.
     * @param ctly2 the y coordinate of the second control point.
     * @param x2    the x coordinate of the end point.
     * @param y2    the y coordinate of the end point.
     */
    public CubicLine(double x1, double y1, double ctlx1, double ctly1,
            double ctlx2, double ctly2, double x2, double y2) {
        super(x1, y1, ctlx1, ctly1, ctlx2, ctly2, x2, y2);
    }

    /**
     * @param p1   the initial point.
     * @param ctl1 the first control point.
     * @param ctl2 the second control point.
     * @param p2   the end point.
     */
    public CubicLine(Point2D p1, Point2D ctl1, Point2D ctl2, Point2D p2) {
        super(p1.getX(), p1.getY(), ctl1.getX(), ctl1.getY(), ctl2.getX(), ctl2
                .getY(), p2.getX(), p2.getY());
    }

    /**
     * This returns an approximation of the length of the cubic curve. The
     * length is approximated as follows: A path consisting of joined line
     * segments is formed such that the first segment connects point 1 with
     * control point 1, the second segment connects control point 1 with control
     * point 2, and the third segment connects control point 2 with point 2. The
     * length of this path is then divided by (1 + log(flatness(this curve) +
     * 1)).
     *
     * @return the approximate path length of this CubicLine.
     */
    public double approximateLength() {
        StraightLine s1 = new StraightLine(getP1(), getCtrlP1());
        StraightLine s2 = new StraightLine(getCtrlP1(), getCtrlP2());
        StraightLine s3 = new StraightLine(getCtrlP2(), getP2());
        return (s1.length() + s2.length() + s3.length())
                / (1 + Math.log(getFlatness() + 1));
    }

    /** {@inheritDoc} */
    public SketchShape magnify(double scaleFactor) {
        return new XMLCubicLine(x1 * scaleFactor, y1 * scaleFactor,
                ctrlx1 * scaleFactor, ctrly1 * scaleFactor,
                ctrlx2 * scaleFactor, ctrly2 * scaleFactor,
                x2 * scaleFactor, y2 * scaleFactor);
    }

    /** {@inheritDoc} */
    public CubicLine transformToCubic() {
        return this;
    }

    /** {@inheritDoc} */
    public Path transformToPath() {
        Path ret = new Path();

        Point s = new Point(getP1());
        Point c1 = new Point(getCtrlP1());
        Point c2 = new Point(getCtrlP2());
        Point f = new Point(getP2());

        ret.moveTo(s.floatX(), s.floatY());
        ret.curveTo(c1.floatX(), c1.floatY(), c2.floatX(), c2.floatY(),
                f.floatX(), f.floatY());

        return ret;
    }

    /** {@inheritDoc} */
    public StraightLine[] transformToLine() {
        Point mid = Point.midpoint(getP1(), getP2());
        Point q1 = Point.midpoint(getP1(), mid);
        Point q3 = Point.midpoint(mid, getP2());

        StraightLine span = new StraightLine(q1, getCtrlP1());
        double lenmultiplier = 0.4;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D p1 = new Point(q1, span.angle(), span.length() * lenmultiplier);

        span = new StraightLine(q3, getCtrlP2());
        lenmultiplier = 0.4;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D p2 = new Point(q3, span.angle(), span.length() * lenmultiplier);

        span = new StraightLine(getP1(), getCtrlP1());
        lenmultiplier = 0.6;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D s1 = new Point(getP1(), span.angle(), span.length()
                * lenmultiplier);

        span = new StraightLine(getP2(), getCtrlP2());
        lenmultiplier = 0.6;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D s2 = new Point(getP2(), span.angle(), span.length()
                * lenmultiplier);

        Point one = Point.midpoint(p1, s1);
        Point two = Point.midpoint(p2, s2);

        StraightLine[] ret = new StraightLine[3];

        ret[0] = new StraightLine(getP1(), one);
        ret[1] = new StraightLine(one, two);
        ret[2] = new StraightLine(two, getP2());

        return ret;
    }

    /** {@inheritDoc} */
    public QuadLine[] transformToQuad() {
        QuadLine[] ret;

        StraightLine line = new StraightLine(getP1(), getP2());
        Point intersection = line.intersects(
                new StraightLine(getCtrlP1(), getCtrlP2()));
        if (intersection == null) {
            ret = new QuadLine[1];
            ret[0] = new QuadLine(getP1(),
                    Point.midpoint(getCtrlP1(), getCtrlP2()), getP2());
        } else {
            ret = new QuadLine[2];
            ret[0] = new QuadLine(getP1(), getCtrlP1(), intersection);
            ret[1] = new QuadLine(intersection, getCtrlP2(), getP2());
        }

        return ret;
    }

    /** {@inheritDoc} */
    public SketchShape deform(AbstractSketcher r) {
        return r.deformCubic(this);
    }

    /** {@inheritDoc} */
    public CubicLine[] transformToCubicList() {
        CubicLine[] ret = new CubicLine[1];
        ret[0] = transformToCubic();
        return ret;
    }

    @Override
    public CubicLine clone() {
        return (CubicLine) super.clone();
    }
}
