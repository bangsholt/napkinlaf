// $Id$

package napkin.sketch.geometry;

import napkin.sketch.Sketcher;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import java.awt.geom.*;

/**
 * StraightLine: An extension of Line2D that has added utility methods.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class StraightLine extends Line2D.Double implements SketchShape {
    /** Constructs a new <tt>StraightLine</tt> object. */
    public StraightLine() {
        super();
    }

    public StraightLine(Line2D l) {
        super(l.getP1(), l.getP2());
    }

    public StraightLine(Point2D p1, Point2D p2) {
        super(p1, p2);
    }

    /**
     * Constructs a new StraightLine given a start point, an angle, and a
     * length.
     *
     * @param start  a point
     * @param angle  an angle in radians
     * @param length a length
     */
    public StraightLine(Point2D start, double angle, double length) {
        super(start, new Point2D.Double(
                start.getX() + (length * Math.cos(angle)), start.getY()
                + (length * Math.sin(angle))));
    }

    /** @see StraightLine#StraightLine(Point2D, double, double) */
    public StraightLine(double x1, double y1, double angle, double length) {
        this(new Point2D.Double(x1, y1), angle, length);
    }

    /** @return the length of this line segment. */
    public double length() {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * @return the slope of this line in mathematical terms; delta y over delta
     *         x.
     */
    public double slope() {
        return (x2 - x1 == 0) ? java.lang.Double.POSITIVE_INFINITY
                : ((y2 - y1) / (x2 - x1));
    }

    /** @return the y value of this line when x is set to 0. */
    public double yIntercept() {
        return (slope() == java.lang.Double.POSITIVE_INFINITY) ?
                java.lang.Double.POSITIVE_INFINITY : (y1 - (slope() * x1));
    }

    /** @return the angle of this line in the range pi/2 to -pi/2 in radians. */
    public double angle() {
        return Math.atan(slope());
    }

    /** @return an XML representation of this element. */
    public Element produceXML() {
        DefaultJDOMFactory f = new DefaultJDOMFactory();
        Element ret = f.element("straightLine");

        ret.addContent(napkin.sketch.XMLUtility.pointToXML(getP1(), "start"));
        ret.addContent(napkin.sketch.XMLUtility.pointToXML(getP2(), "end"));

        return ret;
    }

    /** {@inheritDoc} */
    public SketchShape magnify(double scaleFactor) {
        return new XMLStraightLine(
                new Point(x1 * scaleFactor, y1 * scaleFactor),
                new Point(x2 * scaleFactor, y2 * scaleFactor));
    }

    /** {@inheritDoc} */
    public CubicLine transformToCubic() {
        return new CubicLine(getP1(), getP1(), getP2(), getP2());
    }

    /** {@inheritDoc} */
    public Path transformToPath() {
        Path ret = new Path();

        Point s = new Point(getP1());
        Point f = new Point(getP2());

        ret.moveTo(s.fX(), s.fY());
        ret.lineTo(f.fX(), f.fY());

        return ret;
    }

    /** {@inheritDoc} */
    public StraightLine[] transformToLine() {
        StraightLine[] ret = new StraightLine[1];
        ret[0] = new StraightLine(this);
        return ret;
    }

    /** {@inheritDoc} */
    public QuadLine[] transformToQuad() {
        QuadLine[] ret = new QuadLine[1];
        ret[0] = new QuadLine(getP1(), midpoint(), getP2());
        return ret;
    }

    /**
     * @param o another StraightLine.
     *
     * @return the point of intersection of the two lines, or null if they do
     *         not intersect.
     */
    @SuppressWarnings("MethodOverloadsMethodOfSuperclass")
    public Point intersects(StraightLine o) {
        if (!intersectsLine(o)) {
            return null;
        } else {
            double slope = slope();
            double slopeprime = o.slope();

            double b = y1 - (slope * x1);
            double bprime = o.y1 - (slopeprime * o.x1);

            double x;
            double y;

            if (slope == java.lang.Double.POSITIVE_INFINITY) {
                x = x1;
                y = (slopeprime * x) + bprime;
            } else if (slopeprime == java.lang.Double.POSITIVE_INFINITY) {
                x = o.x1;
                y = (slope * x) + b;
            } else {
                x = (b - bprime) / (slopeprime - slope);
                y = (((slope * x) + b) + ((slopeprime * x) + bprime)) / 2;
            }
            return new Point(x, y);
        }
    }

    /** @return the midpoint of this StraightLine. */
    public Point midpoint() {
        return new Point((x2 + x1) / 2, (y2 + y1) / 2);
    }

    /** {@inheritDoc} */
    public SketchShape deform(Sketcher r) {
        return r.deformLine(this);
    }

    /** {@inheritDoc} */
    public double approximateLength() {
        return length();
    }

    /** {@inheritDoc} */
    public CubicLine[] transformToCubicList() {
        CubicLine[] ret = new CubicLine[1];
        ret[0] = transformToCubic();
        return ret;
    }
}
