// $Id$

package napkin.borders;

import java.awt.*;
import javax.swing.border.*;
import napkin.*;
import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

public abstract class NapkinBorder extends AbstractBorder {
    protected final Border formalBorder;
    private boolean recentlyFormal;

    public NapkinBorder(Border formalBorder) {
        this.formalBorder = formalBorder;
    }

    public Insets getBorderInsets(Component c) {
        if (isFormal(c))
            return formalBorder.getBorderInsets(c);
        else
            return doGetBorderInsets(c);
    }

    public boolean isBorderOpaque() {
        // As far as I can tell this is actually unused, but just in case...
        if (recentlyFormal)
            return formalBorder.isBorderOpaque();
        else
            return doIsBorderOpaque();
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {

        if (isFormal(c))
            formalBorder.paintBorder(c, g, x, y, width, height);
        else {
            NapkinTheme theme = NapkinUtil.currentTheme(c);
            Color penColor = theme.getPenColor();
            if (NapkinUtil.replace(g.getColor(), penColor))
                g.setColor(penColor);
            doPaintBorder(c, g, x, y, width, height);
        }
    }

    protected boolean isFormal(Component c) {
        return (recentlyFormal = NapkinUtil.isFormal(c));
    }

    protected abstract Insets doGetBorderInsets(Component c);

    protected boolean doIsBorderOpaque() {
        // default if false, just like for isBorderOpaque()
        return false;
    }

    protected abstract void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height);
}
