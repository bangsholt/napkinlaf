
package napkin;

import java.awt.*;
import javax.swing.*;

public class NapkinBackgroundLabel extends JLabel {
    private Icon bgIcon;
    private Icon tlCorner, tSide, trCorner;
    private Icon rSide, middle, lSide;
    private Icon blCorner, bSide, brCorner;
    private int iconW, iconH;

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

    public NapkinBackgroundLabel(NapkinBackground bg) {
        setBounds(0, 0, 50000, 50000);
        setBackgroundIcon(bg.icon);
        setInsets(bg.insets);
    }

    private void setBackgroundIcon(Icon backgroundIcon) {
        this.bgIcon = backgroundIcon;
        iconW = backgroundIcon.getIconWidth();
        iconH = backgroundIcon.getIconHeight();
    }

    private void setInsets(Insets insets) {
        if (insets == null)
            insets = NO_INSETS;

        int rX = iconW - insets.right;
        int bY = iconH - insets.bottom;
        int midW = rX - insets.left;
        int midH = bY - insets.top;

        tlCorner = new SubIcon(this, bgIcon, 0, 0, insets.left, insets.top);
        tSide = new SubIcon(this, bgIcon, insets.left, 0, midW, insets.top);
        trCorner = new SubIcon(this, bgIcon, rX, 0, insets.right, insets.top);

        lSide = new SubIcon(this, bgIcon, 0, insets.top, insets.left, midH);
        middle =
                new SubIcon(this, bgIcon, insets.left, insets.top, midW, midH);
        rSide = new SubIcon(this, bgIcon, rX, insets.top, insets.right, midH);

        blCorner =
                new SubIcon(this, bgIcon, 0, bY, insets.left, insets.bottom);
        bSide =
                new SubIcon(this, bgIcon, insets.left, bY, midW, insets.bottom);
        brCorner =
                new SubIcon(this, bgIcon, rX, bY, insets.right, insets.bottom);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getParent().getWidth();
        int h = getParent().getHeight();

        g.setColor(Color.white);
        g.fillRect(0, 0, 5000, 5000);

        int topH = tlCorner.getIconHeight();
        paintSliceAcross(g, 0, w, topH, tlCorner, tSide, trCorner);

        int midH = h - (tSide.getIconHeight() + bSide.getIconHeight());
        paintSliceAcross(g, topH, w, midH, lSide, middle, rSide);

        int botH = blCorner.getIconHeight();
        paintSliceAcross(g, h - botH, w, botH, blCorner, bSide, brCorner);
    }

    private void paintSliceAcross(Graphics g1, int y, int w, int h, Icon left,
            Icon side, Icon right) {

        if (left.getIconHeight() == 0)
            return;

        int lw = left.getIconWidth();
        paintArea(g1, left, 0, y, lw, h);

        int midW = w - (lw + right.getIconWidth());
        paintArea(g1, side, lw, y, midW, h);

        int rX = w - right.getIconWidth();
        paintArea(g1, right, rX, y, right.getIconWidth(), h);
    }

    private void paintArea(Graphics g1, Icon icon, int atX, int atY, int width,
            int height) {

        if (width == 0 || height == 0)
            return;

        int endX = atX + width;
        int endY = atY + height;
        int iw = icon.getIconWidth();
        int ih = icon.getIconHeight();
        for (int x = atX; x < endX; x += iw)
            for (int y = atY; y < endY; y += ih)
                icon.paintIcon(this, g1, x, y);
    }

    public Dimension getPreferredSize() {
        return new Dimension(super.getSize());
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}