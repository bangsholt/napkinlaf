// $Id$

package napkin;

import java.awt.*;
import javax.swing.*;

public interface NapkinTextPainter {
    void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text);
}
