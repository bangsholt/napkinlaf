// $Id$

package napkin.sketch.geometry;

import napkin.sketch.Sketcher;

import java.awt.*;
import java.awt.geom.*;
import java.util.LinkedList;

/**
 * Path: A GeneralPath that has added utility methods.
 *
 * @author Justin Crafford
 * @author Peter Goodspeed
 */
public class Path implements SketchShape {
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public boolean contains(double x, double y) {
        return generalPath.contains(x, y);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public boolean intersects(double x, double y, double w, double h) {
        return generalPath.intersects(x, y, w, h);
    }

    /** {@inheritDoc} */
    public Rectangle getBounds() {
        return generalPath.getBounds();
    }

    /** {@inheritDoc} */
    public boolean contains(Point2D p) {
        return generalPath.contains(p);
    }

    /** {@inheritDoc} */
    public Rectangle2D getBounds2D() {
        return generalPath.getBounds();
    }

    /** {@inheritDoc} */
    public boolean contains(Rectangle2D r) {
        return generalPath.contains(r);
    }

    /** {@inheritDoc} */
    public boolean intersects(Rectangle2D r) {
        return generalPath.intersects(r);
    }

    /** {@inheritDoc} */
    public PathIterator getPathIterator(AffineTransform at) {
        return generalPath.getPathIterator(at);
    }

    /** {@inheritDoc} */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return generalPath.getPathIterator(at, flatness);
    }

    /** {@inheritDoc} */
    public SketchShape magnify(double scaleFactor) {
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

    /** {@inheritDoc} */
    public CubicLine transformToCubic() {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    public Path transformToPath() {
        return new Path(this);
    }

    /** {@inheritDoc} */
    public StraightLine[] transformToLine() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<StraightLine> ret = new LinkedList<StraightLine>();

        SketchShape[] elements = simplify();
        StraightLine[] temp;

        for (SketchShape element : elements) {
            temp = element.transformToLine();
            for (StraightLine line : temp) {
                ret.addLast(line);
            }
        }

        return ret.toArray(new StraightLine[ret.size()]);
    }

    /** {@inheritDoc} */
    public QuadLine[] transformToQuad() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<QuadLine> ret = new LinkedList<QuadLine>();

        SketchShape[] elements = simplify();
        QuadLine[] temp;

        for (SketchShape element : elements) {
            temp = element.transformToQuad();
            for (QuadLine line : temp) {
                ret.addLast(line);
            }
        }

        return ret.toArray(new QuadLine[ret.size()]);
    }

    /** {@inheritDoc} */
    public SketchShape deform(Sketcher r) {
        return r.deformPath(this);
    }

    /**
     * @return An array of UtilityShapes which comprise the elements of this
     *         Path
     */
    public SketchShape[] simplify() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<SketchShape> ret = new LinkedList<SketchShape>();

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
        return ret.toArray(new SketchShape[ret.size()]);
    }

    /** {@inheritDoc} */
    public double approximateLength() {
        double ret = 0;

        SketchShape[] elements = simplify();
        for (SketchShape element : elements) {
            ret += element.approximateLength();
        }

        return ret;
    }

    /** {@inheritDoc} */
    public CubicLine[] transformToCubicList() {
        //noinspection CollectionDeclaredAsConcreteClass
        LinkedList<CubicLine> ret = new LinkedList<CubicLine>();

        SketchShape[] elements = simplify();
        for (SketchShape element : elements) {
            ret.addLast(element.transformToCubic());
        }

        return ret.toArray(new CubicLine[ret.size()]);
    }
}