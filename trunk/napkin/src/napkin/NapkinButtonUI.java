// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinButtonUI extends BasicButtonUI {
    private LineHolder defaultUnderline;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinButtonUI());
    }

    private NapkinButtonUI() {
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        super.paint(g, c);
    }

    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect,
            String text) {

        JButton jb = (JButton) b;
        if (jb.isDefaultButton()) {
            if (defaultUnderline == null)
                defaultUnderline = new LineHolder(new CubicGenerator());
            Graphics2D ulG = NapkinUtil.copy(g);
            FontMetrics fm = ulG.getFontMetrics();
            defaultUnderline.shapeUpToDate(textRect, fm);
            ulG.setColor(Color.red);
            int x = getTextShiftOffset();
            int y = getTextShiftOffset();
            ulG.translate(x, y);
            defaultUnderline.draw(ulG);
        }

        super.paintText(g, b, textRect, text);
    }
}

