// $Id$

package edu.wpi.mqp.napkin.renderers;

import edu.wpi.mqp.napkin.Renderer;
import edu.wpi.mqp.napkin.geometry.CubicLine;
import edu.wpi.mqp.napkin.geometry.Path;
import edu.wpi.mqp.napkin.geometry.QuadLine;
import edu.wpi.mqp.napkin.geometry.StraightLine;
import edu.wpi.mqp.napkin.geometry.UtilityShape;

/**
 * IdealRenderer: Renders a template without performing any deformations or
 * color changes. This renders the image exactly as the template specifies.
 *
 * @author Peter Goodpseed
 * @author Justin Crafford
 */
public class IdealRenderer extends Renderer {
    /** @see Renderer#deformLine(StraightLine) */
    public UtilityShape deformLine(StraightLine l) {
        return l;
    }

    /** @see Renderer#deformQuad(QuadLine) */
    public UtilityShape deformQuad(QuadLine q) {
        return q;
    }

    /** @see Renderer#deformCubic(CubicLine) */
    public UtilityShape deformCubic(CubicLine c) {
        return c;
    }

    /** @see Renderer#deformPath(Path) */
    public UtilityShape deformPath(Path p) {
        return p;
    }
}
