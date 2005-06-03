// $Id$

package edu.wpi.mqp.napkin.geometry;

import edu.wpi.mqp.napkin.Renderer;

import java.awt.geom.*;

/**
 * QuadLine: An extension of QuadCurve2D that has added utility methods.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class QuadLine extends QuadCurve2D.Double implements UtilityShape {
    /** Constructs a new <tt>QuadLine</tt> object */
    public QuadLine() {
        super();
    }

    /**
     * @param x1
     * @param y1
     * @param ctrlx
     * @param ctrly
     * @param x2
     * @param y2
     */
    public QuadLine(double x1, double y1, double ctrlx, double ctrly, double x2,
            double y2) {
        super(x1, y1, ctrlx, ctrly, x2, y2);
    }

    /**
     * @param p1
     * @param ctrlpt
     * @param p2
     */
    public QuadLine(Point2D p1, Point2D ctrlpt, Point2D p2) {
        super(p1.getX(), p1.getY(), ctrlpt.getX(), ctrlpt.getY(), p2.getX(), p2
                .getY());
    }

    /**
     * Creates a QuadLine identical to the QuadCurve2D passed in
     *
     * @param q
     */
    public QuadLine(QuadCurve2D q) {
        this(q.getP1(), q.getCtrlPt(), q.getP2());
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#magnify(double) */
    public UtilityShape magnify(double scaleFactor) {
        return new XMLQuadLine(this.x1 * scaleFactor, this.y1 * scaleFactor,
                this.ctrlx * scaleFactor, this.ctrly * scaleFactor, this.x2
                * scaleFactor, this.y2 * scaleFactor);
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToCubic() */
    public CubicLine transformToCubic() {
        Point base = Point.midpoint(this.getP1(), this.getP2());
        StraightLine span = new StraightLine(base, this.getCtrlPt());
        double lenMultiplier = .6;
        if (span.x2 < span.x1) lenMultiplier *= -1;
        Point2D reach = new StraightLine(base, span.angle(), span.length()
                * lenMultiplier).getP2();
        return new CubicLine(this.getP1(), reach, reach, this.getP2());
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToPath() */
    public Path transformToPath() {
        Path ret = new Path();

        Point s = new Point(this.getP1());
        Point c = new Point(this.getCtrlPt());
        Point f = new Point(this.getP2());

        ret.moveTo(s.fX(), s.fY());
        ret.quadTo(c.fX(), c.fY(), f.fX(), f.fY());

        return ret;
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToLine() */
    public StraightLine[] transformToLine() {
        return this.transformToCubic().transformToLine();
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToQuad() */
    public QuadLine[] transformToQuad() {
        QuadLine[] ret = new QuadLine[1];

        ret[0] = new QuadLine(this);

        return ret;
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#deform(edu.wpi.mqp.napkin.Renderer) */
    public UtilityShape deform(Renderer r) {
        return r.deformQuad(this);
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#approximateLength() */
    public double approximateLength() {
        return this.transformToCubic().approximateLength();
    }

    /** @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToCubicList() */
    public CubicLine[] transformToCubicList() {
        CubicLine[] ret = new CubicLine[1];
        ret[0] = this.transformToCubic();
        return ret;
    }
}
