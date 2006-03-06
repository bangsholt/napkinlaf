// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import napkin.shapes.DrawnBoxHolder;
import napkin.shapes.DrawnScribbleHolder;
import napkin.util.NapkinPainter;
import napkin.NapkinTheme;
import napkin.util.NapkinUtil;

public class NapkinProgressBarUI extends BasicProgressBarUI
        implements NapkinPainter {
    private final DrawnScribbleHolder scribble = new DrawnScribbleHolder();
    private final Rectangle sz = new Rectangle(0, 0, 0, 0);
    /** @noinspection FieldNameHidesFieldInSuperclass */
    private Rectangle boxRect;
    private DrawnBoxHolder box;
    private Image curImage;
    /** @noinspection FieldNameHidesFieldInSuperclass */
    private Color selectionForeground;
    /** @noinspection FieldNameHidesFieldInSuperclass */
    private Color selectionBackground;

    /** @noinspection MethodOverridesStaticMethodOfSuperclass */
    public static ComponentUI createUI(JComponent c) {
        return new NapkinProgressBarUI();
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
            box.setWidth(2f);
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
            NapkinTheme theme = NapkinUtil.currentTheme(c);
            curImage = c.createImage(sz.x + sz.width, sz.y + sz.height);
            Graphics2D imgG = (Graphics2D) curImage.getGraphics();
            selectionForeground = theme.getRadioColor();
            selectionBackground = theme.getCheckColor();
            imgG.setBackground(theme.getBackgroundColor());
            imgG.clearRect(0, 0, sz.x + sz.width, sz.y + sz.height);
            imgG.setColor(theme.getCheckColor());
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
        super.update(g, c);
    }

    protected Color getSelectionBackground() {
        return selectionBackground;
    }

    protected Color getSelectionForeground() {
        return selectionForeground;
    }
}

