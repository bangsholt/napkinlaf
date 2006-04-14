package net.sourceforge.napkinlaf.borders;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;

public class NapkinEtchedBorder extends EtchedBorder implements NapkinBorder {
    public NapkinEtchedBorder(EtchedBorder eb) {
        super(eb.getEtchType(), eb.getHighlightColor(), eb.getShadowColor());
    }

    @Override
    public Color getHighlightColor(Component c) {
        return highlight == null ?
            NapkinUtil.currentTheme(c).getPenColor() : highlight;
    }

    @Override
    public Color getShadowColor(Component c) {
        return shadow == null ?
            NapkinUtil.currentTheme(c).getPenColor() : shadow;
    }
}
