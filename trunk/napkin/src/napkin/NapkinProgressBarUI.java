// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinProgressBarUI extends BasicProgressBarUI {
    private final ScribbleHolder scribble = new ScribbleHolder();
    private final Rectangle sz = new Rectangle(0, 0, 0, 0);

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinProgressBarUI());
    }

    public void installUI(JComponent c) {
        NapkinUtil.installUI(c);
        super.installUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g);
        c.setForeground(Color.black);
        super.paint(g, c);
    }

    protected void paintDeterminate(Graphics g, JComponent c) {
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        // amount of progress to draw
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

        c.getBounds(sz);
        int orientation = progressBar.getOrientation();
        boolean backwards = !NapkinUtil.isLeftToRight(c);
        scribble.shapeUpToDate(c, sz, orientation, amountFull, backwards);

        g.setColor(NapkinIconFactory.CheckBoxIcon.MARK_COLOR);
        scribble.draw(g);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                    amountFull, b);
        }
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.background(g, c);
        super.update(g, c);
    }
}

