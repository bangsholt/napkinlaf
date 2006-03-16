// $Id: LineSketcher.java 355 2006-03-15 09:15:55Z kcrca $

package net.sourceforge.napkinlaf.sketch.sketchers;

import net.sourceforge.napkinlaf.sketch.AbstractSketcher;
import net.sourceforge.napkinlaf.sketch.geometry.CubicLine;
import net.sourceforge.napkinlaf.sketch.geometry.Path;
import net.sourceforge.napkinlaf.sketch.geometry.QuadLine;
import net.sourceforge.napkinlaf.sketch.geometry.SketchShape;
import net.sourceforge.napkinlaf.sketch.geometry.StraightLine;

/**
 * LineSketcher: Sketches things as straight lines.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class LineSketcher extends AbstractSketcher {
    /** {@inheritDoc} */
    @Override
    public SketchShape deformLine(StraightLine l) {
        return l;
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformQuad(QuadLine q) {
        return formPath(q.transformToLine());
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformCubic(CubicLine c) {
        return formPath(c.transformToLine());
    }

    /** {@inheritDoc} */
    @Override
    public SketchShape deformPath(Path p) {
        Path ret = new Path();
        SketchShape[] elements = p.simplify();

        for (SketchShape element : elements) {
            ret.append(element.deform(this), false);
        }

        return ret;
    }
}
