package net.sourceforge.napkinlaf.borders;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;

/** @author Alex Lam Sze Lok */
@SuppressWarnings({"NonSerializableFieldInSerializableClass"})
public class NapkinWrappedBorder extends AbstractNapkinBorder {

    private final Border origBorder;
    private final AbstractBorder border;

    public NapkinWrappedBorder(Border origBorder) {
        this.origBorder = origBorder;
        border = origBorder instanceof AbstractBorder ?
                (AbstractBorder) origBorder :
                null;
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

    @Override
    public Rectangle getInteriorRectangle(Component c, int x, int y, int width,
            int height) {
        return border != null ? border.getInteriorRectangle(c, x, y, width,
                height) : super.getInteriorRectangle(c, x, y, width, height);
    }

    // These overrides are for Mustang (1.6), and won't compile under 1.5
    // TODO: Figure out a way to make this conditional -- two versions of the
    // class file maybe?
//    @Override
//    public int getBaseline(Component c, int width, int height) {
//        return border != null ? border.getBaseline(c, width, height) :
//            super.getBaseline(c, width, height);
//    }
//
//    @Override
//    public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component c) {
//        return border != null ? border.getBaselineResizeBehavior(c) :
//            super.getBaselineResizeBehavior(c);
//    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NapkinWrappedBorder &&
                ((NapkinWrappedBorder) obj).origBorder.equals(origBorder);
    }

    @Override
    public int hashCode() {
        return NapkinWrappedBorder.class.hashCode() ^ origBorder.hashCode();
    }

    @Override
    public String toString() {
        return "NapkinWrappedBoreder{" + origBorder.toString() + "}";
    }
}
