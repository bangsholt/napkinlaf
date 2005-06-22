// $Id$

package napkin.sketch.sketchers;

import napkin.NapkinRandom;
import napkin.sketch.Sketcher;
import napkin.sketch.geometry.CubicLine;
import napkin.sketch.geometry.Path;
import napkin.sketch.geometry.QuadLine;
import napkin.sketch.geometry.SketchShape;
import napkin.sketch.geometry.StraightLine;

/**
 * DraftSketcher: Sketches like a drafter might: lots of straight lines and
 * simple curves. In this current incarnation, it's not quite done and doesn't
 * yet look very professional. Curves are a bit of a problem.
 *
 * @author Peter Goodspeed
 * @author Justin Crafford
 */
public class DraftSketcher extends Sketcher {
    private static final double DEFORM_FACTOR = 0.05;

    /** @see Sketcher#deformLine(StraightLine) */
    public SketchShape deformLine(StraightLine l) {
        StraightLine ret = new StraightLine(l);

        double scale = l.length() * DEFORM_FACTOR;

        if (l.slope() == Double.POSITIVE_INFINITY) {
            if (ret.y2 < ret.y1)
                scale = -scale;
            ret.y2 += Math.abs(NapkinRandom.gaussian() + 1) * scale;
            ret.y1 -= Math.abs(NapkinRandom.gaussian() + 1) * scale;
        } else {
            if (ret.x2 < ret.x1)
                scale = -scale;
            double bonusL = Math.abs(NapkinRandom.gaussian() + 1) * scale;
            double bonusR = Math.abs(NapkinRandom.gaussian() + 1) * scale;

            double cos = Math.cos(l.angle());
            ret.x1 -= bonusL / cos;
            ret.x2 += bonusR / cos;

            double intercept = l.yIntercept();
            ret.y1 = l.slope() * l.x1 + intercept;
            ret.y2 = l.slope() * l.x2 + intercept;
        }
        return ret;
    }

    /** @see Sketcher#deformQuad(QuadLine) */
    public SketchShape deformQuad(QuadLine q) {
        if (q.getFlatness() < q.approximateLength() * DEFORM_FACTOR) {
            return new StraightLine(q.getP1(), q.getP2()).deform(this);
        } else {
            return q;
        }
    }

    /** @see Sketcher#deformCubic(CubicLine) */
    public SketchShape deformCubic(CubicLine c) {
        if (c.getFlatness() < c.approximateLength() * DEFORM_FACTOR * 0.5) {
            return new StraightLine(c.getP1(), c.getP2()).deform(this);
        } else {
            return c;
        }
    }

    /** @see Sketcher#deformPath(Path) */
    public SketchShape deformPath(Path p) {
        Path ret = new Path();
        SketchShape[] elements = p.simplify();

        for (SketchShape element : elements) {
            ret.append(element.deform(this), false);
        }

        return ret;
    }
}
