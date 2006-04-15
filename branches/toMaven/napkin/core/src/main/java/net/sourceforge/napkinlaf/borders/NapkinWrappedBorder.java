/*
 * NapkinWrappedBorder.java
 *
 * Created on 15 April 2006, 00:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sourceforge.napkinlaf.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import net.sourceforge.napkinlaf.util.NapkinUtil;

/**
 *
 * @author Alex Lam Sze Lok
 */
public class NapkinWrappedBorder extends AbstractNapkinBorder {

    private final Border origBorder;
    private final AbstractBorder border;

    public NapkinWrappedBorder(Border origBorder) {
        super();
        this.origBorder = origBorder;
        border = origBorder instanceof AbstractBorder ?
            (AbstractBorder) origBorder : null;
    }

    protected Insets doGetBorderInsets(Component c, Insets insets) {
        Insets result = origBorder.getBorderInsets(c);
        insets.set(result.top, result.left, result.bottom, result.right);
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return origBorder.isBorderOpaque();
    }

    protected void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {

        g = NapkinUtil.defaultGraphics(g, c);
        NapkinUtil.syncWithTheme(origBorder, c);
        origBorder.paintBorder(c, g, x, y, width, height);
        NapkinUtil.finishGraphics(g, c);
    }

    @Override
    public Rectangle getInteriorRectangle(Component c, int x, int y, int width, int height) {
        return border != null ?
            border.getInteriorRectangle(c, x, y, width, height) :
            super.getInteriorRectangle(c, x, y, width, height);
    }

    @Override
    public int getBaseline(Component c, int width, int height) {
        return border != null ? border.getBaseline(c, width, height) :
            super.getBaseline(c, width, height);
    }

    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component c) {
        return border != null ? border.getBaselineResizeBehavior(c) :
            super.getBaselineResizeBehavior(c);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NapkinWrappedBorder &&
            ((NapkinWrappedBorder) obj).origBorder.equals(origBorder);
    }

    @Override
    public int hashCode() {
        return NapkinWrappedBorder.class.hashCode() ^ origBorder.hashCode();
    }

    public String toString() {
        return "NapkinWrappedBoreder{" + origBorder.toString() + "}";
    }
}
