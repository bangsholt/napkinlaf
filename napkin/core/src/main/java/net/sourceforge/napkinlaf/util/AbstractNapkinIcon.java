package net.sourceforge.napkinlaf.util;

import net.sourceforge.napkinlaf.NapkinThemeColor;
import net.sourceforge.napkinlaf.shapes.AbstractDrawnGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public abstract class AbstractNapkinIcon implements NapkinIcon {
    protected Shape place;
    protected Shape mark;

    private final NapkinThemeColor markColor;
    private final AffineTransform scaleMat;
    private int width;
    private int height;

    protected AbstractDrawnGenerator placeGen;
    protected AbstractDrawnGenerator markGen;

    public AbstractNapkinIcon(NapkinThemeColor markColor,
            AffineTransform scale) {

        this.markColor = markColor;
        scaleMat = scale;
    }

    protected void init() {
        try {
            markGen = createMarkGenerator();
            placeGen = createPlaceGenerator();
            width = calcWidth();
            height = calcHeight();
        } catch (StackOverflowError e) {
            Thread.dumpStack();
            throw e;
        }
    }

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    public void paintIcon(Component c, Graphics g1, int x, int y) {
        if (place == null) {
            place = placeGen.generate(scaleMat);
        }
        boolean useMark = shouldUseMark(c);

        Graphics2D placeG = NapkinUtil.copy(g1);

        Graphics2D markG = null;
        if (!useMark) {
            mark = null;
        } else {
            if (markGen != null && mark == null) {
                mark = markGen.generate(scaleMat);
            }
            markG = NapkinUtil.lineGraphics(g1, NapkinConstants.CHECK_WIDTH);
            markG.setColor(NapkinUtil.currentTheme(c).getColor(markColor));
        }
        placeG.setColor(c.getForeground());
        doPaint(placeG, markG, x, y);
    }

    protected boolean shouldUseMark(Component c) {
        return c instanceof AbstractButton && ((AbstractButton) c).isSelected();
    }

    protected void
    doPaint(Graphics2D placeG, Graphics2D markG, int x, int y) {
        if (markG != null) {
            markG.translate(x, y);
            markG.fill(mark);
        }

        placeG.translate(x, y);
        placeG.draw(place);
    }

    protected abstract int calcWidth();

    protected abstract int calcHeight();

    protected abstract AbstractDrawnGenerator createPlaceGenerator();

    protected abstract AbstractDrawnGenerator createMarkGenerator();
}
