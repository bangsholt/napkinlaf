// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinProgressBarUI extends BasicProgressBarUI {
    private final ScribbleHolder scribble = new ScribbleHolder();
    private final Rectangle sz = new Rectangle(0, 0, 0, 0);
    private Rectangle boxRect;
    private BoxHolder box;
    private Image curImage;

    public static ComponentUI createUI(JComponent c) {
        return NapkinUtil.uiFor(c, new NapkinProgressBarUI());
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    public void paint(Graphics g, JComponent c) {
        NapkinUtil.defaultGraphics(g, c);
        c.setForeground(Color.black);
        super.paint(g, c);
    }

    protected void paintIndeterminate(Graphics g1, JComponent c) {
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        Graphics2D g = (Graphics2D) g1;

        // Paint the bouncing box.
        boxRect = getBox(boxRect);
        if (boxRect == null)
            return;

        if (box == null) {
            box = new BoxHolder();
            box.width = 2;
        }
        box.shapeUpToDate(boxRect);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(NapkinIconFactory.CheckBoxIcon.MARK_COLOR);
        lineG.translate(boxRect.x, boxRect.y);
        box.draw(lineG);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                        boxRect.width, b);
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                        boxRect.height, b);
            }
        }
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
        if (scribble.shapeUpToDate(c, sz, orientation, amountFull, backwards)) {
            curImage = c.createImage(sz.x + sz.width, sz.y + sz.height);
            Graphics imgG = curImage.getGraphics();
            imgG.setColor(NapkinIconFactory.CheckBoxIcon.MARK_COLOR);
            scribble.draw(imgG);
        }

        g.drawImage(curImage, 0, 0, c);

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

