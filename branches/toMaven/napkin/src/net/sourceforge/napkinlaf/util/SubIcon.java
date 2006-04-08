package net.sourceforge.napkinlaf.util;

import javax.swing.*;
import java.awt.*;

public class SubIcon implements Icon {
    private final Icon icon;
    private final int x, y;
    private final int width, height;
    private Icon subIcon;

    public SubIcon(Icon icon, int x, int y, int width, int height) {
        this.icon = icon;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (x == 0 && y == 0 && width == icon.getIconWidth() &&
                height == icon.getIconHeight()) {
            subIcon = icon;
        }
    }

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations may use
     * the Component argument to get properties useful for painting, e.g. the
     * foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int atX, int atY) {
        if (width == 0 || height == 0)
            return;
        if (subIcon == null) {
            Image img = c.createImage(width, height);
            Graphics ig = img.getGraphics();
            icon.paintIcon(c, ig, -x, -y);
            subIcon = new ImageIcon(img);
        }
        subIcon.paintIcon(c, g, atX, atY);
    }
}
