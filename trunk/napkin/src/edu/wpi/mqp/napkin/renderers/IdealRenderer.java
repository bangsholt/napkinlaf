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
    /** @see edu.wpi.mqp.napkin.Renderer#deformLine(edu.wpi.mqp.napkin.geometry.StraightLine) */
    public UtilityShape deformLine(StraightLine l) {
        return l;
    }

    /** @see edu.wpi.mqp.napkin.Renderer#deformQuad(edu.wpi.mqp.napkin.geometry.QuadLine) */
    public UtilityShape deformQuad(QuadLine q) {
        return q;
    }

    /** @see edu.wpi.mqp.napkin.Renderer#deformCubic(edu.wpi.mqp.napkin.geometry.CubicLine) */
    public UtilityShape deformCubic(CubicLine c) {
        return c;
    }

    /** @see edu.wpi.mqp.napkin.Renderer#deformPath(edu.wpi.mqp.napkin.geometry.Path) */
    public UtilityShape deformPath(Path p) {
        return p;
    }
}
