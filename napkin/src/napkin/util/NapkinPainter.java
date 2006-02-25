// $Id$

package napkin.util;

import java.awt.Graphics;
import javax.swing.JComponent;
import napkin.NapkinTheme;

public interface NapkinPainter {
    void superPaint(Graphics g, JComponent c, NapkinTheme theme);
}