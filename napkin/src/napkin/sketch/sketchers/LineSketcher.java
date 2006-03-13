// $Id$

package napkin.sketch.sketchers;

import napkin.sketch.AbstractSketcher;
import napkin.sketch.geometry.CubicLine;
import napkin.sketch.geometry.Path;
import napkin.sketch.geometry.QuadLine;
import napkin.sketch.geometry.SketchShape;
import napkin.sketch.geometry.StraightLine;

/**
 * LineSketcher: Sketches things as straight lines.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class LineSketcher extends AbstractSketcher {
    /** {@inheritDoc} */
    public SketchShape deformLine(StraightLine l) {
        return l;
    }

    /** {@inheritDoc} */
    public SketchShape deformQuad(QuadLine q) {
        return formPath(q.transformToLine());
    }

    /** {@inheritDoc} */
    public SketchShape deformCubic(CubicLine c) {
        return formPath(c.transformToLine());
    }

    /** {@inheritDoc} */
    public SketchShape deformPath(Path p) {
        Path ret = new Path();
        SketchShape[] elements = p.simplify();

        for (SketchShape element : elements) {
            ret.append(element.deform(this), false);
        }

        return ret;
    }
}
