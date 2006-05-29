package net.sourceforge.napkinlaf.sketch.geometry;

import net.sourceforge.napkinlaf.sketch.Sketcher;

import java.awt.*;

/**
 * The {@link SketchShape} class defines a set of utility methods which are used
 * within the Napkin Drawing subsystem. An important point of note is that one
 * of the most important methods is not actually specified by the interface
 * itself, but should still hold true for all implementors of this interface:
 * there should be a constructor which accepts the highest-level
 * non-<tt>Object</tt> class in the tree, and constructs a new member of this
 * shape class whose path is identical to that of the one passed in. Thus,
 * {@link XMLStraightLine}, which implements {@link XMLShape}, which is a
 * subinterface of this, has a constructor thus: <tt>new
 * XMLStraightLine(Line2D)</tt>. This allows easy access to all the
 * functionality of these classes without having to deal with factories.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public interface SketchShape extends Shape, Cloneable {
    /**
     * Returns a shape geometrically similar to this, magnified by the scale
     * factor. This transformation does not affect this shape; only the returned
     * shape. The scale is applied from the origin. The transformation can be
     * visualized as drawing lines connecting each point defining this shape to
     * the origin, then multiplying all of those derived lines by the scale
     * factor. Alternately, it can be visualized as multiplying the x and y
     * coordinates of each defining point by the scale factor.
     *
     * @param scaleFactor The scaling factor.
     *
     * @return A {@link SketchShape} geometrically similar to this one, scaled
     *         by the scaleFactor.
     */
    SketchShape magnify(double scaleFactor);

    /** @return An approximation of this shape, sketched as a set of Lines. */
    StraightLine[] transformToLine();

    /** @return A representation of this shape in the form of a QuadLine. */
    QuadLine transformToQuad();

    /** @return An approximation of this shape, sketched as a set of Quads. */
    QuadLine[] transformToQuadList();

    /** @return A representation of this shape in the form of a CubicLine. */
    CubicLine transformToCubic();

    /** @return An approximation of this shape, sketched as a set of Cubics. */
    CubicLine[] transformToCubicList();

    /** @return A representation of this shape in the form of a Path. */
    Path transformToPath();

    /**
     * Deforms this shape by the appropriate method within the sketcher. Thus,
     * the implementation of this method will nearly always take the following
     * form:
     * <pre>
     * return r.deform<i>SketchShapeName</i>(this);
     * </pre>
     *
     * @param sketcher The sketcher.
     *
     * @return This shape, deformed by the appropriate method within the
     *         sketcher
     */
    SketchShape deform(Sketcher sketcher);

    /**
     * @return The approximate length of this item. This should be optimized for
     *         speed of computation, not accuracy. However, in general it should
     *         be accurate to within at least 20%.
     */
    double approximateLength();

    @SuppressWarnings({"override"})
    SketchShape clone();
}
