// $Id: IdealSketcher.java 355 2006-03-15 09:15:55Z kcrca $

package net.sourceforge.napkinlaf.sketch.sketchers;

import net.sourceforge.napkinlaf.sketch.AbstractSketcher;
import net.sourceforge.napkinlaf.sketch.geometry.CubicLine;
import net.sourceforge.napkinlaf.sketch.geometry.Path;
import net.sourceforge.napkinlaf.sketch.geometry.QuadLine;
import net.sourceforge.napkinlaf.sketch.geometry.SketchShape;
import net.sourceforge.napkinlaf.sketch.geometry.StraightLine;

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
