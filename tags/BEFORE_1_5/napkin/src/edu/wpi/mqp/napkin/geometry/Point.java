/*
 * Created on Nov 22, 2004 by peterg Point.java in edu.wpi.mqp.napkin for MQP
 */
package edu.wpi.mqp.napkin.geometry;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * Point: An extension of Point2D that has added utility methods.
 * 
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class Point extends Point2D.Double {
	private static Random rng = new Random();
	
	/**
	 * Constructs a new <tt>Point</tt> object
	 */
	public Point() {
		super();
	}

	/**
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		super(x, y);
	}

	/**
	 * @param clone
	 */
	public Point(Point2D clone) {
		super(clone.getX(),clone.getY());
	}

	/**
	 * @param start
	 * @param distance
	 * @param angle
	 */
	public Point(Point2D start, double distance, double angle) {
		this(new StraightLine(start,distance,angle).getP2());
	}

	/**
	 * Modifies the position of this point as if the viewport
	 * had been magnified towards or from the origin
	 * 
	 * @param scaleFactor
	 * @return a point located along the magnification line through the 
	 * original and the origin, modified by the scale factor.
	 */
	public Point magnify(double scaleFactor) {
		return new Point(this.x * scaleFactor, this.y * scaleFactor);
	}

	/**
	 * Returns a new <tt>Point</tt> located near the specified point, with a 
	 * gaussian distribution. 
	 * 
	 * @param start
	 *            the point to start from
	 * @param stdev
	 *            the standard deviation of the distance from the original
	 *            point.
	 * @return a new Point offset from the original by a random distance and
	 *         angle.
	 */
	public static Point random(Point2D start, double stdev) {
		return new Point(start).random(stdev);
	}
	
	/**
	 * Returns a new Point located near the specified point, with a gaussian
	 * distribution. 
	 * 
	 * @param stdev
	 *            the standard deviation of the distance from the original
	 *            point.
	 * @return a new Point offset from the original by a random distance and
	 *         angle.
	 */
	public Point random(double stdev) {
		double dist = rng.nextGaussian() * stdev;
		double angle = (rng.nextDouble() * Math.PI) - (Math.PI / 2);
		return new Point(this, dist, angle);
	}
	
	/**
	 * @return the float value of the x coordinate
	 */
	public float fX() {
		return new java.lang.Float(this.x).floatValue();
	}
	
	/**
	 * @return the float value of the y coordinate
	 */
	public float fY() {
		return new java.lang.Float(this.y).floatValue();
	}
	
	/**
	 * @param p1
	 * @param p2
	 * @return the midpoint of the line between points p1 and p2
	 */
	public static Point midpoint(Point2D p1, Point2D p2) {
		return new StraightLine(p1,p2).midpoint();
	}
}