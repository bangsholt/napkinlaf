// $Id$

package napkin.sketch.sketchers;

import napkin.sketch.AbstractSketcher;
import napkin.sketch.geometry.CubicLine;
import napkin.sketch.geometry.Path;
import napkin.sketch.geometry.QuadLine;
import napkin.sketch.geometry.SketchShape;
import napkin.sketch.geometry.StraightLine;

/**
 * IdealSketcher: Sketches a template without performing any deformations or
 * color changes. This sketches the image exactly as the template specifies.
 *
 * @author Peter Goodpseed
 * @author Justin Crafford
 */
public class IdealSketcher extends AbstractSketcher {
    /** {@inheritDoc} */
    @Override
    public SketchShape deformLine(StraightLine l) {
        return l;
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformQuad(QuadLine q) {
        return q;
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformCubic(CubicLine c) {
        return c;
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformPath(Path p) {
        return p;
    }
}