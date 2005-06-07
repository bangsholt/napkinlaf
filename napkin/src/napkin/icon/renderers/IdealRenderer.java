// $Id$

package napkin.icon.renderers;

import napkin.icon.Renderer;
import napkin.icon.geometry.CubicLine;
import napkin.icon.geometry.Path;
import napkin.icon.geometry.QuadLine;
import napkin.icon.geometry.StraightLine;
import napkin.icon.geometry.UtilityShape;

/**
 * IdealRenderer: Renders a template without performing any deformations or
 * color changes. This renders the image exactly as the template specifies.
 *
 * @author Peter Goodpseed
 * @author Justin Crafford
 */
public class IdealRenderer extends Renderer {
    /** @see napkin.icon.Renderer#deformLine(StraightLine) */
    public UtilityShape deformLine(StraightLine l) {
        return l;
    }

    /** @see Renderer#deformQuad(QuadLine) */
    public UtilityShape deformQuad(QuadLine q) {
        return q;
    }

    /** @see napkin.icon.Renderer#deformCubic(CubicLine) */
    public UtilityShape deformCubic(CubicLine c) {
        return c;
    }

    /** @see napkin.icon.Renderer#deformPath(Path) */
    public UtilityShape deformPath(Path p) {
        return p;
    }
}
