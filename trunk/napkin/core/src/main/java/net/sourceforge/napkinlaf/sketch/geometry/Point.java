package net.sourceforge.napkinlaf.sketch.geometry;

import net.sourceforge.napkinlaf.util.NapkinRandom;

import java.awt.geom.*;

/**
 * Point: An extension of Point2D that has added utility methods.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
@SuppressWarnings({"ClassNamingConvention"})
public class Point extends Point2D.Double {
    public Point(double x, double y) {
        super(x, y);
    }

    public Point(Point2D clone) {
        super(clone.getX(), clone.getY());
    }

    public Point(Point2D start, double distance, double angle) {
        this(new StraightLine(start, distance, angle).getP2());
    }

    /**
     * Modifies the position of this point as if the viewport had been magnified
     * towards or from the origin.
     *
     * @param scaleFactor The factor to scale by.
     *
     * @return a point located along the magnification line through the original
     *         and the origin, modified by the scale factor.
     */
    public Point magnify(double scaleFactor) {
        return new Point(x * scaleFactor, y * scaleFactor);
    }

    /**
     * Returns a new <tt>Point</tt> located near the specified point, with a
     * gaussian distribution.
     *
     * @param start the point to start from
     * @param stdev the standard deviation of the distance from the original
     *              point.
     *
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
     * @param stdev the standard deviation of the distance from the original
     *              point.
     *
     * @return a new Point offset from the original by a random distance and
     *         angle.
     */
    private Point random(double stdev) {
        double dist = NapkinRandom.gaussian(stdev);
        double angle = NapkinRandom.nextDouble(Math.PI) - Math.PI / 2;
        return new Point(this, dist, angle);
    }

    /** @return the float value of the x coordinate. */
    public float floatX() {
        return (float) x;
    }

    /** @return the float value of the y coordinate. */
    public float floatY() {
        return (float) y;
    }

    /**
     * @param p1 One point.
     * @param p2 The other point.
     *
     * @return the midpoint of the line between points p1 and p2.
     */
    public static Point midpoint(Point2D p1, Point2D p2) {
        return new StraightLine(p1, p2).midpoint();
    }
}
