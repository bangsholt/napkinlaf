// $Id$

package napkin.borders;

import java.awt.*;
import javax.swing.*;
import napkin.*;
import napkin.util.NapkinUtil;

public class NapkinSelectedBorder extends NapkinBoxBorder {
    public void doPaintBorder(Component c, Graphics g, int x, int y,
            int width, int height) {

        Color origC = null;
        try {
            AbstractButton b = (AbstractButton) c;
            if (b.getModel().isSelected()) {
                origC = g.getColor();
                g.setColor(NapkinUtil.currentTheme(c).getSelectionColor());
            }
            super.doPaintBorder(c, g, x, y, width, height);
        } finally {
            if (origC != null)
                g.setColor(origC);
        }
    }
}