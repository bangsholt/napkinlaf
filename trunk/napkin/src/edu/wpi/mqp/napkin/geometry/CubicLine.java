// $Id$

package edu.wpi.mqp.napkin.geometry;

import edu.wpi.mqp.napkin.Renderer;

import java.awt.geom.*;

/**
 * CubicLine: An extension of CubicCurve2D that has added utility methods.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class CubicLine extends Double implements UtilityShape {
    /** Constructs a new <tt>CubicLine</tt> object */
    public CubicLine() {
        super();
    }

    /**
     * Creates a new <tt>CubicLine</tt> identical to the <tt>CubicCurve2D</tt>
     * passed into it
     *
     * @param clone the CubicCurve2D to duplicate
     */
    public CubicLine(CubicCurve2D clone) {
        this(clone.getP1(), clone.getCtrlP1(), clone.getCtrlP2(),
                clone.getP2());
    }

    /**
     * @param x1    the x coordinate of the initial point
     * @param y1    the y coordinate of the initial point
     * @param ctlx1 the x coordinate of the first control point
     * @param ctly1 the y coordinate of the first control point
     * @param ctlx2 the x coordinate of the second control point
     * @param ctly2 the y coordinate of the second control point
     * @param x2    the x coordinate of the end point
     * @param y2    the y coordinate of the end point
     */
    public CubicLine(double x1, double y1, double ctlx1, double ctly1,
            double ctlx2,
            double ctly2, double x2, double y2) {
        super(x1, y1, ctlx1, ctly1, ctlx2, ctly2, x2, y2);
    }

    /**
     * @param p1   the initial point
     * @param ctl1 the first control point
     * @param ctl2 the second control point
     * @param p2   the end point
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
        StraightLine s1 = new StraightLine(this.getP1(), this.getCtrlP1());
        StraightLine s2 = new StraightLine(this.getCtrlP1(), this.getCtrlP2());
        StraightLine s3 = new StraightLine(this.getCtrlP2(), this.getP2());
        return (s1.length() + s2.length() + s3.length())
                / (1 + Math.log(this.getFlatness() + 1));
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#magnify(double) */
    public UtilityShape magnify(double scaleFactor) {
        return new XMLCubicLine(this.x1 * scaleFactor, this.y1 * scaleFactor,
                this.ctrlx1 * scaleFactor, this.ctrly1 * scaleFactor,
                this.ctrlx2
                        * scaleFactor, this.ctrly2 * scaleFactor, this.x2
                * scaleFactor, this.y2 * scaleFactor);
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToCubic() */
    public CubicLine transformToCubic() {
        return this;
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToPath() */
    public Path transformToPath() {
        Path ret = new Path();

        Point s = new Point(this.getP1());
        Point c1 = new Point(this.getCtrlP1());
        Point c2 = new Point(this.getCtrlP2());
        Point f = new Point(this.getP2());

        ret.moveTo(s.fX(), s.fY());
        ret.curveTo(c1.fX(), c1.fY(), c2.fX(), c2.fY(), f.fX(), f.fY());

        return ret;
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToLine() */
    public StraightLine[] transformToLine() {
        Point mid = Point.midpoint(this.getP1(), this.getP2());
        Point q1 = Point.midpoint(this.getP1(), mid);
        Point q3 = Point.midpoint(mid, this.getP2());

        StraightLine span = new StraightLine(q1, this.getCtrlP1());
        double lenmultiplier = .4;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D p1 = new Point(q1, span.angle(), span.length() * lenmultiplier);

        span = new StraightLine(q3, this.getCtrlP2());
        lenmultiplier = .4;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D p2 = new Point(q3, span.angle(), span.length() * lenmultiplier);

        span = new StraightLine(this.getP1(), this.getCtrlP1());
        lenmultiplier = .6;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D s1 = new Point(this.getP1(), span.angle(), span.length()
                * lenmultiplier);

        span = new StraightLine(this.getP2(), this.getCtrlP2());
        lenmultiplier = .6;
        if (span.x2 < span.x1 || (span.x2 == span.x1 && span.y2 < span.y1))
            lenmultiplier *= -1;
        Point2D s2 = new Point(this.getP2(), span.angle(), span.length()
                * lenmultiplier);

        Point one = Point.midpoint(p1, s1);
        Point two = Point.midpoint(p2, s2);

        StraightLine[] ret = new StraightLine[3];

        ret[0] = new StraightLine(this.getP1(), one);
        ret[1] = new StraightLine(one, two);
        ret[2] = new StraightLine(two, this.getP2());

        return ret;
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToQuad() */
    public QuadLine[] transformToQuad() {
        QuadLine[] ret = new QuadLine[0];

        Point intersection = new StraightLine(this.getP1(), this.getP2())
                .intersects(new StraightLine(this.getCtrlP1(),
                this.getCtrlP2()));
        if (intersection == null) {
            ret = new QuadLine[1];
            ret[0] = new QuadLine(this.getP1(),
                    Point.midpoint(this.getCtrlP1(), this
                            .getCtrlP2()), this.getP2());
        } else {
            ret = new QuadLine[2];
            ret[0] = new QuadLine(this.getP1(), this.getCtrlP1(), intersection);
            ret[1] = new QuadLine(intersection, this.getCtrlP2(), this.getP2());
        }

        return ret;
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#deform(edu.wpi.mqp.napkin.Renderer) */
    public UtilityShape deform(Renderer r) {
        return r.deformCubic(this);
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToCubicList() */
    public CubicLine[] transformToCubicList() {
        CubicLine[] ret = new CubicLine[1];
        ret[0] = this.transformToCubic();
        return ret;
    }
}
