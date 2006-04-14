package net.sourceforge.napkinlaf.borders;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;
import java.util.Map;
import java.util.WeakHashMap;

public class NapkinWrappedBorder extends AbstractNapkinBorder {
    private final Border origBorder;

    private NapkinWrappedBorder(Border origBorder) {
        super();
        this.origBorder = origBorder;
    }

    public static NapkinWrappedBorder wrap(Border origBorder) {
        return origBorder instanceof NapkinWrappedBorder ?
            (NapkinWrappedBorder) origBorder :
            new NapkinWrappedBorder(origBorder);
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
}
