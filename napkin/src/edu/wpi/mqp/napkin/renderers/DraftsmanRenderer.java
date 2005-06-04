// $Id$

package edu.wpi.mqp.napkin.renderers;

import edu.wpi.mqp.napkin.Renderer;
import edu.wpi.mqp.napkin.geometry.CubicLine;
import edu.wpi.mqp.napkin.geometry.Path;
import edu.wpi.mqp.napkin.geometry.QuadLine;
import edu.wpi.mqp.napkin.geometry.StraightLine;
import edu.wpi.mqp.napkin.geometry.UtilityShape;

import java.util.Random;

/**
 * DraftsmanRenderer: Renders like a draftsman might: lots of straight lines and
 * simple curves. In this current incarnation, it's not quite done and doesn't
 * yet look very professional. Curves are a bit of a problem.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class DraftsmanRenderer extends Renderer {
    private static final double DEFORM_FACTOR = 0.05;
    private static Random rng = new Random();

    /** @see Renderer#deformLine(StraightLine) */
    public UtilityShape deformLine(StraightLine l) {
        StraightLine ret = new StraightLine(l);

        double scale = l.length() * DEFORM_FACTOR;

        if (l.slope() == Double.POSITIVE_INFINITY) {
            if (ret.y2 < ret.y1) scale *= -1;
            ret.y2 += Math.abs(rng.nextGaussian() + 1) * scale;
            ret.y1 -= Math.abs(rng.nextGaussian() + 1) * scale;
        } else {
            if (ret.x2 < ret.x1) scale *= -1;
            double bonusL = Math.abs(rng.nextGaussian() + 1) * scale;
            double bonusR = Math.abs(rng.nextGaussian() + 1) * scale;

            ret.x1 -= bonusL / Math.cos(l.angle());
            ret.x2 += bonusR / Math.cos(l.angle());

            ret.y1 = l.slope() * l.x1 + l.yIntercept();
            ret.y2 = l.slope() * l.x2 + l.yIntercept();
        }
        return ret;
    }

    /** @see Renderer#deformQuad(QuadLine) */
    public UtilityShape deformQuad(QuadLine q) {
        if (q.getFlatness() < (q.approximateLength() * DEFORM_FACTOR)) {
            return new StraightLine(q.getP1(), q.getP2()).deform(this);
        } else {
            return q;
        }
    }

    /** @see Renderer#deformCubic(CubicLine) */
    public UtilityShape deformCubic(CubicLine c) {
        if (c.getFlatness() < (c.approximateLength() * DEFORM_FACTOR * 0.5)) {
            return new StraightLine(c.getP1(), c.getP2()).deform(this);
        } else {
            return c;
        }
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
