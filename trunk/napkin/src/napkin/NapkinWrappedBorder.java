// $Id$

package napkin;

import java.awt.*;
import javax.swing.border.*;

public class NapkinWrappedBorder extends NapkinBorder {
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
}
