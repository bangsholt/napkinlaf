// $Id: NapkinTextPainter.java 293 2006-03-06 14:18:49Z kcrca $

package net.sourceforge.napkinlaf.util;

import javax.swing.*;
import java.awt.*;

public interface NapkinTextPainter {
    void superPaintText(Graphics g, JComponent c, Rectangle textRect,
            String text);
}
