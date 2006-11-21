package net.sourceforge.napkinlaf;

import net.sourceforge.napkinlaf.shapes.DrawnBoxHolder;
import net.sourceforge.napkinlaf.shapes.DrawnScribbleHolder;
import net.sourceforge.napkinlaf.util.NapkinPainter;
import net.sourceforge.napkinlaf.util.NapkinUtil;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.image.*;

@SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass"})
public class NapkinProgressBarUI extends BasicProgressBarUI
        implements NapkinPainter {
    private final DrawnScribbleHolder scribble = new DrawnScribbleHolder();
    private final Rectangle sz = new Rectangle(0, 0, 0, 0);
    private DrawnBoxHolder box;
    private Image curImage;
    private Color selectionForeground;
    private Color selectionBackground;

    @SuppressWarnings({"UnusedParameters"})
    public static ComponentUI createUI(JComponent c) {
        return new NapkinProgressBarUI();
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        NapkinUtil.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        NapkinUtil.uninstallUI(c);
        super.uninstallUI(c);
    }

    @Override
    protected void paintIndeterminate(Graphics g1, JComponent c) {
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        Graphics2D g = (Graphics2D) g1;

        // Paint the bouncing box.
        boxRect = getBox(boxRect);
        if (boxRect == null) {
            return;
        }

        if (box == null) {
            box = new DrawnBoxHolder();
            box.setWidth(2.0f);
        }
        box.shapeUpToDate(boxRect);
        Graphics2D lineG = NapkinUtil.copy(g);
        lineG.setColor(NapkinUtil.currentTheme(c).getCheckColor());
        lineG.translate(boxRect.x, boxRect.y);
        box.draw(lineG);

        // Deal with possible text painting
        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                        boxRect.width, b);
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                        boxRect.height, b);
            }
        }
    }

    @Override
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
            curImage = new BufferedImage(sz.x + sz.width, sz.y + sz.height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D imgG = (Graphics2D) curImage.getGraphics();
            selectionForeground = theme.getRadioColor();
            selectionBackground = theme.getCheckColor();
            imgG.setColor(theme.getCheckColor());
            scribble.draw(imgG);
        }

        g.drawImage(curImage, 0, 0, c);

        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight,
                    amountFull, b);
        }
    }

    @Override
    public void update(Graphics g, JComponent c) {
        NapkinUtil.update(g, c, this);
    }

    public void superPaint(Graphics g, JComponent c) {
        super.update(g, c);
    }

    @Override
    protected Color getSelectionBackground() {
        return selectionBackground;
    }

    @Override
    protected Color getSelectionForeground() {
        return selectionForeground;
    }
}

