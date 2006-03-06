// $Id$

package napkin.borders;

import java.awt.*;
import javax.swing.border.*;

import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

/** Common work for napkin-style borders */
public abstract class NapkinBorder extends AbstractBorder {
    public NapkinBorder() {
    }

    public Insets getBorderInsets(Component c) {
        return doGetBorderInsets(c);
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {

        NapkinTheme theme = NapkinUtil.currentTheme(c);
        Color penColor = theme.getPenColor();
        if (NapkinUtil.replace(g.getColor(), penColor))
            g.setColor(penColor);
        doPaintBorder(c, g, x, y, width, height);
    }

    protected abstract Insets doGetBorderInsets(Component c);

    protected abstract void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height);
}
