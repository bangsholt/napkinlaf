// $Id$

package napkin;

import java.awt.geom.*;

public class RandomXY {
    private final RandomValue x, y;
    private Point2D point;
    private Point2D mid;
    private Point2D min;
    private Point2D max;

    public RandomXY(double xMid, double yMid) {
        this(xMid, 0, yMid, 0);
    }

    public RandomXY(double xMid, double xRange, double yMid, double yRange) {
        this(new RandomValue(xMid, xRange), new RandomValue(yMid, yRange));
    }

    public RandomXY(RandomValue x, RandomValue y) {
        this.x = x;
        this.y = y;
    }

    public void randomize() {
        x.randomize();
        y.randomize();
        point = null;
    }

    public Point2D get() {
        if (point == null)
            point = new Point2D.Double(x.get(), y.get());
        return point;
    }

    public Point2D generate() {
        randomize();
        return get();
    }

    public RandomValue getX() {
        return x;
    }

    public RandomValue getY() {
        return y;
    }

    public void setMid(double xMid, double yMid) {
        x.setMid(xMid);
        y.setMid(yMid);
        mid = min = max = null;
    }

    public Point2D getMid() {
        if (mid == null)
            mid = new Point2D.Double(x.getMid(), y.getMid());
        return mid;
    }

    public Point2D max() {
        if (max == null)
            max = new Point2D.Double(x.max(), y.max());
        return max;
    }

    public Point2D min() {
        if (min == null)
            min = new Point2D.Double(x.min(), y.min());
        return min;
    }
}