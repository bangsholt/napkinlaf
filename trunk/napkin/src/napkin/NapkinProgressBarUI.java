// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public class NapkinProgressBarUI extends BasicProgressBarUI
        implements NapkinPainter {

    private final DrawnScribbleHolder scribble = new DrawnScribbleHolder();
    private final Rectangle sz = new Rectangle(0, 0, 0, 0);
    private Rectangle boxRect;
    private DrawnBoxHolder box;
    private Image curImage;
    private Color selectionForeground, selectionBackground;

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
            box = new DrawnBoxHolder();
            box.width = 2;
        }
        box.shapeUpToDate(boxRect);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(NapkinUtil.currentTheme(c).getCheckColor());
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
            Graphics2D imgG = (Graphics2D) curImage.getGraphics();
            Composite origComposit;
            origComposit = imgG.getComposite();
            imgG.setComposite(AlphaComposite.Clear);
            imgG.fillRect(0, 0, sz.width, sz.height);
            imgG.setComposite(origComposit);
            imgG.setColor(NapkinUtil.currentTheme(c).getCheckColor());
            scribble.draw(imgG);
        }

        g.drawImage(curImage, 0, 0, c);

        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                    amountFull, b);
        }
    }

    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c, NapkinTheme theme) {
        Color radioColor = theme.getRadioColor();
        if (NapkinUtil.replace(selectionForeground, radioColor))
            selectionForeground = radioColor;
        Color checkColor = theme.getCheckColor();
        if (NapkinUtil.replace(selectionBackground, checkColor))
            selectionBackground = theme.getCheckColor();
        super.update(g, c);
    }

    protected Color getSelectionBackground() {
        return selectionBackground;
    }

    protected Color getSelectionForeground() {
        return selectionForeground;
    }
}

