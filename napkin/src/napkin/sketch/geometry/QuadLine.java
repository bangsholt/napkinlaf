// $Id$

package napkin.sketch.geometry;

import napkin.sketch.AbstractSketcher;

import java.awt.geom.*;

/**
 * QuadLine: An extension of QuadCurve2D that has added utility methods.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class QuadLine extends QuadCurve2D.Double implements SketchShape {
    /** Constructs a new <tt>QuadLine</tt> object. */
    public QuadLine() {
        super();
    }

    public QuadLine(double x1, double y1, double ctrlx, double ctrly, double x2,
            double y2) {
        super(x1, y1, ctrlx, ctrly, x2, y2);
    }

    public QuadLine(Point2D p1, Point2D ctrlpt, Point2D p2) {
        super(p1.getX(), p1.getY(), ctrlpt.getX(), ctrlpt.getY(), p2.getX(), p2
                .getY());
    }

    /** Creates a QuadLine identical to the QuadCurve2D passed in. */
    public QuadLine(QuadCurve2D q) {
        this(q.getP1(), q.getCtrlPt(), q.getP2());
    }

    /** {@inheritDoc} */
    public SketchShape magnify(double scaleFactor) {
        return new XMLQuadLine(x1 * scaleFactor, y1 * scaleFactor,
                ctrlx * scaleFactor, ctrly * scaleFactor,
                x2 * scaleFactor, y2 * scaleFactor);
    }

    /** {@inheritDoc} */
    public CubicLine transformToCubic() {
        Point base = Point.midpoint(getP1(), getP2());
        StraightLine span = new StraightLine(base, getCtrlPt());
        double lenMultiplier = 0.6;
        if (span.x2 < span.x1) lenMultiplier *= -1;
        Point2D reach = new StraightLine(base, span.angle(),
                span.length() * lenMultiplier).getP2();
        return new CubicLine(getP1(), reach, reach, getP2());
    }

    /** {@inheritDoc} */
    public Path transformToPath() {
        Path ret = new Path();

        Point s = new Point(getP1());
        Point c = new Point(getCtrlPt());
        Point f = new Point(getP2());

        ret.moveTo(s.fX(), s.fY());
        ret.quadTo(c.fX(), c.fY(), f.fX(), f.fY());

        return ret;
    }

    /** {@inheritDoc} */
    public StraightLine[] transformToLine() {
        return transformToCubic().transformToLine();
    }

    /** {@inheritDoc} */
    public QuadLine[] transformToQuad() {
        QuadLine[] ret = new QuadLine[1];

        ret[0] = new QuadLine(this);

        return ret;
    }

    /** {@inheritDoc} */
    public SketchShape deform(AbstractSketcher r) {
        return r.deformQuad(this);
    }

    /** {@inheritDoc} */
    public double approximateLength() {
        return transformToCubic().approximateLength();
    }

    /** {@inheritDoc} */
    public CubicLine[] transformToCubicList() {
        CubicLine[] ret = new CubicLine[1];
        ret[0] = transformToCubic();
        return ret;
    }

    public QuadLine clone() {
        return new QuadLine(this);
    }

}
