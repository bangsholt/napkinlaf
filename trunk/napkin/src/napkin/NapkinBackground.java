
package napkin;

import java.awt.*;
import javax.swing.*;

public class NapkinBackground {
    private final Icon icon;
    private final int iconW, iconH;
    private final Icon tlCorner, tSide, trCorner;
    private final Icon rSide, middle, lSide;
    private final Icon blCorner, bSide, brCorner;

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

    public static final NapkinBackground NAPKIN_BG =
            new NapkinBackground("resources/napkin.jpg");
    public static final NapkinBackground[] POSTITS = {
        new NapkinBackground("resources/postit01.jpg", 15, 15, 38, 32),
        new NapkinBackground("resources/postit00.jpg", 38, 20, 100, 83),
    };
    public static final NapkinBackground POSTIT_BG = POSTITS[0];

    public NapkinBackground(String name) {
        this(name, null);
    }

    public NapkinBackground(String name, int top, int left, int bottom,
            int right) {
        this(name, new Insets(top, left, bottom, right));
    }

    public NapkinBackground(String name, Insets insets) {
        icon = new ImageIcon(getClass().getResource(name));
        iconW = icon.getIconWidth();
        iconH = icon.getIconHeight();

        if (insets == null)
            insets = NO_INSETS;

        int rX = iconW - insets.right;
        int bY = iconH - insets.bottom;
        int midW = rX - insets.left;
        int midH = bY - insets.top;

        tlCorner = new SubIcon(icon, 0, 0, insets.left, insets.top);
        tSide = new SubIcon(icon, insets.left, 0, midW, insets.top);
        trCorner = new SubIcon(icon, rX, 0, insets.right, insets.top);

        lSide = new SubIcon(icon, 0, insets.top, insets.left, midH);
        middle = new SubIcon(icon, insets.left, insets.top, midW, midH);
        rSide = new SubIcon(icon, rX, insets.top, insets.right, midH);

        blCorner = new SubIcon(icon, 0, bY, insets.left, insets.bottom);
        bSide = new SubIcon(icon, insets.left, bY, midW, insets.bottom);
        brCorner = new SubIcon(icon, rX, bY, insets.right, insets.bottom);
    }

    public void paint(Component c, Graphics g, int w, int h) {
        paint(c, g, 0, 0, w, h);
    }

    public void
            paint(Component c, Graphics g, int xOff, int yOff, int w, int h) {

        int topH = tlCorner.getIconHeight();
        paintSliceAcross(c, g, 0, w, topH, xOff, yOff, tlCorner, tSide,
                trCorner);

        int midH = h - (tSide.getIconHeight() + bSide.getIconHeight());
        paintSliceAcross(c, g, topH, w, midH, xOff, yOff, lSide, middle, rSide);

        int botH = blCorner.getIconHeight();
        paintSliceAcross(c, g, h - botH, w, botH, xOff, yOff, blCorner, bSide,
                brCorner);
    }

    private void paintSliceAcross(Component c, Graphics g1, int y, int w,
            int h, int xOff, int yOff, Icon left, Icon mid, Icon right) {

        if (yOff + h <= y)
            return;

        int lw = left.getIconWidth();
        paintArea(c, g1, left, 0, y, lw, h, xOff, yOff);

        int midW = w - (lw + right.getIconWidth());
        paintArea(c, g1, mid, lw, y, midW, h, xOff, yOff);

        int rX = w - right.getIconWidth();
        paintArea(c, g1, right, rX, y, right.getIconWidth(), h, xOff, yOff);
    }

    private void paintArea(Component c, Graphics g1, Icon icon, int atX,
            int atY, int w, int h, int xOff, int yOff) {

        if (w == 0 || h == 0)
            return;

        int endX = atX + w;
        int endY = atY + h;

        atX += xOff;
        atY += yOff;

        int iw = icon.getIconWidth();
        int ih = icon.getIconHeight();
        for (int x = atX; x < endX; x += iw) {
            if (x + iw < 0)
                continue;
            for (int y = atY; y < endY; y += ih) {
                if (y + ih >= 0) {
//                    if (c.getClass() == JLabel.class)
//                        System.out.println("(" + x + ", " + y + ") " + iw + " x " + ih);
                    icon.paintIcon(c, g1, x, y);
                }
            }
        }
    }
}