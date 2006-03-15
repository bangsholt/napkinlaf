// $Id$

package napkin.util;

import javax.swing.plaf.*;
import java.awt.*;
import java.awt.color.*;

/**
 * This is required because <tt>ColorUIResource</tt> discards any alpha
 * component.  I've filed a bug against this.  [Note: Actually somebody else
 * already filed such a report, but the made two requests in the same RFE
 * request, and since they fixed one of the two, they marked the RFE as "done"
 * without fixing this one. Let this be a lesson to all and sundry to only put
 * one issue in each report.]
 */
public class AlphaColorUIResource extends Color implements UIResource {
    public AlphaColorUIResource(Color c) {
        super(c.getColorSpace(), c.getColorComponents(null),
                c.getAlpha() / 255.0f);
    }

    @SuppressWarnings({"SameParameterValue"})
    public AlphaColorUIResource(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public AlphaColorUIResource(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public AlphaColorUIResource(int rgba) {
        super(rgba, true);
    }

    public AlphaColorUIResource(ColorSpace space, float[] comp, float alpha) {
        super(space, comp, alpha);
    }
}
