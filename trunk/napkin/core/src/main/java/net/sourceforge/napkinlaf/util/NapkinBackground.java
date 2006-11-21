package net.sourceforge.napkinlaf.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * This class describes a background.  It may be as simple as a color, or have
 * edges that differ from a tiled middle.  The model is that the background
 * consists of the following parts:
 * <p/>
 * <table align="center" border="1"> <tr> <td>Top<br>left<br>corner <td
 * align="center">Top side <td>Top<br>right<br>corner <tr> <td>Left<br>side <td
 * width="200" height="200" align="center">Middle <td>Right<br>side <tr>
 * <td>Bottom<br>left<br>corner <td align="center">Bottom side
 * <td>Bottom<br>right<br>corner </table>
 * <p/>
 * This model is used in two ways.  The first is to take the provided image and
 * break it up into these parts.  When you create a <tt>NapkinBackground</tt>,
 * you provide widths for the left and right sides, and height for the top and
 * bottom sides.  From these the corners can be inferred.  The parts of the
 * image that are so described become the relevant parts of the image that are
 * used in drawing the background.
 * <p/>
 * The second is to draw the background. Each of the corners will appear once.
 * Each side will be tiled to fill out the area between the side's corners. The
 * middle part of the image will be tiled to fill up the remaining area in the
 * middle of the background (if any).
 */
@SuppressWarnings({"WeakerAccess"})
public class NapkinBackground {
    private final String name;
    private final ImageIcon icon;
    private final Icon tlCorner, tSide, trCorner;
    private final Icon rSide, middle, lSide;
    private final Icon blCorner, bSide, brCorner;

    private static final Insets NO_INSETS = new Insets(0, 0, 0, 0);

    /**
     * Creates a background with the given image. This has no outer parts, only
     * a middle.
     *
     * @param icon The background image.
     */
    public NapkinBackground(ImageIcon icon) {
        this(icon, null);
    }

    /**
     * Creates a background with the specified insets for the given sides.
     *
     * @param icon   The background image.
     * @param top    The height of the top side and corners.
     * @param left   The width of the left side and corners.
     * @param bottom The height of the bottom side and corners.
     * @param right  The width of the right side and corners.
     */
    public NapkinBackground(ImageIcon icon, int top, int left, int bottom,
            int right) {

        this(icon, new Insets(top, left, bottom, right));
    }

    /**
     * Creates a background with the insert's sizes for the given sides.
     *
     * @param icon   The background image.
     * @param insets The inset describing the sizes; <tt>top</tt> is the height
     *               of the top side and corners; <tt>left</tt> is the width of
     *               the left side and corners; <tt>bottom</tt> is the height of
     *               the bottom side and corners; <tt>right</tt> is the width of
     *               the right side and corners. If <tt>null</tt>, all these
     *               values will be zero.
     */
    @SuppressWarnings({"WeakerAccess"})
    public NapkinBackground(ImageIcon icon, Insets insets) {
        this.icon = icon;
        this.name = icon.getDescription();
        int iconW = icon.getIconWidth();
        int iconH = icon.getIconHeight();

        if (insets == null) {
            insets = NO_INSETS;
        }

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

    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "NapkinBackground(\"" + name + "\")";
    }

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    public void paint(Component c, Graphics g, Rectangle paper, Rectangle comp,
            Insets cInsets) {

        int topH = tlCorner.getIconHeight();
        int topY = 0;

        int botH = blCorner.getIconHeight();
        int botY = paper.height - botH;

        int midH = paper.height - (topH + botH);
        int midY = topH;

        paintSliceAcross(c, g, paper, comp, cInsets, topY, topH, tlCorner,
                tSide, trCorner);
        paintSliceAcross(c, g, paper, comp, cInsets, midY, midH, lSide, middle,
                rSide);
        paintSliceAcross(c, g, paper, comp, cInsets, botY, botH, blCorner,
                bSide, brCorner);
    }

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    private static void paintSliceAcross(Component c, Graphics g,
            Rectangle paper, Rectangle comp, Insets cInsets, int bandY,
            int bandH, Icon lftIcon, Icon midIcon, Icon rgtIcon) {

        if (bandH != 0 && comp.y + comp.height >= bandY &&
                comp.y < bandY + bandH) {

            int lftW = lftIcon.getIconWidth();
            int lftX = 0;

            int midW = paper.width - (lftW + rgtIcon.getIconWidth());
            int midX = lftW;

            int rgtW = rgtIcon.getIconWidth();
            int rgtX = paper.width - rgtW;

            paintArea(c, g, comp, lftX, bandY, lftW, bandH, lftIcon, cInsets);
            paintArea(c, g, comp, midX, bandY, midW, bandH, midIcon, cInsets);
            paintArea(c, g, comp, rgtX, bandY, rgtW, bandH, rgtIcon, cInsets);
        }
    }

    private static void paintArea(Component c, Graphics g, Rectangle comp,
            int atX, int atY, int w, int h, Icon icon, Insets cInsets) {

        if (w != 0 && h != 0 && comp.x + comp.width >= atX &&
                comp.x < atX + w) {

            // at this point we deal with the fact that the Graphics object is
            // translated to the origin of the component, with insets outside
            atX -= comp.x + cInsets.left;
            atY -= comp.y + cInsets.top;
            Rectangle cZeroed = new Rectangle(-cInsets.left, -cInsets.top,
                    comp.width, comp.height);

            int endX = atX + w;
            int endY = atY + h;

            int iw = icon.getIconWidth();
            int ih = icon.getIconHeight();
            Rectangle area = new Rectangle(atX, atY, w, h);
            for (area.x = atX; area.x < endX; area.x += iw) {
                if (area.x + iw < 0) {
                    continue;
                }
                for (area.y = atY; area.y < endY; area.y += ih) {
                    if (area.intersects(cZeroed)) {
                        icon.paintIcon(c, g, area.x, area.y);
                    }
                }
            }
        }
    }

    public Color getMeanColor() {
        Image iconImg = icon.getImage();
        int w = iconImg.getWidth(null);
        int h = iconImg.getHeight(null);
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(iconImg, 0, 0, null);
        int rSum = 0, gSum = 0, bSum = 0;
        int[] colors = img.getRGB(0, 0, w, h, null, 0, w);
        for (int color : colors) {
            rSum += (color >> 16) & 0xFF;
            gSum += (color >> 8) & 0xFF;
            bSum += (color) & 0xFF;
        }
        return new Color(rSum / colors.length, gSum / colors.length,
                bSum / colors.length);
    }
}
