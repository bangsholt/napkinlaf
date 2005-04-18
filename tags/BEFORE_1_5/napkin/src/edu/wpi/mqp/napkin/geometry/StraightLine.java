/*
 * Created on Nov 18, 2004 by peterg StraightLine.java in edu.wpi.mqp.napkin.geometry
 * for MQP
 */
package edu.wpi.mqp.napkin.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;

import edu.wpi.mqp.napkin.Renderer;
import edu.wpi.mqp.napkin.XMLUtility;

/**
 * StraightLine: An extension of Line2D that has added utility methods.
 * 
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class StraightLine extends Line2D.Double implements UtilityShape {
	/**
	 *  Constructs a new <tt>StraightLine</tt> object
	 */
	public StraightLine() {
		super();
	}

	/**
	 * @param l
	 */
	public StraightLine(Line2D l) {
		super(l.getP1(), l.getP2());
	}

	/**
	 * @param p1
	 * @param p2
	 */
	public StraightLine(Point2D p1, Point2D p2) {
		super(p1, p2);
	}

	/**
	 * Constructs a new StraightLine given a start point, an angle, and a length
	 * 
	 * @param start
	 *           a point
	 * @param angle
	 *           an angle in radians
	 * @param length
	 *           a length
	 */
	public StraightLine(Point2D start, double angle, double length) {
		super(start, new Point2D.Double(
				start.getX() + (length * Math.cos(angle)), start.getY()
						+ (length * Math.sin(angle))));
	}

	/**
	 * @param x1
	 * @param y1
	 * @param angle
	 * @param length
	 * @see StraightLine#StraightLine(Point2D, double, double)
	 */
	public StraightLine(double x1, double y1, double angle, double length) {
		this(new Point2D.Double(x1, y1), angle, length);
	}

	/**
	 * @return the length of this line segment.
	 */
	public double length() {
		return Math.sqrt(Math.pow(this.x2 - this.x1, 2)
				+ Math.pow(this.y2 - this.y1, 2));
	}

	/**
	 * @param l
	 * @return the length of line l
	 */
	public static double length(Line2D l) {
		return new StraightLine(l).length();
	}

	/**
	 * @return the slope of this line in mathematical terms; delta y over delta x
	 */
	public double slope() {
		return (this.x2 - this.x1 == 0) ? java.lang.Double.POSITIVE_INFINITY
				: ((this.y2 - this.y1) / (this.x2 - this.x1));
	}

	/**
	 * @param l
	 * @return the slope of line l
	 */
	public static double slope(Line2D l) {
		return new StraightLine(l).slope();
	}

	/**
	 * @return the y value of this line when x is set to 0
	 */
	public double yIntercept() {
		return (this.slope() == java.lang.Double.POSITIVE_INFINITY) ? java.lang.Double.POSITIVE_INFINITY
				: (this.y1 - (this.slope() * this.x1));
	}

	/**
	 * @param l
	 * @return the y intercept of l
	 * @see StraightLine#yIntercept()
	 */
	public static double yIntercept(Line2D l) {
		return new StraightLine(l).yIntercept();
	}

	/**
	 * @return the angle of this line in the range pi/2 to -pi/2 in radians
	 */
	public double angle() {
		return Math.atan(this.slope());
	}

	/**
	 * @param l
	 * @return the angle of line l in the range pi/2 to -pi/2 in radians
	 */
	public static double angle(Line2D l) {
		return new StraightLine(l).angle();
	}

	/**
	 * @return an XML representation of this element
	 */
	public Element produceXML() {
		DefaultJDOMFactory f = new DefaultJDOMFactory();
		Element ret = f.element("straightLine");

		ret.addContent(XMLUtility.pointToXML(this.getP1(), "start"));
		ret.addContent(XMLUtility.pointToXML(this.getP2(), "end"));

		return ret;
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#magnify(double)
	 */
	public UtilityShape magnify(double scaleFactor) {
		return new XMLStraightLine(
				new Point(this.x1 * scaleFactor, this.y1 * scaleFactor), new Point(
						this.x2 * scaleFactor, this.y2 * scaleFactor));
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToCubic()
	 */
	public CubicLine transformToCubic() {
		return new CubicLine(this.getP1(), this.getP1(), this.getP2(), this.getP2());
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToPath()
	 */
	public Path transformToPath() {
		Path ret = new Path();

		Point s = new Point(this.getP1());
		Point f = new Point(this.getP2());

		ret.moveTo(s.fX(), s.fY());
		ret.lineTo(f.fX(), f.fY());

		return ret;
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToLine()
	 */
	public StraightLine[] transformToLine() {
		StraightLine[] ret = new StraightLine[1];
		ret[0] = new StraightLine(this);
		return ret;
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToQuad()
	 */
	public QuadLine[] transformToQuad() {
		QuadLine[] ret = new QuadLine[1];
		ret[0] = new QuadLine(this.getP1(), this.midpoint(), this.getP2());
		return ret;
	}

	/**
	 * @param o
	 *           another StraightLine
	 * @return the point of intersection of the two lines, or null if they do not
	 *         intersect
	 */
	public Point intersects(StraightLine o) {
		if (this.intersectsLine(o)) {
			double slope = this.slope();
			double slopeprime = o.slope();

			double b = this.y1 - (slope * this.x1);
			double bprime = o.y1 - (slopeprime * o.x1);

			double x = 0;
			double y = 0;

			if (slope == java.lang.Double.POSITIVE_INFINITY
					|| slopeprime == java.lang.Double.POSITIVE_INFINITY) {
				if (slope == java.lang.Double.POSITIVE_INFINITY) {
					x = this.x1;
					y = (slopeprime * x) + bprime;
				} else if (slopeprime == java.lang.Double.POSITIVE_INFINITY) {
					x = o.x1;
					y = (slope * x) + b;
				}
			} else {
				x = (b - bprime) / (slopeprime - slope);
				y = (((slope * x) + b) + ((slopeprime * x) + bprime)) / 2;
			}
			return new Point(x, y);
		} else {
			return null;
		}
	}

	/**
	 * @param l1
	 * @param l2
	 * @return the point of intersection of the two lines, or null if they do not
	 *         intersect
	 * @see StraightLine#intersects(StraightLine)
	 */
	public static Point intersects(Line2D l1, Line2D l2) {
		return new StraightLine(l1).intersects(new StraightLine(l2));
	}

	/**
	 * @return the midpoint of this StraightLine
	 */
	public Point midpoint() {
		return new Point((this.x2 + this.x1) / 2, (this.y2 + this.y1) / 2);
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#deform(edu.wpi.mqp.napkin.Renderer)
	 */
	public UtilityShape deform(Renderer r) {
		return r.deformLine(this);
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#approximateLength()
	 */
	public double approximateLength() {
		return this.length();
	}

	/**
	 * @see edu.wpi.mqp.napkin.geometry.UtilityShape#transformToCubicList()
	 */
	public CubicLine[] transformToCubicList() {
		CubicLine[] ret = new CubicLine[1];
		ret[0] = this.transformToCubic();
		return ret;
	}
}