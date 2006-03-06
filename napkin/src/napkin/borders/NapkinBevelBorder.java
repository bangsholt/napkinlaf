// $Id$

package napkin.borders;

import napkin.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;

public class NapkinBevelBorder extends BevelBorder {
    public NapkinBevelBorder(BevelBorder bb) {
        super(bb.getBevelType(), bb.getHighlightOuterColor(),
                bb.getHighlightInnerColor(), bb.getShadowOuterColor(),
                bb.getShadowInnerColor());
    }

    public Color getHighlightInnerColor(Component c) {
        if (highlightInner != null)
            return highlightInner;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }

    public Color getHighlightOuterColor(Component c) {
        if (highlightOuter != null)
            return highlightOuter;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }

    public Color getShadowInnerColor(Component c) {
        if (shadowInner != null)
            return shadowInner;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }

    public Color getShadowOuterColor(Component c) {
        if (shadowOuter != null)
            return shadowOuter;
        else
            return NapkinUtil.currentTheme(c).getPenColor();
    }
}