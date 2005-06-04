// $Id$

package edu.wpi.mqp.napkin.geometry;

import edu.wpi.mqp.napkin.Renderer;

import java.awt.*;
import java.awt.geom.*;
import java.util.LinkedList;

/**
 * Path: A GeneralPath that has added utility methods.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
public class Path implements UtilityShape {
    private GeneralPath generalPath;

    /**
     *
     */
    public Path() {
        generalPath = new GeneralPath();
    }

    /** @param rule  */
    public Path(int rule) {
        generalPath = new GeneralPath(rule);
    }

    /**
     * @param rule
     * @param capacity
     */
    public Path(int rule, int capacity) {
        generalPath = new GeneralPath(rule, capacity);
    }

    /** @param s  */
    public Path(Shape s) {
        generalPath = new GeneralPath(s);
    }

    /**
     * @param s
     * @param connect
     */
    public void append(Shape s, boolean connect) {
        generalPath.append(s, connect);
    }

    /** @see Object#clone() */
    public Object clone() {
        try {
            Path clone = (Path) super.clone();
            clone.generalPath = (GeneralPath) generalPath.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("cannot clone?", e);
        }
    }

    /**
     *
     */
    public void closePath() {
        generalPath.closePath();
    }

    /** @see Shape#contains(double, double) */
    public boolean contains(double x, double y) {
        return generalPath.contains(x, y);
    }

    /** @see Shape#contains(double, double, double, double) */
    public boolean contains(double x, double y, double w, double h) {
        return generalPath.contains(x, y, w, h);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void curveTo(float x1, float y1, float x2, float y2, float x3,
            float y3) {
        generalPath.curveTo(x1, y1, x2, y2, x3, y3);
    }

    /** @return the current point */
    public Point2D getCurrentPoint() {
        return generalPath.getCurrentPoint();
    }

    /** @return the current winding rule */
    public int getWindingRule() {
        return generalPath.getWindingRule();
    }

    /**
     * @param x
     * @param y
     */
    public void lineTo(float x, float y) {
        generalPath.lineTo(x, y);
    }

    /**
     * @param x
     * @param y
     */
    public void moveTo(float x, float y) {
        generalPath.moveTo(x, y);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public void quadTo(float x1, float y1, float x2, float y2) {
        generalPath.quadTo(x1, y1, x2, y2);
    }

    /**
     *
     */
    public void reset() {
        generalPath.reset();
    }

    /** @param rule  */
    public void setWindingRule(int rule) {
        generalPath.setWindingRule(rule);
    }

    /** @param at  */
    public void transform(AffineTransform at) {
        generalPath.transform(at);
    }

    /** @see Shape#intersects(double, double, double, double) */
    public boolean intersects(double x, double y, double w, double h) {
        return generalPath.intersects(x, y, w, h);
    }

    /** @see Shape#getBounds() */
    public Rectangle getBounds() {
        return generalPath.getBounds();
    }

    /** @see Shape#contains(Point2D) */
    public boolean contains(Point2D p) {
        return generalPath.contains(p);
    }

    /** @see Shape#getBounds2D() */
    public Rectangle2D getBounds2D() {
        return generalPath.getBounds();
    }

    /** @see Shape#contains(Rectangle2D) */
    public boolean contains(Rectangle2D r) {
        return generalPath.contains(r);
    }

    /** @see Shape#intersects(Rectangle2D) */
    public boolean intersects(Rectangle2D r) {
        return generalPath.intersects(r);
    }

    /** @see Shape#getPathIterator(AffineTransform) */
    public PathIterator getPathIterator(AffineTransform at) {
        return generalPath.getPathIterator(at);
    }

    /** @see Shape#getPathIterator(AffineTransform, double) */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return generalPath.getPathIterator(at, flatness);
    }

    /** @see UtilityShape#magnify(double) */
    public UtilityShape magnify(double scaleFactor) {
        Path ret = new Path();
        Point current;
        Point control1;
        Point control2;

        double[] points = new double[6];
        int type;

        PathIterator i = getPathIterator(new AffineTransform());

        while (!i.isDone()) {
            type = i.currentSegment(points);
            switch (type) {
            case PathIterator.SEG_MOVETO:
                current = new Point(points[0], points[1]).magnify(scaleFactor);
                ret.moveTo(current.fX(), current.fY());
                break;
            case PathIterator.SEG_LINETO:
                current = new Point(points[0], points[1]).magnify(scaleFactor);
                ret.lineTo(current.fX(), current.fY());
                break;
            case PathIterator.SEG_QUADTO:
                control1 = new Point(points[0], points[1]).magnify(scaleFactor);
                current = new Point(points[2], points[3]).magnify(scaleFactor);
                ret.quadTo(control1.fX(), control1.fY(), current.fX(), current
                        .fY());
                break;
            case PathIterator.SEG_CUBICTO:
                control1 = new Point(points[0], points[1]).magnify(scaleFactor);
                control2 = new Point(points[2], points[3]).magnify(scaleFactor);
                current = new Point(points[4], points[5]).magnify(scaleFactor);
                ret.curveTo(control1.fX(), control1.fY(), control2.fX(),
                        control2.fY(), current.fX(), current.fY());
                break;
            case PathIterator.SEG_CLOSE:
                ret.closePath();
                break;
            default:
                throw new IllegalStateException(type + ": unknown");
            }
        }

        return ret;
    }

    /** @see UtilityShape#transformToCubic() */
    public CubicLine transformToCubic() {
        throw new UnsupportedOperationException();
    }

    /** @see UtilityShape#transformToPath() */
    public Path transformToPath() {
        return new Path(this);
    }

    /** @see UtilityShape#transformToLine() */
    public StraightLine[] transformToLine() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<StraightLine> ret = new LinkedList<StraightLine>();

        UtilityShape[] elements = simplify();
        StraightLine[] temp;

        for (UtilityShape element : elements) {
            temp = element.transformToLine();
            for (StraightLine line : temp) {
                ret.addLast(line);
            }
        }

        return ret.toArray(new StraightLine[ret.size()]);
    }

    /** @see UtilityShape#transformToQuad() */
    public QuadLine[] transformToQuad() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<QuadLine> ret = new LinkedList<QuadLine>();

        UtilityShape[] elements = simplify();
        QuadLine[] temp;

        for (UtilityShape element : elements) {
            temp = element.transformToQuad();
            for (QuadLine line : temp) {
                ret.addLast(line);
            }
        }

        return ret.toArray(new QuadLine[ret.size()]);
    }

    /** @see UtilityShape#deform(Renderer) */
    public UtilityShape deform(Renderer r) {
        return r.deformPath(this);
    }

    /**
     * @return An array of UtilityShapes which comprise the elements of this
     *         Path
     */
    public UtilityShape[] simplify() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<UtilityShape> ret = new LinkedList<UtilityShape>();

        Point initial = new Point(0, 0);
        Point current = new Point(initial);
        Point control1;
        Point control2;
        Point far;

        int curseg;
        double[] coords = new double[6];

        PathIterator iter = getPathIterator(new AffineTransform());
        while (!iter.isDone()) {
            curseg = iter.currentSegment(coords);
            iter.next();

            switch (curseg) {
            case PathIterator.SEG_MOVETO:
                current = new Point(coords[0], coords[1]);
                initial = new Point(current);
                break;
            case PathIterator.SEG_LINETO:
                far = new Point(coords[0], coords[1]);
                ret.addLast(new StraightLine(current, far));
                current = far;
                break;
            case PathIterator.SEG_QUADTO:
                control1 = new Point(coords[0], coords[1]);
                far = new Point(coords[2], coords[3]);
                ret.addLast(new QuadLine(current, control1, far));
                current = far;
                break;
            case PathIterator.SEG_CUBICTO:
                control1 = new Point(coords[0], coords[1]);
                control2 = new Point(coords[2], coords[3]);
                far = new Point(coords[4], coords[5]);
                ret.addLast(new CubicLine(current, control1, control2, far));
                current = far;
                break;
            case PathIterator.SEG_CLOSE:
                ret.addLast(new StraightLine(current, initial));
                current = initial;
                break;
            default:
                throw new IllegalStateException(curseg + ": unknown seg type");
            }
        }
        return ret.toArray(new UtilityShape[ret.size()]);
    }

    /** @see UtilityShape#approximateLength() */
    public double approximateLength() {
        double ret = 0;

        UtilityShape[] elements = simplify();
        for (UtilityShape element : elements) {
            ret += element.approximateLength();
        }

        return ret;
    }

    /** @see UtilityShape#transformToCubicList() */
    public CubicLine[] transformToCubicList() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<CubicLine> ret = new LinkedList<CubicLine>();

        UtilityShape[] elements = simplify();
        for (UtilityShape element : elements) {
            ret.addLast(element.transformToCubic());
        }

        return ret.toArray(new CubicLine[ret.size()]);
    }
}
