package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

public class NapkinInternalFrameTitlePane extends BasicInternalFrameTitlePane {
    private LineHolder line;
    private Rectangle bounds;

    public NapkinInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    protected void paintTitleBackground(Graphics g) {
        g = NapkinUtil.defaultGraphics(g, frame);
        NapkinUtil.background(g, frame);
        if (line == null)
            line = new LineHolder(new CubicGenerator());
        Graphics2D ulG = NapkinUtil.copy(g);
        bounds = getBounds(bounds);
        bounds.x = bounds.y = 0;
        line.shapeUpToDate(bounds, null);
        ulG.translate(0, bounds.height - 2);
        line.draw(ulG);
        NapkinUtil.finishGraphics(g, frame);
    }
}