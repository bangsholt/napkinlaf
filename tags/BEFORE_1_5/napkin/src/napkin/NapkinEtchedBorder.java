// $Id$

package napkin;

import java.awt.*;
import javax.swing.border.*;

public class NapkinEtchedBorder extends EtchedBorder {
    public NapkinEtchedBorder(EtchedBorder eb) {
        super(eb.getEtchType(), eb.getHighlightColor(), eb.getShadowColor());
    }

    public Color getHighlightColor(Component c) {
        if (highlight != null)
            return highlight;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }

    public Color getShadowColor(Component c) {
        if (shadow != null)
            return shadow;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }
}