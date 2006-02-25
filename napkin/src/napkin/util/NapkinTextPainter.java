// $Id$

package napkin.util;

import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

public interface NapkinTextPainter {
    void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text);
}
