// $Id$

package napkin;

import java.awt.*;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.border.*;

public class NapkinWrappedBorder extends NapkinBorder {
    private static final Map borders = new WeakHashMap(3);

    public NapkinWrappedBorder(Border formalBorder) {
        super(formalBorder);
    }

    protected Insets doGetBorderInsets(Component c) {
        return formalBorder.getBorderInsets(c);
    }

    protected boolean doIsBorderOpaque() {
        return formalBorder.isBorderOpaque();
    }

    protected void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {

        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.syncWithTheme(formalBorder, c);
        formalBorder.paintBorder(c, g, x, y, width, height);
        NapkinUtil.finishGraphics(g, c);
    }

    protected Border getFormalBorder() {
        return formalBorder;
    }

    public static NapkinWrappedBorder wrap(Border formalBorder) {
    	Object brd = borders.get(formalBorder);
    	// Not all passed borders are wrapped yet...
    	if( brd instanceof NapkinWrappedBorder ) {
        	return (NapkinWrappedBorder) brd;
    	}

        Border toWrap = formalBorder;
        if (formalBorder instanceof EtchedBorder) {
            EtchedBorder eb = (EtchedBorder) formalBorder;
            if (eb.getHighlightColor() == null || eb.getShadowColor() == null) {
                toWrap = new NapkinEtchedBorder(eb);
                borders.put(formalBorder, toWrap);
            }
        }
        if (formalBorder instanceof BevelBorder) {
            BevelBorder bb = (BevelBorder) formalBorder;
            if (bb.getHighlightInnerColor() == null ||
                    bb.getHighlightOuterColor() == null ||
                    bb.getShadowInnerColor() == null ||
                    bb.getShadowOuterColor() == null) {

                toWrap = new NapkinBevelBorder(bb);
                borders.put(formalBorder, toWrap);
            }
        }
        return new NapkinWrappedBorder(toWrap);
    }
}
