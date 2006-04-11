package net.sourceforge.napkinlaf.borders;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;
import java.util.Map;
import java.util.WeakHashMap;

public class NapkinWrappedBorder extends AbstractNapkinBorder {
    private final Border origBorder;

    private static final Map<Border, Border> borders =
            new WeakHashMap<Border, Border>(3);

    public NapkinWrappedBorder(Border origBorder) {
        super();
        this.origBorder = origBorder;
    }

    @Override
    protected Insets doGetBorderInsets(Component c, Insets insets) {
        Insets result = origBorder.getBorderInsets(c);
        insets.set(result.top, result.left, result.bottom, result.right);
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return origBorder.isBorderOpaque();
    }

    @Override
    protected void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {

        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.syncWithTheme(origBorder, c);
        origBorder.paintBorder(c, g, x, y, width, height);
        NapkinUtil.finishGraphics(g, c);
    }

    public Border getOrigBorder() {
        return origBorder;
    }

    public static NapkinWrappedBorder wrap(Border origBorder) {
        NapkinWrappedBorder result;
        Object brd = borders.get(origBorder);
        // Not all passed borders are wrapped yet...
        if (brd instanceof NapkinWrappedBorder) {
            result = (NapkinWrappedBorder) brd;
        } else {
            if (origBorder instanceof EtchedBorder) {
                EtchedBorder eb = (EtchedBorder) origBorder;
                if (eb.getHighlightColor() == null ||
                        eb.getShadowColor() == null) {

                    borders.put(origBorder, new NapkinEtchedBorder(eb));
                }
            } else if (origBorder instanceof BevelBorder) {
                BevelBorder bb = (BevelBorder) origBorder;
                if (bb.getHighlightInnerColor() == null ||
                        bb.getHighlightOuterColor() == null ||
                        bb.getShadowInnerColor() == null ||
                        bb.getShadowOuterColor() == null) {

                    borders.put(origBorder, new NapkinBevelBorder(bb));
                }
            }
            result = new NapkinWrappedBorder(origBorder);
        }
        return result;
    }
}
