// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

public class NapkinInternalFrameTitlePane extends BasicInternalFrameTitlePane
        implements NapkinConstants {

    private DrawnLineHolder line;
    private Rectangle bounds;

    public NapkinInternalFrameTitlePane(JInternalFrame f) {
        super(f);
        putClientProperty(THEME_TOP_KEY, f);
    }

    public void paint(Graphics g) {
        g = NapkinUtil.defaultGraphics(g, this, frame);
        selectedTextColor = notSelectedTextColor = g.getColor();
        NapkinUtil.background(g, this);
        super.paint(g);
        NapkinUtil.finishGraphics(g, this);
    }

    protected void paintTitleBackground(Graphics g) {
        if (line == null)
            line = new DrawnLineHolder(new DrawnCubicLineGenerator());
        Graphics2D ulG = NapkinUtil.copy(g);
        bounds = getBounds(bounds);
        bounds.x = bounds.y = 0;
        line.shapeUpToDate(bounds, null);
        ulG.translate(0, bounds.height - 2);
        line.draw(ulG);
    }
}
