// $Id$

package napkin.util;

import javax.swing.*;
import java.awt.*;

public interface NapkinTextPainter {
    void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text);
}
