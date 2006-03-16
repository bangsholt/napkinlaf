// $Id$

package net.sourceforge.napkinlaf.borders;

import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import java.awt.*;

public class NapkinSelectedBorder extends NapkinBoxBorder {
    @Override
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