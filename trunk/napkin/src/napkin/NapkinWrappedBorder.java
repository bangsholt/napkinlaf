package napkin;

import java.awt.*;
import javax.swing.border.*;

public class NapkinWrappedBorder extends NapkinBorder {
    private final Border formal;

    public NapkinWrappedBorder(Border formal) {
        super(formal);
        this.formal = formal;
    }

    protected Insets doGetBorderInsets(Component c) {
        return formal.getBorderInsets(c);
    }

    protected boolean doIsBorderOpaque() {
        return formal.isBorderOpaque();
    }

    protected void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {

        g = NapkinUtil.defaultGraphics(g, c);
        formal.paintBorder(c, g, x, y, width, height);
    }

    protected Border getFormalBorder() {
        return formal;
    }
}
