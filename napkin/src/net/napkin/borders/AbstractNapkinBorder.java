// $Id$

package napkin.borders;

import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

import javax.swing.border.*;
import java.awt.*;

/** Common work for napkin-style borders. */
public abstract class AbstractNapkinBorder extends AbstractBorder {

    @Override
    public Insets getBorderInsets(Component c) {
        return doGetBorderInsets(c, new Insets(0, 0, 0, 0));
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {

        NapkinTheme theme = NapkinUtil.currentTheme(c);
        Color penColor = theme.getPenColor();
        if (NapkinUtil.replace(g.getColor(), penColor))
            g.setColor(penColor);
        doPaintBorder(c, g, x, y, width, height);
    }

    protected abstract Insets doGetBorderInsets(Component c, Insets insets);

    protected abstract void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height);

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return doGetBorderInsets(c, insets);
    }
}
