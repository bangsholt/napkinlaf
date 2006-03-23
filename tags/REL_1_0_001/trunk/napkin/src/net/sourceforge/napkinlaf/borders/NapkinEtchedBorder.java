// $Id$

package net.sourceforge.napkinlaf.borders;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;

public class NapkinEtchedBorder extends EtchedBorder {
    public NapkinEtchedBorder(EtchedBorder eb) {
        super(eb.getEtchType(), eb.getHighlightColor(), eb.getShadowColor());
    }

    @Override
    public Color getHighlightColor(Component c) {
        if (highlight != null)
            return highlight;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }

    @Override
    public Color getShadowColor(Component c) {
        if (shadow != null)
            return shadow;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }
}