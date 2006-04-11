package net.sourceforge.napkinlaf.borders;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;

public class NapkinBevelBorder extends BevelBorder {
    public NapkinBevelBorder(BevelBorder bb) {
        super(bb.getBevelType(), bb.getHighlightOuterColor(),
                bb.getHighlightInnerColor(), bb.getShadowOuterColor(),
                bb.getShadowInnerColor());
    }

    @Override
    public Color getHighlightInnerColor(Component c) {
        return highlightInner == null ?
            NapkinUtil.currentTheme(c).getPenColor() : highlightInner;
    }

    @Override
    public Color getHighlightOuterColor(Component c) {
        return highlightOuter == null ?
            NapkinUtil.currentTheme(c).getPenColor() : highlightOuter;
    }

    @Override
    public Color getShadowInnerColor(Component c) {
        return shadowInner == null ?
            NapkinUtil.currentTheme(c).getPenColor() : shadowInner;
    }

    @Override
    public Color getShadowOuterColor(Component c) {
        return shadowOuter == null ?
            NapkinUtil.currentTheme(c).getPenColor() : shadowOuter;
    }
}
