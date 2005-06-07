// $Id$

package napkin.icon.renderers;

import napkin.icon.Renderer;
import napkin.icon.geometry.CubicLine;
import napkin.icon.geometry.Path;
import napkin.icon.geometry.QuadLine;
import napkin.icon.geometry.StraightLine;
import napkin.icon.geometry.UtilityShape;

/**
 * LineRenderer: Renders things as straight lines.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class LineRenderer extends napkin.icon.Renderer {
    /** @see Renderer#deformLine(StraightLine) */
    public UtilityShape deformLine(StraightLine l) {
        return l;
    }

    /** @see napkin.icon.Renderer#deformQuad(QuadLine) */
    public UtilityShape deformQuad(QuadLine q) {
        return formPath(q.transformToLine());
    }

    /** @see Renderer#deformCubic(CubicLine) */
    public UtilityShape deformCubic(CubicLine c) {
        return formPath(c.transformToLine());
    }

    /** @see Renderer#deformPath(Path) */
    public UtilityShape deformPath(Path p) {
        Path ret = new Path();
        UtilityShape[] elements = p.simplify();

        for (UtilityShape element : elements) {
            ret.append(element.deform(this), false);
        }

        return ret;
    }
}
